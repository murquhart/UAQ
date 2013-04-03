package edu.psu.msu5001.rbac;

import java.io.Reader;
import java.util.*;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.InstanceReader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

public class Uaq {
	
	private static Uaq uaq = null;
	private Policy policy;
	private HashSet<int []> sodClauses = new HashSet<int []>();
	
	private Uaq() {
		
	}
	private Uaq(Policy policy) {
		setPolicy(policy);
		getSodClauses();
		
	}
	
	public static Uaq getInstance() {
		if (uaq==null) throw new NullPointerException();
		return uaq;
	}
	
	public static Uaq getInstance(Policy policy) {
		if (uaq==null) uaq = new Uaq(policy);
		return uaq;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}
	
	private HashSet<int []> getSodClauses() {
		
		/* Generate SoD clauses */
		HashSet<Sod> sodSet = policy.getSodSet();
		for (Sod sod : sodSet) {
			int t = sod.get_t();
			HashSet<Role> sodRoles = sod.getRoles();
			Set<Set<Role>> roleSets = enumerateSubsets(sodRoles, t); 
			
			for (Set<Role> roleSet : roleSets) {
				int[] cnf = new int[roleSet.size()];
				int i = 0;
				for (Role role : roleSet) {
					cnf[i] = -role.getId();
					i++;
				}
				sodClauses.add(cnf);
			}
		}
		
		return sodClauses;
	}
	
	public int[] doRequest(Request request, Requester requester) {
		
		HashSet<Permission> permissions = request.getPermissons();
		int numClauses = permissions.size();
		int maxVar = policy.getRoleTable().size();
		
		ISolver solver = new ModelIterator(SolverFactory.newDefault());
		solver.newVar(maxVar);
		solver.setExpectedNumberOfClauses(numClauses);
		
		
		System.out.println("\ncnf: ");
		
		/* Generate RBAC clauses */
		for (Permission permission : permissions) {
			HashSet<Role> roles = permission.getRoles();
			roles.retainAll(requester.getRoles());
			int[] cnf = new int[roles.size()];
			int i = 0;
			for (Role role : roles) {
				cnf[i] = role.getId();
				System.out.print(cnf[i] + " ");
				i++;
			}
			System.out.println();
			try {
				solver.addClause(new VecInt(cnf));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}
		}
		
		/* Generate SoD clauses */
		
		for (int [] cnf : sodClauses) {
			try {
				solver.addClause(new VecInt(cnf));
				for (int i = 0; i < cnf.length; i++) System.out.print(cnf[i] + " ");
				System.out.println();
			} catch (ContradictionException e) {
			}
		}
		
		System.out.println();
		
		IProblem problem = solver;
		int [] model = {0};
		try {
			int modelPermSize = -1;
			
			while (problem.isSatisfiable()) {
				int [] bufModel = problem.model();
				
				HashSet<Permission> permissionsActivated = new HashSet<Permission>();
				for(int i : bufModel) { 
					if (i > 0) {
						Role bufRole = policy.getRoleTable().get(i);
						permissionsActivated.addAll(bufRole.getRolePermissions());
					}
				}
				
				int bufPermSize = permissionsActivated.size();
				
				
				if (modelPermSize == -1 || bufPermSize < modelPermSize) {
					
					model = bufModel;
					modelPermSize = bufPermSize;
				}	
			}
			
			return model;
			
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
			
		 
		return null;
	}
	
	private <E> Set<Set<E>> addElementToSubsets(E e, Set<Set<E>> set) {
		for (Set<E> subset : set) subset.add(e);
		return set;
	}
	
	private <E> Set<Set<E>> enumerateSubsets(Set<E> set, int subsetSize) {
		Set<Set<E>> sets = new HashSet<Set<E>>(); 
		
		if (subsetSize == 1)
			for (E e : set) {
				Set<E> tmp = new HashSet<E>();
				tmp.add(e);
				sets.add(tmp);
			}
		
		else {
			
			for(E e : set) {
				Set<E> subset = new HashSet<E>();
				subset.addAll(set);
				subset.remove(e);
				sets.addAll(addElementToSubsets(e, enumerateSubsets(subset, subsetSize-1)));
			}
		}
		
		return sets;
	}

}

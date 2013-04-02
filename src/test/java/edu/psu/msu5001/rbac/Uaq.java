package edu.psu.msu5001.rbac;

import java.util.*;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class Uaq {
	
	private static Uaq uaq = null;
	private Policy policy;
	
	private Uaq() {
		
	}
	private Uaq(Policy policy) {
		setPolicy(policy);
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
	
	public int[] doRequest(Request request, Requester requester) {
		
		HashSet<Permission> permissions = request.getPermissons();
		int numClauses = permissions.size();
		int maxVar = policy.getRoleTable().size();
		
		ISolver solver = SolverFactory.newDefault();
		solver.newVar(maxVar);
		solver.setExpectedNumberOfClauses(numClauses);
		
		/* Generate RBAC clauses */
		for (Permission permission : permissions) {
			HashSet<Role> roles = permission.getRoles();
			roles.retainAll(requester.getRoles());
			int[] cnf = new int[roles.size()];
			int i = 0;
			for (Role role : roles) {
				cnf[i] = role.getId();
				//System.out.println(cnf[i]);
				i++;
			}
			try {
				solver.addClause(new VecInt(cnf));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}
		}
		
		/* Generate SoD clauses */
		HashSet<Sod> sodSet = policy.getSodSet();
		for (Sod sod : sodSet) {
			int t = sod.get_t();
			HashSet<Role> sodRoles = sod.getRoles();
			Set<Set<Role>> roleSets = enumerateSubsets(sodRoles, t); 
			
			for (Set<Role> roleSet : roleSets) {
				int[] cnf = new int[t];
				int i = 0;
				for (Role role : roleSet) {
					cnf[i] = -role.getId();
					i++;
				}
				try {
					solver.addClause(new VecInt(cnf));
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
			
			
		}
			
		IProblem problem = solver;
		try {
			if (problem.isSatisfiable()) {
				//do something here
				return problem.model();

			}
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
		
		else
			for(E e : set) {
				set.remove(e);
				sets.addAll(addElementToSubsets(e, enumerateSubsets(set, subsetSize-1)));
			}
		
		return sets;
	}

}

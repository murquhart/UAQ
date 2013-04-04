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
	private HashMap<Permission, int []> rbacClauseMap= null;
	private ISolver solver;
	private HashSet<Integer> validRoleIds;
	public static final int MINIMIZE_PERMISSIONS = -1;
	public static final int MAXIMIZE_PERMISSIONS = 1;
	public static final int ANY_PERMISSIONS = 0;
	public static final int MINIMIZE_ROLES = -1;
	public static final int MAXIMIZE_ROLES = 1;
	public static final int ANY_ROLES = 0;
	private int [] model;
	

	private Uaq() {
		
	}
	private Uaq(Policy policy) {
		setPolicy(policy);
		generateSodClauses();
	}
	
	public static Uaq getInstance() {
		if (uaq==null) throw new NullPointerException();
		return uaq;
	}
	
	public static Uaq getInstance(Policy policy) {
		if (uaq==null) uaq = new Uaq(policy);
		return uaq;
	}

	public Collection<int []> getRbacClauses() {
		return rbacClauseMap.values();
	}
	
	public HashSet<int []> getSodClauses() {
		return sodClauses;
	}
	
	public int [] getModel() {
		return model;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}
	
	private HashSet<int []> generateSodClauses() {
		
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
	
	public int [] minRequest(Request request, Requester requester) {
		
		if (!requestIsSatisfiable(request, requester)) {
			int [] nonSat = {0};
			return nonSat;
		}
		
		HashSet<Permission> permissions = request.getPermissions();
		int numClauses = permissions.size();
		int maxVar = policy.getRoleTable().size();
		
		int [] model = {0};
		int counter = 0;
		
		while (model[0] == 0) {
			ISolver solver = SolverFactory.newDefault();
			//solver.newVar(maxVar);
			//solver.setExpectedNumberOfClauses(numClauses);
			HashSet<Integer> validRoleIds = new HashSet<Integer>();
			HashSet<Integer> skipRoles = new HashSet<Integer>();
			
			System.out.println("\ncnf: ");
			
			/* Generate RBAC clauses */
			for (Permission permission : permissions) {
				HashSet<Role> roles = permission.getRoles();
				
				roles.retainAll(requester.getRoles());
				
				for (Role role : roles) validRoleIds.add(role.getId());
				
				HashSet<Role> bufRoles = new HashSet<Role>();
				bufRoles.addAll(roles);
				for (Role role : bufRoles) {
					role.removePermissions(permissions);
					if (role.getRolePermissions().size() > counter) {
						/*roles.remove(role);
						System.out.println("Role_" + role.getId() + " removed 2");
						skipRoles.add(role.getId());*/
						validRoleIds.remove(role.getId());
					}
					/*
					else {
						validRoleIds.add(role.getId());
						validRoleIds.toArray();
					}*/
					
				}
				
				//int[] cnf = new int[roles.size()];
				int [] cnf = new int[validRoleIds.size()];
				int i = 0;
				for (Integer id : validRoleIds) {
					cnf[i] = id;
					System.out.print(cnf[i] + " ");
					i++;
				}
				/*
				int i = 0;
				for (Role role : roles) {
					cnf[i] = role.getId();
					System.out.print(cnf[i] + " ");
					i++;
				}*/
				System.out.println();
				try {
					solver.addClause(new VecInt(cnf));
				} catch (ContradictionException e) {
				}
			}
			
			/* Generate SoD clauses */
			
			for (int [] cnf : sodClauses) {
				try {
					boolean validClause = true;
					for (int i = 0; i < cnf.length; i++) if (!validRoleIds.contains(-cnf[i])) validClause = false;
					
					if (validClause) {
						solver.addClause(new VecInt(cnf));
						for (int i = 0; i < cnf.length; i++) System.out.print(cnf[i] + " ");
						System.out.println();
					}
					//for (int i = 0; i < cnf.length; i++) System.out.print(cnf[i] + " ");
				} catch (ContradictionException e) {
				}
			}
			System.out.println();
			
			
			IProblem problem = solver;
			try {
				if (problem.isSatisfiable()) {
					model = problem.model();
					System.out.println("Tried " + (counter+1) + " models");
					return model;
				}
				
			} catch (TimeoutException e) {
			}
			counter++;
		}

		return model;
	}

	private void setSolver() {
		solver = SolverFactory.newDefault();
		
		for (int [] cnf : rbacClauseMap.values()) {
			try {
				solver.addClause(new VecInt(cnf));
			} catch (ContradictionException e) {
			}
		}
		
		/* Generate SoD clauses */
		
		for (int [] cnf : sodClauses) {
			try {
				boolean skipClause = false;
				for (int i = 0; i < cnf.length; i++) if (!validRoleIds.contains(cnf[i])) skipClause = true;
				
				if (!skipClause) solver.addClause(new VecInt(cnf));
				
			} catch (ContradictionException e) {
			}
		}
	}
	

	private boolean generateRbacClauses(Request request, Requester requester) {
		HashSet<Permission> permissions = request.getPermissions();
		HashMap<Permission, int []> clauseMap = new HashMap<Permission, int []>();
		validRoleIds = new HashSet<Integer>();
		
		for (Permission permission : permissions) {
			HashSet<Role> roles = permission.getRoles();
			
			/*
			 * Intersection of roles able to satisfy a permission and roles the requester has available
			 */
			roles.retainAll(requester.getRoles());
			
			/*
			 * Permission cannot possibly be satisfied if roles is empty
			 */
			if (roles.isEmpty()) return false;
			
			int [] clause = new int [roles.size()];
			int i = 0;
			for (Role role : roles) {
				validRoleIds.add(role.getId());
				clause[i] = role.getId();
				i++;
			}
			clauseMap.put(permission, clause);
		}
		rbacClauseMap = clauseMap;
		return true;
	}
	
	private boolean solve() {
		setSolver();
		
		IProblem problem = solver;
		try {
			if (problem.isSatisfiable()) {
				model = problem.model();
				return true;
			}
			
		} catch (TimeoutException e) {
		}
		
		return false;
	}
	
	private boolean requestIsSatisfiable(Request request, Requester requester) {
		//HashSet<Permission> permissions = request.getPermissons();
		/*int numClauses = permissions.size();
		int maxVar = policy.getRoleTable().size();
		
		ISolver solver = SolverFactory.newDefault();
		solver.newVar(maxVar);
		solver.setExpectedNumberOfClauses(numClauses);*/
		
		/* Generate RBAC clauses */
		/*for (Permission permission : permissions) {
			HashSet<Role> roles = permission.getRoles();
			roles.retainAll(requester.getRoles());
			
			int[] cnf = new int[roles.size()];
			int i = 0;
			for (Role role : roles) {
				cnf[i] = role.getId();
				i++;
			}
			try {
				solver.addClause(new VecInt(cnf));
			} catch (ContradictionException e) {
			}
		}*/
		
		if (!generateRbacClauses(request, requester)) return false;
		
		return solve();
	}
	
	private void setValidRoleIds() {
		HashSet<Integer> newSet = new HashSet<Integer>();
		for (Permission permission : rbacClauseMap.keySet()) {
			for (Integer i : rbacClauseMap.get(permission)) newSet.add(i);
		}
		validRoleIds = newSet;
	}
	
	private void minimizePermissions() {
		
		boolean unsatisfiable;
		
		Set<Permission> requestPermissions = rbacClauseMap.keySet();
		
		
		for (Permission permission : rbacClauseMap.keySet()) {
			int [] cnf = rbacClauseMap.get(permission);
			HashSet<Integer> cnfSet = new HashSet<Integer>();
			unsatisfiable = true;
			
			int extraPerms = 0;
			
			while (unsatisfiable) {
				for (int i = 0; i < cnf.length; i++) {
					HashSet<Permission> permissions = new HashSet<Permission>();
					permissions.addAll(policy.getRoleTable().get(cnf[i]).getRolePermissions());
					permissions.removeAll(requestPermissions);
					if (permissions.size() <= extraPerms) {
						cnfSet.add(cnf[i]);
					}
				}
				
				if (cnfSet.isEmpty()) {
					extraPerms++;
					continue;
				}
				
				int [] cnfBuf = new int[cnfSet.size()];
				int i = 0;
				for (Integer j : cnfSet) {
					cnfBuf[i] = j;
					i++;
				}
				rbacClauseMap.put(permission, cnfBuf);
				setValidRoleIds();
				unsatisfiable = !solve();
				extraPerms++;
			}
		}
		
	
	}
	
	public int[] doRequest(Request request, Requester requester, int permObj, int roleObj) {
		
		int [] nonSat = {0};
		if (!requestIsSatisfiable(request, requester)) return nonSat;
		
		if (permObj == MINIMIZE_PERMISSIONS) {
			minimizePermissions();
		}
		
		else if (permObj == MAXIMIZE_PERMISSIONS) {
			
		}
		
		return model;
		
		
		
		/*
		HashSet<Permission> permissions = request.getPermissons();
		int numClauses = permissions.size();
		int maxVar = policy.getRoleTable().size();
		
		ISolver solver = new ModelIterator(SolverFactory.newDefault());
		solver.newVar(maxVar);
		solver.setExpectedNumberOfClauses(numClauses);
		
		
		System.out.println("\ncnf: ");
		
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
			
			System.out.println("Now solving SAT...");
			
			long elapsedTime = System.nanoTime();
			int solCounter = 0;

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
				
				double dblTime = (System.nanoTime() - elapsedTime)/1000000000.0;
				
				if (dblTime > .001) {
					System.out.println("Current minPermissions: " + modelPermSize);
					System.out.println("loop took: " + dblTime + " seconds");
				}
				elapsedTime = System.nanoTime();
				solCounter++;
			}
			System.out.println("There were " + solCounter + " satisfiable models.");
			
			
			return model;
			
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
			
		 
		return null;
		*/
	}
	
	private static <E> Set<Set<E>> addElementToSubsets(E e, Set<Set<E>> set) {
		for (Set<E> subset : set) subset.add(e);
		return set;
	}
	
	private static <E> Set<Set<E>> enumerateSubsets(Set<E> set, int subsetSize) {
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

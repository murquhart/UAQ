package edu.psu.msu5001.rbac.gen;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;

import edu.psu.msu5001.rbac.*;

public class PolicyGenerator {
	
	public static Policy randomPolicy(int numRoles, int roleDepth, int numPermissions, int maxPermPerRole) {
		
		Hashtable<Integer, Permission> permissions = new Hashtable<Integer, Permission>();
		for (int i=0; i < numPermissions; i++) {
			permissions.put(i, new Permission("permission_" + i));
		}
		
		HashSet<Role> roles = new HashSet<Role>();
		Policy policy = Policy.getPolicyInstance();
		Random r = new Random();
		
		
		for (int i=1; i <= numRoles; i++) {
			HashSet<Permission> randPermissions = new HashSet<Permission>();
			for (int j=0; j < r.nextInt(maxPermPerRole)+1; j++) {
				randPermissions.add(permissions.get(r.nextInt(numPermissions)));
			}
			
			roles.add(new Role(policy,"Role_" + i, randPermissions, new HashSet<Role>(), new HashSet<Role>()));
		}
		
		return policy;
	}
	
	public static HashSet<Sod> randomSodSet(int numSod, int maxRoles, Policy policy) {
		Random r = new Random();
		Hashtable<Integer, Role> roleTable = policy.getRoleTable();
		HashSet<Sod> sodSet = new HashSet<Sod>();
		
		
		int roleTableSize = roleTable.size();
		
		for (int i = 0; i < numSod; i++) {
			HashSet<Role> roleSet = new HashSet<Role>();
			int numRoles = r.nextInt(maxRoles-1) + 2;
			int t = r.nextInt(numRoles-1) + 2;
			for (int j = 1; j <= numRoles; j++) {
				while(!roleSet.add(roleTable.get(r.nextInt(roleTableSize)+1)));
			}
			sodSet.add(new Sod(roleSet, t));
		}
		
		return sodSet;
	}
}

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

}

package edu.psu.msu5001.rbac;

import java.util.*;
//import org.h2.*;

public class Request {
	
	
	private HashSet<Permission> permissions = new HashSet<Permission>();
	private Policy policy; 
	//private HashMap<Role, HashSet<Role>> roleHierarchy;
	//private HashMap<Integer, HashSet<Permission>> rolePermissionsMap;
	
	
	public Request(HashSet<String> permissions, Policy policy) {
		setPolicy(policy);
		setPermissions(permissions);
	}
	
	
	public void makeRequest(HashSet<Permission> permissions) {
		
	}
	
	public void makeRequest(HashSet<Permission> permissions, HashSet<Permission> permissions_upperBound) {
		
	}

	public HashSet<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(HashSet<String> permissions) {
		ArrayList<Permission> policyPermissions = policy.getPermissions();
		//HashSet<String> policyPermissions = new HashSet<String>();
		/*
		for (Permission permission : policy.getPermissions()) {
			policyPermissions.add(permission.getPermissionName());
		}*/
		
		for (Permission permission : policyPermissions) {
			if (permissions.contains(permission.getPermissionName()) && 
					!this.permissions.contains(permission)) {
				this.permissions.add(permission);
			}
		}
	}


	public Policy getPolicy() {
		return policy;
	}


	public void setPolicy(Policy policy) {
		this.policy = policy;
	}
}

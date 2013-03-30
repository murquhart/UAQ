package edu.psu.msu5001.rbac;

import java.util.HashSet;

public class Policy {
	
	private HashSet<Role> roleSet;
	
	public HashSet<Role> getRoleSet() {
		return roleSet;
	}

	public void setRoleSet(HashSet<Role> roleSet) {
		this.roleSet = roleSet;
	}

}

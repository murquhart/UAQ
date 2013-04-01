package edu.psu.msu5001.rbac;

import java.util.HashSet;

public class Sod {
	
	private HashSet<Role> roles;
	private int t;
	
	public Sod(HashSet<Role> roles, int t) {
		setRoles(roles);
		set_t(t);
	}
	
	public HashSet<Role> getRoles() {
		return roles;
	}
	public void setRoles(HashSet<Role> roles) {
		this.roles = roles;
	}
	
	public void addRoles(HashSet<Role> roles) {
		this.roles.addAll(roles);
	}
	
	public void removeRoles(HashSet<Role> roles) {
		this.roles.removeAll(roles);
	}
	
	public void addRole(Role role) {
		this.roles.add(role);
	}
	
	public void removeRole(Role role) {
		this.roles.remove(role);
	}

	public int get_t() {
		return t;
	}

	public void set_t(int t) {
		if (t<2) throw new IllegalArgumentException("t must be an integer value of 2 or greater");
		else this.t = t;
	}
}

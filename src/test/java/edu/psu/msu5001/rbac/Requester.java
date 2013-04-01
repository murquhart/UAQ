package edu.psu.msu5001.rbac;

import java.util.HashSet;

public class Requester {
	//private final int id;
	private String name;
	private HashSet<Role> roles;
	
	public Requester(String name, HashSet<Role> roles) {
		setName(name);
		setRoles(roles);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
}

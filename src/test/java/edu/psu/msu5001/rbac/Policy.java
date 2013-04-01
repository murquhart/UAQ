package edu.psu.msu5001.rbac;

import java.util.HashSet;
import java.util.Hashtable;

public class Policy {
	
	private static Policy policy = null;
	private Hashtable<Integer, Role> roleTable;
	private HashSet<Sod> sodSet;
	
	private Policy() {
		
	}
	
	private Policy(Hashtable<Integer, Role> roleTable, HashSet<Sod> sodSet) {
		setRoleTable(roleTable);
		setSodSet(sodSet);
	}
	
	public static Policy getPolicyInstance() {
		if (policy == null) throw new NullPointerException();
		return policy;
	}
	
	public static Policy getPolicyInstance(Hashtable<Integer, Role> roleTable, HashSet<Sod> sodSet) {
		if (policy == null) policy = new Policy(roleTable, sodSet);
		return policy;
	}
	
	public Hashtable<Integer, Role> getRoleTable() {
		return roleTable;
	}

	public void setRoleTable(Hashtable<Integer, Role> roleTable) {
		this.roleTable = roleTable;
	}
	
	public void addRoles(Hashtable<Integer, Role> roleTable) {
		this.roleTable.putAll(roleTable);
	}
	
	public void removeRoles(HashSet<Integer> roleKeys) {
		for (int key : roleKeys) this.roleTable.remove(key);
	}
	
	protected void addRole(Role role) {
		roleTable.put(role.getId(), role);
	}
	
	protected int getUniqueRoleId() {
		int key;
		for (key = 0; roleTable.containsKey(key); key++);
		return key;
	}
	
	public void removeRole(int key) {
		this.roleTable.remove(key);
	}

	public HashSet<Sod> getSodSet() {
		return sodSet;
	}

	public void setSodSet(HashSet<Sod> sodSet) {
		this.sodSet = sodSet;
	}
	
	public void addSod(Sod sod) {
		sodSet.add(sod);
	}
	
	public void removeSod(Sod sod) {
		sodSet.remove(sod);
	}
	
	public void addSods(HashSet<Sod> sods) {
		sodSet.addAll(sods);
	}
	
	public void removeSods(HashSet<Sod> sods) {
		sodSet.removeAll(sods);
	}

}

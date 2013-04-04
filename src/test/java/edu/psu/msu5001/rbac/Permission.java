package edu.psu.msu5001.rbac;

import java.util.HashSet;

public class Permission {
	private String permissionName;
	private HashSet<Role> roles = new HashSet<Role>();

	public Permission(String permissionName) {
		setPermissionName(permissionName);
	}
	
	/**
	 * @return the permissionName
	 */
	public String getPermissionName() {
		return permissionName;
	}

	/**
	 * @param permissionName the permissionName to set
	 */
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	
	public HashSet<Role> getRoles() {
		return roles;
	}

	public void setRoles(HashSet<Role> roles) {
		for (Role role : this.roles) role.removePermission(this);
		for (Role role : roles) role.addPermission(this);
		this.roles = roles;
	}
	
	public void addRoles(HashSet<Role> roles) {
		for (Role role : roles) role.addPermission(this);
		this.roles.addAll(roles);
	}
	
	public void removeRoles(HashSet<Role> roles) {
		for (Role role : roles) role.removePermission(this);
		this.roles.removeAll(roles);
	}
	
	public void addRole(Role role) {
		if (!role.getRolePermissions().contains(this)) role.addPermission(this);
		this.roles.add(role);
	}
	
	public void removeRole(Role role) {
		roles.remove(role);
		if (role.getRolePermissions().contains(this)) role.removePermission(this);
	}
}

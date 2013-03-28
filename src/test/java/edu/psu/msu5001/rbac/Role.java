package edu.psu.msu5001.rbac;

import java.util.*;

public class Role {
	private String roleName;
	private HashSet<Role> roleChildren;
	private HashSet<Role> roleParents;
	private HashSet<Permission> rolePermissions;
	
	public Role (String roleName) {
		this(roleName,new HashSet<Permission>());
	}
	
	public Role (String roleName, HashSet<Permission> rolePermissions) {
		this(roleName,rolePermissions,new HashSet<Role>(),new HashSet<Role>());
	}
	
	public Role (String roleName, HashSet<Permission> rolePermissions, HashSet<Role> roleChildren, HashSet<Role> roleParents) {
		setRoleName(roleName);
		setRolePermissions(rolePermissions);
		setRoleChildren(roleChildren);
		setRoleParents(roleParents);
	}
	
	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the rolePermissions
	 */
	public HashSet<Permission> getRolePermissions() {
		return rolePermissions;
	}

	/**
	 * @param rolePermissions the rolePermissions to set
	 */
	public void setRolePermissions(HashSet<Permission> rolePermissions) {
		this.rolePermissions = rolePermissions;
	}
	
	/**
	 * @return the roleChildren
	 */
	public HashSet<Role> getRoleChildren() {
		return roleChildren;
	}

	/**
	 * @param roleChildren the roleChildren to set
	 */
	public void setRoleChildren(HashSet<Role> roleChildren) {
		this.roleChildren = roleChildren;
	}
	
	/**
	 * @return the roleParents
	 */
	public HashSet<Role> getRoleParents() {
		return roleParents;
	}

	/**
	 * @param roleParents the roleParents to set
	 */
	public void setRoleParents(HashSet<Role> roleParents) {
		this.roleParents = roleParents;
	}

	public boolean addChild(Role child) {
		return roleChildren.add(child);
	}
	
	public boolean addChildren(HashSet<Role> children) {
		return roleChildren.addAll(children);
	}
	
	public boolean removeChild(Role child) {
		return roleChildren.remove(child);
	}
	
	public boolean removeChildren(HashSet<Role> children) {
		return roleChildren.removeAll(children);
	}
	
	public boolean addParent(Role parent) {
		return roleParents.add(parent);
	}
	public boolean addParents(HashSet<Role> parents) {
		return roleParents.addAll(parents);
	}
	
	public boolean removeParent(Role parent) {
		return roleParents.remove(parent);
	}
	
	public boolean removeParents(HashSet<Role> parents) {
		return roleParents.removeAll(parents);
	}
	
	public boolean addPermission(Permission permission) {
		return rolePermissions.add(permission);
	}
	public boolean addPermissions(HashSet<Permission> permissions) {
		return rolePermissions.addAll(permissions);
	}
	
	public boolean removePermission(Role permission) {
		return rolePermissions.remove(permission);
	}
	
	public boolean removePermissions(HashSet<Permission> permissions) {
		return rolePermissions.removeAll(permissions);
	}

}

package edu.psu.msu5001.rbac;

import java.util.*;

public class Role {
	private final int id;
	private String roleName;
	private HashSet<Role> roleChildren;
	private HashSet<Role> roleParents;
	private HashSet<Permission> rolePermissions;
	private Policy policy;
	
	public Role (Policy policy, String roleName) {
		this(policy,roleName,new HashSet<Permission>());
	}
	
	public Role (Policy policy, String roleName, HashSet<Permission> rolePermissions) {
		this(policy,roleName,rolePermissions,new HashSet<Role>(),new HashSet<Role>());
	}
	
	public Role (Policy policy, String roleName, HashSet<Permission> rolePermissions, HashSet<Role> roleChildren, HashSet<Role> roleParents) {
		setRoleName(roleName);
		setRolePermissions(rolePermissions);
		setRoleChildren(roleChildren);
		setRoleParents(roleParents);
		setPolicy(policy);
		id = policy.getUniqueRoleId();
		policy.addRole(this);
	}
	
	/**
	 * @return the policy
	 */
	public Policy getPolicy() {
		return policy;
	}

	/**
	 * @param policy the policy to set
	 */
	public void setPolicy(Policy policy) {
		this.policy = policy;
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
		for (Permission permission : this.rolePermissions) permission.removeRole(this);
		for (Permission permission : rolePermissions) permission.addRole(this);
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
		permission.addRole(this);
		return rolePermissions.add(permission);
	}
	public boolean addPermissions(HashSet<Permission> permissions) {
		for (Permission permission : permissions) permission.addRole(this);
		return rolePermissions.addAll(permissions);
	}
	
	public boolean removePermission(Permission permission) {
		permission.removeRole(this);
		return rolePermissions.remove(permission);
	}
	
	public boolean removePermissions(HashSet<Permission> permissions) {
		for (Permission permission : permissions) permission.removeRole(this);
		return rolePermissions.removeAll(permissions);
	}

	public int getId() {
		return id;
	}

}

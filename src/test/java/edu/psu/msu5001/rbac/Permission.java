package edu.psu.msu5001.rbac;

public class Permission {
	private String permissionName;

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
}

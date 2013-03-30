package edu.psu.msu5001.rbac;

import java.util.*;
//import org.h2.*;

public class Request {
	
	
	private HashSet<Permission> permissons;
	//private HashMap<Role, HashSet<Role>> roleHierarchy;
	//private HashMap<Integer, HashSet<Permission>> rolePermissionsMap;
	
	
	public Request(HashSet<Permission> permissons) {
		setPermissons(permissons);
	}
	
	
	public void makeRequest(HashSet<Permission> permissions) {
		
	}
	
	public void makeRequest(HashSet<Permission> permissions, HashSet<Permission> permissions_upperBound) {
		
	}

	public HashSet<Permission> getPermissons() {
		return permissons;
	}

	public void setPermissons(HashSet<Permission> permissons) {
		this.permissons = permissons;
	}
}

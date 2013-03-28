package edu.psu.msu5001.rbac;

import java.util.*;
import org.h2.*;

public class Policy {
	
	private HashSet<Role> roleSet;
	private HashSet<Permission> permissionSet = new HashSet<Permission>();
	private HashMap<Role, HashSet<Role>> roleHierarchy;
	private HashMap<Integer, HashSet<Permission>> rolePermissionsMap;
	
	
	public Policy() {
		
	}
	
	public static void main(String [] args) {
		System.out.println("Hello, World!");
	}
}

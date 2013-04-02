package edu.psu.msu5001.rbac.test;

import java.util.HashSet;
import java.util.Hashtable;

import edu.psu.msu5001.rbac.*;
import edu.psu.msu5001.rbac.gen.PolicyGenerator;
import edu.psu.msu5001.rbac.gen.RequestGenerator;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.print("Please wait while we build you a policy...");
		Policy policy = PolicyGenerator.randomPolicy(20, 0, 20, 8);
		policy.toXml("policy.xml");
		System.out.println("done.");
		
		System.out.print("Creating UAQ engine...");
		Uaq uaqEngine = Uaq.getInstance(policy);
		System.out.println("done.\n");
		
		
		HashSet<Role> roles = new HashSet<Role>();
		for (Role role : policy.getRoleTable().values()) roles.add(role);
		
		/*
		 * Need generated request/requester here
		 */
		Requester requester = new Requester(1,"Test User", roles);
		//Request request = new Request(policy.getRoleTable().get(1).getRolePermissions());
		HashSet<Request> requests = RequestGenerator.generateRequests(10, policy, 6, 2);
		
		for (Request request : requests) {
			System.out.println(requester.getName() + " issuing request for permissions: ");
			for (Permission permission : request.getPermissons()) System.out.println(permission.getPermissionName());
			
			long elapsedTime = System.nanoTime();
			int [] model = uaqEngine.doRequest(request, requester);
			System.out.println("\nUAQ activated roles: ");
			for(int i : model) if (i > 0) System.out.println("role_" + i);
			elapsedTime = System.nanoTime() - elapsedTime;
			System.out.println("\nUAQ took: "+ elapsedTime/1000000000.0 + " seconds");
		}
	}

}

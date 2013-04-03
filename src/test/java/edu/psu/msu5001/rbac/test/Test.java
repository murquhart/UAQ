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
		/*
		 * Config for PolicyGenerator.randomPolicy
		 */
		int numRoles = 20;
		int roleDepth = 0;
		int numPermissions = 20;
		int maxPermPerRole = 8;
		
		/*
		 * Config for PolicyGenerator.randomSodSet
		 */
		int numSod = 20;
		int maxRoles = 3;
		
		/*
		 * Config for RequestGenerator.generateRequests
		 */
		int numRequests = 10;
		int maxRequestSize = 6;
		int minRequestSize = 2;
		
		System.out.print("Please wait while we build you a policy...");
		Policy policy = PolicyGenerator.randomPolicy(numRoles, roleDepth, numPermissions, maxPermPerRole);
		HashSet<Sod> randomSodSet = PolicyGenerator.randomSodSet(numSod, maxRoles, policy);
		policy.setSodSet(randomSodSet);
		
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
		HashSet<Request> requests = RequestGenerator.generateRequests(numRequests, policy, maxRequestSize, minRequestSize);
		
		for (Request request : requests) {
			System.out.println(requester.getName() + " issuing request for permissions: ");
			for (Permission permission : request.getPermissons()) System.out.println(permission.getPermissionName());
			
			long elapsedTime = System.nanoTime();
			int [] model = uaqEngine.doRequest(request, requester);
			System.out.print("\nmodel: ");
			for(int i=0; i < model.length; i++) System.out.print(model[i] + " ");
			System.out.println();
			System.out.println("\nUAQ activated roles: ");
			for(int i : model) if (i > 0) System.out.println(policy.getRoleTable().get(i).getRoleName());
			elapsedTime = System.nanoTime() - elapsedTime;
			System.out.println("\nUAQ took: "+ elapsedTime/1000000000.0 + " seconds");
		}
	}

}

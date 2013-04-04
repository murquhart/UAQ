package edu.psu.msu5001.rbac.gen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import edu.psu.msu5001.rbac.*;

public class RequestGenerator {
	public static HashSet<Request> generateRequests(int numRequests, Policy policy, int maxRequestSize, int minRequestSize) {
		ArrayList<Permission> policyPermissions = policy.getPermissions();
		int policyPermissionCount = policyPermissions.size();
		
		Random r = new Random();
		
		HashSet<Request> requests = new HashSet<Request>();
		for (int i=0; i < numRequests; i++) {
			
			HashSet<String> requestPermissions = new HashSet<String>();
			
			for (int j=0; j < minRequestSize + r.nextInt(maxRequestSize - minRequestSize + 1); j++) {
				int rand = r.nextInt(policyPermissionCount);
				while (requestPermissions.contains(policyPermissions.get(rand))) rand = r.nextInt(policyPermissionCount);
				requestPermissions.add(policyPermissions.get(rand).getPermissionName());
			}
			
			requests.add(new Request(requestPermissions, policy));
		}
		
		return requests;
	}
	
	public static HashSet<Requester> generateUsers(Policy policy) {
		return new HashSet<Requester>();
	}

}

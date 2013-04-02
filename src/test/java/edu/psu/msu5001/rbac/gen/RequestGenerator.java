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
			
			HashSet<Permission> requestPermissions = new HashSet<Permission>();
			
			for (int j=0; j < minRequestSize + r.nextInt(maxRequestSize - minRequestSize + 1); j++)
				while (!requestPermissions.add(policyPermissions.get(r.nextInt(policyPermissionCount))));
			
			requests.add(new Request(requestPermissions));
		}
		
		return requests;
	}
	
	public static HashSet<Requester> generateUsers(Policy policy) {
		return new HashSet<Requester>();
	}

}

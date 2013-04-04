package edu.psu.msu5001.rbac.test;

import java.io.*;
import java.util.*;

import edu.psu.msu5001.rbac.*;
import edu.psu.msu5001.rbac.gen.PolicyGenerator;
import edu.psu.msu5001.rbac.gen.RequestGenerator;

public class Test {
	
	private static HashSet<Request> loadRequests(File file, Policy policy) {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String readLine;
		HashSet<Request> requests = new HashSet<Request>();
		
		try {
			HashSet<String> permissions = null;
			while ((readLine = br.readLine()) != null) {
				if (readLine.equals("<request>")) {
					permissions = new HashSet<String>();
				}
				else if (readLine.equals("</request>")) {
					requests.add(new Request(permissions, policy));
				}
				else permissions.add(readLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requests;
	}
	
	private static void writeRequests(HashSet<Request> requests, File file) {
		FileOutputStream fop = null;
		PrintStream print = null;
		try {
			fop = new FileOutputStream(file);
			print = new PrintStream(fop);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean first = true;
		for (Request request : requests) {
			if (first) {
				print.println("<request>");
				first = false;
			}
			else print.println("\n<request>");
			for (Permission permission : request.getPermissions()) {
				print.println(permission.getPermissionName());
			}
			print.print("</request>");
			print.flush();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/*Properties prop = new Properties();
		
		try {
    		//set the properties value
    		prop.setProperty("policy_input_xml", "policy.xml");
    		prop.setProperty("policy_output_xml", "policy.xml");
    		prop.setProperty("requests_input_file", "requests.txt");
    		prop.setProperty("requests_output_file", "requests.txt");
 
    		//save properties to project root folder
    		prop.store(new FileOutputStream("config.properties"), null);
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }*/
		
		boolean usePolicyXml = false;
		File policyInputXml = new File("policy.xml");
		String policyOutputXml = "policy.xml";
		File requestsInputFile = new File("requests.txt");
		File requestsOutputFile = new File("requests.txt");
		boolean useRequestsFile = false;
		
		
		/*
		 * Config for PolicyGenerator.randomPolicy
		 */
		int numRoles = 400;
		int roleDepth = 0;
		int numPermissions = 2000;
		int maxPermPerRole = 50;
		
		/*
		 * Config for PolicyGenerator.randomSodSet
		 */
		int numSod = 25;
		int maxRoles = 6;
		
		/*
		 * Config for RequestGenerator.generateRequests
		 */
		int numRequests = 100;
		int maxRequestSize = 30;
		int minRequestSize = 2;
		
		System.out.print("Please wait while we build you a policy...");
		
		Policy policy = null;
		
		if (usePolicyXml) {
			policy = Policy.getPolicyInstance(policyInputXml);
		}
		else {
			policy = PolicyGenerator.randomPolicy(numRoles, roleDepth, numPermissions, maxPermPerRole);
			HashSet<Sod> randomSodSet = PolicyGenerator.randomSodSet(numSod, maxRoles, policy);
			policy.setSodSet(randomSodSet);
			policy.toXml(policyOutputXml);
		}
		
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
		
		HashSet<Request> requests = null;
		
		if (useRequestsFile) {
			requests = loadRequests(requestsInputFile, policy);
		}
		else {
			requests = RequestGenerator.generateRequests(numRequests, policy, maxRequestSize, minRequestSize);
			writeRequests(requests, requestsOutputFile);
		}
		
		for (Request request : requests) {
			//System.out.println(requester.getName() + " issuing request for permissions: ");
			//for (Permission permission : request.getPermissions()) System.out.println(permission.getPermissionName());
			
			long elapsedTime = System.nanoTime();
			int [] model = uaqEngine.doRequest(request, requester, -1, 0);
			Collection<int []> rbacClauses = uaqEngine.getRbacClauses();
			HashSet<int []> sodClauses = uaqEngine.getSodClauses();
			
			//System.out.println("rbac clauses:");
			//for (int [] rbacClause : rbacClauses) {
			//	for(int i=0; i < rbacClause.length; i++) System.out.print(rbacClause[i] + " ");
				//System.out.println();
			//}
			
			//System.out.println("sod clauses:");
			//for (int [] sodClause : sodClauses) {
			//	for(int i=0; i < sodClause.length; i++) System.out.print(sodClause[i] + " ");
				//System.out.println();
			//}
			
			//System.out.print("model: ");
			//for(int i=0; i < model.length; i++) System.out.print(model[i] + " ");
			//System.out.println();
			
			//System.out.println("\nUAQ activated roles: ");
			HashSet<Permission> permissionsActivated = new HashSet<Permission>();
			for(int i : model) { 
				if (i > 0) {
					Role bufRole = policy.getRoleTable().get(i);
					//System.out.println(bufRole.getRoleName());
					permissionsActivated.addAll(bufRole.getRolePermissions());
				}
			}
				
			System.out.print("\nTotal permissions activated: " + permissionsActivated.size());
			//for (Permission permission : permissionsActivated) System.out.print(permission.getPermissionName() + " ");
			elapsedTime = System.nanoTime() - elapsedTime;
			System.out.println("\nUAQ took: "+ elapsedTime/1000000000.0 + " seconds");
		}
	}

}

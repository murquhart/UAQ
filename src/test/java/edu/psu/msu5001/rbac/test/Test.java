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
		File metricsFile = new File("results.csv");
		boolean useRequestsFile = false;
		
		
		/*
		 * Config for PolicyGenerator.randomPolicy
		 */
		int numRoles = 10;
		int roleDepth = 0;
		int numPermissions = 5;
		int maxPermPerRole = 3;
		
		/*
		 * Config for PolicyGenerator.randomSodSet
		 */
		int numSod = 2;
		int maxRoles = 2;
		
		/*
		 * Config for RequestGenerator.generateRequests
		 */
		int numRequests = 5;
		int maxRequestSize = 3;
		int minRequestSize = 2;
		
		
		Properties prop = new Properties();
		 
    	try {
    		prop.load(new FileInputStream("config.properties"));
    		
    		usePolicyXml = !Boolean.parseBoolean(prop.getProperty("gen_policy"));
    		useRequestsFile = !Boolean.parseBoolean(prop.getProperty("gen_requests"));
    		policyInputXml = new File(prop.getProperty("policy_input_xml"));
    		policyOutputXml = prop.getProperty("policy_output_xml");
    		requestsInputFile = new File(prop.getProperty("requests_input_file"));
    		requestsOutputFile = new File(prop.getProperty("requests_output_file"));
    		metricsFile = new File(prop.getProperty("out_file"));
    		numRoles = Integer.parseInt(prop.getProperty("numRoles"));
    		numPermissions = Integer.parseInt(prop.getProperty("numPermissions"));
    		maxPermPerRole = Integer.parseInt(prop.getProperty("maxPermPerRole"));
    		numSod = Integer.parseInt(prop.getProperty("numSod"));
    		maxRoles = Integer.parseInt(prop.getProperty("maxRoles"));
    		numRequests = Integer.parseInt(prop.getProperty("numRequests"));
    		maxRequestSize = Integer.parseInt(prop.getProperty("maxRequestSize"));
    		minRequestSize = Integer.parseInt(prop.getProperty("minRequestSize")); 
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
		
		
		
		int request_id = 0;
		
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
		System.out.println("done.");
		
		
		HashSet<Role> roles = new HashSet<Role>();
		for (Role role : policy.getRoleTable().values()) roles.add(role);
		
		/*
		 * Need generated request/requester here
		 */
		Requester requester = new Requester(1,"Test User", roles);
		//Request request = new Request(policy.getRoleTable().get(1).getRolePermissions());
		
		HashSet<Request> requests = null;
		
		System.out.print("Building requests...");
		if (useRequestsFile) {
			requests = loadRequests(requestsInputFile, policy);
		}
		else {
			requests = RequestGenerator.generateRequests(numRequests, policy, maxRequestSize, minRequestSize);
			writeRequests(requests, requestsOutputFile);
		}
		System.out.println("done.\n");
		
		FileOutputStream fop = null;
		PrintStream print = null;
		
		try {
			fop = new FileOutputStream(metricsFile, true);
			print = new PrintStream(fop);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Request request : requests) {
			//System.out.println(requester.getName() + " issuing request for permissions: ");
			//for (Permission permission : request.getPermissions()) System.out.println(permission.getPermissionName());
			
			long elapsedTime = System.nanoTime();
			int [] model = uaqEngine.doRequest(request, requester, -1, 0);
			elapsedTime = System.nanoTime() - elapsedTime;
			Collection<int []> rbacClauses = uaqEngine.getAllRbacClauses();
			HashSet<int []> sodClauses = uaqEngine.getSodClauses();
			
			int numClauses = 0;
			double avgConstPerClause = 0;
			int requestSize = request.getPermissions().size();
			
			for (int [] rbacClause : rbacClauses) {
				avgConstPerClause += rbacClause.length;
				numClauses++;
			}
			
			for (int [] sodClause : sodClauses) {
				avgConstPerClause += sodClause.length;
				numClauses++;
			}
			
			avgConstPerClause = avgConstPerClause/numClauses;
			
			/*System.out.println("rbac clauses:");
			for (int [] rbacClause : rbacClauses) {
				for(int i=0; i < rbacClause.length; i++) System.out.print(rbacClause[i] + " ");
				System.out.println();
			}*/
			
			/*System.out.println("sod clauses:");
			for (int [] sodClause : sodClauses) {
				for(int i=0; i < sodClause.length; i++) System.out.print(sodClause[i] + " ");
				System.out.println();
			}*/
			
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
			
			System.out.println();
			
			print.print(numRoles + ",");
			
			System.out.println("Request_" + request_id++);
			
			print.print(requestSize + ",");
			System.out.println("Total permissions requested: " + requestSize);
			
			print.print(permissionsActivated.size() + ",");
			System.out.println("Total permissions activated: " + permissionsActivated.size());
			
			print.print(numClauses + ",");
			System.out.println("Total cnf clauses: " + numClauses);
			
			print.print(numSod + ",");
			print.print(maxRoles + ",");
			
			print.print(avgConstPerClause + ",");
			System.out.println("Average roles per clause: " + avgConstPerClause);
			//for (Permission permission : permissionsActivated) System.out.print(permission.getPermissionName() + " ");
			
			print.println(elapsedTime/1000000000.0);
			System.out.println("UAQ took: "+ elapsedTime/1000000000.0 + " seconds");
		}
		
		print.flush();
		print.close();
		try {
			fop.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
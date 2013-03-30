package edu.psu.msu5001.rbac;

public class Uaq {
	
	private Policy policy;
	
	public Uaq(Policy policy) {
		setPolicy(policy);
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}
	
	public int makeRequest(Request request) {
		return 0;
	}

}

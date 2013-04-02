package edu.psu.msu5001.rbac;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Hashtable;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class Policy {
	
	private static Policy policy = null;
	private Hashtable<Integer, Role> roleTable = new Hashtable<Integer, Role>();
	private HashSet<Sod> sodSet = new HashSet<Sod>();
	
	private Policy() {
		
	}
	
	/*private Policy(Hashtable<Integer, Role> roleTable, HashSet<Sod> sodSet) {
		setRoleTable(roleTable);
		setSodSet(sodSet);
	}*/
	
	public static Policy getPolicyInstance() {
		if (policy == null) policy = new Policy();
		return policy;
	}
	
	/*public static Policy getPolicyInstance(Hashtable<Integer, Role> roleTable, HashSet<Sod> sodSet) {
		if (policy == null) policy = new Policy(roleTable, sodSet);
		return policy;
	}*/
	
	public Hashtable<Integer, Role> getRoleTable() {
		return roleTable;
	}

	public void setRoleTable(Hashtable<Integer, Role> roleTable) {
		this.roleTable = roleTable;
	}
	
	public void addRoles(Hashtable<Integer, Role> roleTable) {
		this.roleTable.putAll(roleTable);
	}
	
	public void removeRoles(HashSet<Integer> roleKeys) {
		for (int key : roleKeys) this.roleTable.remove(key);
	}
	
	protected void addRole(Role role) {
		roleTable.put(role.getId(), role);
	}
	
	protected int getUniqueRoleId() {
		int key;
		for (key = 1; roleTable.containsKey(key); key++);
		
		return key;
	}
	
	public void removeRole(int key) {
		this.roleTable.remove(key);
	}

	public HashSet<Sod> getSodSet() {
		return sodSet;
	}

	public void setSodSet(HashSet<Sod> sodSet) {
		this.sodSet = sodSet;
	}
	
	public void addSod(Sod sod) {
		sodSet.add(sod);
	}
	
	public void removeSod(Sod sod) {
		sodSet.remove(sod);
	}
	
	public void addSods(HashSet<Sod> sods) {
		sodSet.addAll(sods);
	}
	
	public void removeSods(HashSet<Sod> sods) {
		sodSet.removeAll(sods);
	}
	
	public void toXml(String filePath) {
		//get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom=null;
		try {
		//get an instance of builder
		DocumentBuilder db = dbf.newDocumentBuilder();

		//create an instance of DOM
		dom = db.newDocument();

		}catch(ParserConfigurationException pce) {
			//dump it
			System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
			System.exit(1);
		}
		
		//create the root element 
		Element rootEle = dom.createElement("Policy");
		dom.appendChild(rootEle);
		
		for (Role role : roleTable.values()) {
			rootEle.appendChild(createRoleElement(role, dom));
		}
		
		Transformer tr = null;
		try {
			tr = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.setOutputProperty(OutputKeys.METHOD,"xml");
		tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

		//to send the output to a file
		try {
			tr.transform( new DOMSource(dom),new StreamResult(new FileOutputStream(filePath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//to send the output to console
		/*try {
			tr.transform( new DOMSource(dom),new StreamResult(System.out));
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	
	private Element createRoleElement(Role role, Document dom) {
		
		Element roleEle = dom.createElement("Role");
		roleEle.setAttribute("id", Integer.toString(role.getId()));
		roleEle.setAttribute("Name", role.getRoleName());
		
		for (Permission permission : role.getRolePermissions()) {
			Element permEle = dom.createElement("permission");
			permEle.appendChild(dom.createTextNode(permission.getPermissionName()));
			roleEle.appendChild(permEle);
		}
		
		for (Role parent : role.getRoleParents()) {
			Element parentEle = dom.createElement("parentRole");
			parentEle.setAttribute("id", Integer.toString(parent.getId()));
			parentEle.appendChild(dom.createTextNode(parent.getRoleName()));
			roleEle.appendChild(parentEle);
		}
		
		for (Role child : role.getRoleChildren()) {
			Element childEle = dom.createElement("childRole");
			childEle.setAttribute("id", Integer.toString(child.getId()));
			childEle.appendChild(dom.createTextNode(child.getRoleName()));
			roleEle.appendChild(childEle);
		}

		return roleEle;
	}

}

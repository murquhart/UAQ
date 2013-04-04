package edu.psu.msu5001.rbac;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Policy {
	
	private static Policy policy = null;
	private Hashtable<Integer, Role> roleTable = new Hashtable<Integer, Role>();
	//private Hashtable<Integer, Permission> permissionTable = new Hashtable<Integer, Permission>();
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
	
	public static Policy getPolicyInstance(File xml) {
		policy = new Policy();
		policy.parseXml(xml);
		return policy;
	}
	
	/*public static Policy getPolicyInstance(Hashtable<Integer, Role> roleTable, HashSet<Sod> sodSet) {
		if (policy == null) policy = new Policy(roleTable, sodSet);
		return policy;
	}*/
	
	public ArrayList<Permission> getPermissions() {
		ArrayList<Permission> permissions = new ArrayList<Permission>();
		for (Role role : roleTable.values()) {
			for (Permission permission : role.getRolePermissions()) {
				if (!permissions.contains(permission)) permissions.add(permission);
			}
		}
		return permissions;
	}
	
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
		
		for (Sod sod : sodSet) {
			rootEle.appendChild(createSodElement(sod, dom));
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
	
	private static Element createSodElement(Sod sod, Document dom) {
		
		Element sodEle = dom.createElement("sod");
		sodEle.setAttribute("t", Integer.toString(sod.get_t()));
		for (Role role : sod.getRoles()) {
			Element roleEle = dom.createElement("role");
			roleEle.setAttribute("id", Integer.toString(role.getId()));
			roleEle.appendChild(dom.createTextNode(role.getRoleName()));
			sodEle.appendChild(roleEle);
		}
		
		return sodEle;
	}
	
	private static Element createRoleElement(Role role, Document dom) {
		
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
	
	private void parseXml(File xml) {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc = null;
		
		//Hashtable <Integer, Role> xmlRoleTable = new Hashtable <Integer, Role>();
		HashSet<Sod> xmlSodSet = new HashSet<Sod>();
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			doc = dBuilder.parse(xml);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Node root = doc.getDocumentElement();
		root.normalize();
		
		Node node = root.getFirstChild();
		Hashtable<String, Permission> masterPermissions = new Hashtable<String, Permission>();
		do {
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				node = node.getNextSibling();
				continue;
			}
			Element ele = (Element) node;
			if (ele.getNodeName() == "Role") {
				HashSet<Permission> permissions = new HashSet<Permission>();
				NodeList nList = ele.getElementsByTagName("permission");
				for (int i = 0; i < nList.getLength(); i++) {
					Permission permission = new Permission(nList.item(i).getTextContent());
					if (masterPermissions.containsKey(permission.getPermissionName())) permission = masterPermissions.get(permission.getPermissionName());
					else masterPermissions.put(permission.getPermissionName(), permission);
					permissions.add(permission);
				}
				new Role(Integer.parseInt(ele.getAttribute("id")),this,ele.getAttribute("Name"),permissions, new HashSet<Role>(), new HashSet<Role>());
			}
			else if (ele.getNodeName() == "sod") {
				HashSet<Role> roleSet = new HashSet<Role>();
				
				NodeList nList = ele.getElementsByTagName("role");
				for (int i = 0; i < nList.getLength(); i++) {
					Element roleEle = (Element) nList.item(i);
					roleSet.add(roleTable.get(Integer.parseInt(roleEle.getAttribute("id"))));
				}
				xmlSodSet.add(new Sod(roleSet, Integer.parseInt(ele.getAttribute("t"))));
			}
			node = node.getNextSibling();
		} while (node != null);
		
		setSodSet(xmlSodSet);
		
		NodeList nList = doc.getElementsByTagName("Role");
		for (int i = 0; i < nList.getLength(); i++) {
			Element roleEle = (Element) nList.item(i);
			NodeList childList = roleEle.getElementsByTagName("childRole");
			for (int j = 0; j < childList.getLength(); j++) {
				Element childEle = (Element) childList.item(j);
				roleTable.get(roleEle.getAttribute("id")).addChild(roleTable.get(Integer.parseInt(childEle.getAttribute("id"))));
			}
		}
		for (int i = 0; i < nList.getLength(); i++) {
			Element roleEle = (Element) nList.item(i);
			NodeList parentList = roleEle.getElementsByTagName("parentRole");
			for (int j = 0; j < parentList.getLength(); j++) {
				Element parentEle = (Element) parentList.item(j);
				roleTable.get(roleEle.getAttribute("id")).addParent(roleTable.get(Integer.parseInt(parentEle.getAttribute("id"))));
			}
		}
	}
}

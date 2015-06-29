/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReikaXMLBase {

	public static Node getNamedNode(String name, NodeList li) {
		for (int i = 0; i < li.getLength(); i++) {
			Node n = li.item(0);
			if (n.getNodeName().equals(name))
				return n;
		}
		return null;
	}

	public static Document getXMLDocument(Class root, String path) {
		InputStream in = root.getResourceAsStream(path);
		if (in == null) {
			throw new RuntimeException("XML file at "+path+" relative to "+root.getCanonicalName()+" not found!");
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(in);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("XML file at "+path+" relative to "+root.getCanonicalName()+" failed to load!");
		}
	}

	public static String getNodeNameTree(Node n) {
		StringBuilder sb = new StringBuilder();
		List<Node> parents = new ArrayList<Node>();
		Node p;
		Node c;
		c = n;
		while ((p = c.getParentNode()) != null) {
			if (p.getNodeType() == Node.ELEMENT_NODE) {
				parents.add(p);
			}
			c = p;
		}
		for (int i = parents.size()-1; i >= 0; i--) {
			sb.append(parents.get(i).getNodeName());
			if (i > 0)
				sb.append(":");
		}
		//sb.append(n.getNodeName());
		return sb.toString();
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ReikaXMLBase {

	private ReikaXMLBase() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

	public static Document getXMLDocument(InputStream in) throws SAXException, IOException {
		ArrayList<String> li = ReikaFileReader.getFileAsLines(in, true, Charsets.UTF_8);
		while (!li.isEmpty() && !li.get(0).startsWith("<?xml version")) { //automatically clear any header crap
			li.remove(0);
		}
		li.set(0, getHeader(li.get(0)));
		in = ReikaFileReader.convertLinesToStream(li, true, Charsets.UTF_8);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(in);
			is.setEncoding(CharEncoding.UTF_8);
			return builder.parse(is);
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException("Could not initialize XML Parser!", e);
		}
	}

	private static String getHeader(String s) {
		return "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";
	}

	public static Node getNamedNode(String name, NodeList li) {
		for (int i = 0; i < li.getLength(); i++) {
			Node n = li.item(0);
			if (n.getNodeName().equals(name))
				return n;
		}
		return null;
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

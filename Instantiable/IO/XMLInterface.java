/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import Reika.DragonAPI.IO.ReikaXMLBase;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLInterface {

	private Document doc;

	private final Class rootClass;
	private final String filepath;
	private final boolean requireFile;

	private final HashMap<String, String> data = new HashMap<String, String>();

	public static final String NULL_VALUE = "#NULL!";

	public XMLInterface(Class root, String path) {
		this(root, path, false);
	}

	public XMLInterface(Class root, String path, boolean crashIfNull) {
		rootClass = root;
		filepath = path;
		requireFile = crashIfNull;
		try {
			doc = ReikaXMLBase.getXMLDocument(root, path);
		}
		catch (RuntimeException e) {
			if (requireFile)
				throw new RuntimeException(e);
		}
		this.readFileToMap();
	}

	public void reread() {
		try {
			doc = ReikaXMLBase.getXMLDocument(rootClass, filepath);
			this.readFileToMap();
		}
		catch (RuntimeException e) {
			if (requireFile)
				throw new RuntimeException(e);
		}
	}

	private void readFileToMap() {
		this.recursiveRead(doc);
	}

	private void recursiveRead(Node n) {
		if (n == null)
			return;
		NodeList li = n.getChildNodes();
		int len = li.getLength();
		for (int i = 0; i < len; i++) {
			Node ch = li.item(i);
			if (ch.getNodeType() == Node.ELEMENT_NODE) {
				//ReikaJavaLibrary.pConsole(ch.getNodeName());
				this.recursiveRead(ch);
			}
			else if (ch.getNodeType() == Node.TEXT_NODE) {
				String val = ch.getNodeValue();
				if (val != null) {
					if (val.equals("\n"))
						val = null;
					else {
						if (val.startsWith("\n"))
							val = val.substring(1);
						if (val.endsWith("\n"))
							val = val.substring(0, val.length()-1);
					}
					if (val != null && val.equals("\n"))
						val = null;
				}
				if (val != null) {
					val = val.replace("\t", "");
					//ReikaJavaLibrary.pConsole("TREE: "+ReikaXMLBase.getNodeNameTree(ch));
					String key = ReikaXMLBase.getNodeNameTree(ch);
					if (data.containsKey(key))
						;//throw new RuntimeException("Your input XML has multiple node trees with the EXACT same names! Resolve this!");
					data.put(key, val);
				}
			}
		}
	}

	public String getValueAtNode(String name) {
		String dat = data.get(name);
		if (dat == null)
			dat = NULL_VALUE;
		return dat;
	}

	@Override
	public String toString() {
		return data.toString();
	}


}
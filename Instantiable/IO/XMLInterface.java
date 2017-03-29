/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Reika.DragonAPI.IO.ReikaXMLBase;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;

public class XMLInterface {

	private Document doc;

	private final boolean requireFile;
	private final Object[] loadData;
	private final LoadFormat format;

	private final HashMap<String, String> data = new HashMap();
	private final MultiMap<String, String> tree = new MultiMap();

	public static final String NULL_VALUE = "#NULL!";

	public XMLInterface(Class root, String path) {
		this(root, path, false);
	}

	public XMLInterface(File path, boolean crashIfNull) {
		format = LoadFormat.FILE;
		loadData = new Object[]{path};
		requireFile = crashIfNull;
		try {
			doc = ReikaXMLBase.getXMLDocument(new FileInputStream(path));
		}
		catch (Exception e) {
			if (requireFile)
				throw new RuntimeException("Could not load XML at "+path, e);
			else
				e.printStackTrace();
		}
	}

	public XMLInterface(Class root, String path, boolean crashIfNull) {
		format = LoadFormat.JARPATH;
		requireFile = crashIfNull;
		loadData = new Object[]{root, path};
		try {
			InputStream in = root.getResourceAsStream(path);
			if (in == null)
				throw new RuntimeException("XML file at "+path+" relative to "+root.getName()+" not found!");
			doc = ReikaXMLBase.getXMLDocument(in);
		}
		catch (RuntimeException e) {
			if (requireFile)
				throw new RuntimeException("Could not load XML at "+path+" relative to "+root.getName(), e);
			else
				e.printStackTrace();
		}
		this.readFileToMap();
	}

	public void reread() {
		try {
			InputStream in = format.getInputStream(loadData);
			doc = ReikaXMLBase.getXMLDocument(in);
			this.readFileToMap();
		}
		catch (Exception e) {
			if (requireFile)
				throw new RuntimeException("Could not load XML: "+Arrays.toString(loadData), e);
			else
				e.printStackTrace();
		}
	}

	private void readFileToMap() {
		data.clear();
		tree.clear();
		this.recursiveRead("$TOP$", doc);
	}

	private void recursiveRead(String parent, Node n) {
		if (n == null)
			return;
		NodeList li = n.getChildNodes();
		int len = li.getLength();
		for (int i = 0; i < len; i++) {
			Node ch = li.item(i);
			String key = ReikaXMLBase.getNodeNameTree(ch);
			tree.addValue(parent, key);
			if (ch.getNodeType() == Node.ELEMENT_NODE) {
				//ReikaJavaLibrary.pConsole(ch.getNodeName());
				this.recursiveRead(key, ch);
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
					//ReikaJavaLibrary.pConsole("TREE: "+ReikaXMLBase.getNodeNameTree(ch));
					if (data.containsKey(key))
						;//throw new RuntimeException("Your input XML has multiple node trees with the EXACT same names! Resolve this!");
					data.put(key, this.cleanString(val));
				}
			}
		}
	}

	private String cleanString(String val) {
		val = val.replace("\t", "");
		while (!val.isEmpty() && val.endsWith("\\n")) {
			val = val.substring(0, val.length()-2);
		}
		while (!val.isEmpty() && val.charAt(0) == ' ') {
			val = val.substring(1);
		}
		while (!val.isEmpty() && val.charAt(val.length()-1) == ' ') {
			val = val.substring(0, val.length()-1);
		}
		return val;
	}

	public String getValueAtNode(String name) {
		String dat = data.get(name);
		if (dat == null)
			dat = NULL_VALUE;
		return dat;
	}

	public boolean nodeExists(String name) {
		return data.containsKey(name);
	}

	/** Only returns "tree" nodes, not text ones. */
	public Collection<String> getNodesWithin(String name) {
		return name == null ? this.getTopNodes() : tree.get(name);
	}

	public Collection<String> getTopNodes() {
		return tree.get("$TOP$");
	}

	@Override
	public String toString() {
		return data.toString();
	}

	private static enum LoadFormat {

		JARPATH(),
		FILE();

		private InputStream getInputStream(Object[] data) throws Exception {
			switch(this) {
				case FILE:
					return new FileInputStream((File)data[0]);
				case JARPATH:
					return ((Class)data[0]).getResourceAsStream((String)data[1]);
			}
			return null; //never happens
		}

	}
}

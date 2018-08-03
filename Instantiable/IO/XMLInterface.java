/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.IO.ReikaXMLBase;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;

public class XMLInterface {

	private Document doc;

	private final boolean requireFile;
	private final LoadPoint loadData;
	private final LoadFormat format;
	private final String pathString;
	private Class referenceClass;

	private final HashMap<String, String> data = new HashMap();
	private final MultiMap<String, String> tree = new MultiMap();

	public static final String NULL_VALUE = "#NULL!";

	private boolean hasLoaded;

	private XMLInterface(LoadFormat f, Object raw, String disp, boolean crashIfNull) {
		format = f;
		loadData = new LoadPoint();
		loadData.paths.add(raw);
		requireFile = crashIfNull;
		pathString = disp;
	}

	public XMLInterface(File path, boolean crashIfNull) {
		this(LoadFormat.FILE, path, path.getAbsolutePath(), crashIfNull);
	}

	public XMLInterface(Class root, String path) {
		this(root, path, false);
	}

	public XMLInterface(Class root, String path, boolean crashIfNull) {
		this(LoadFormat.JARPATH, path, path+" relative to "+root.getName(), crashIfNull);
		referenceClass = root;
	}

	public void setFallback(String s) {
		loadData.addEntry(s);
	}

	public void init() {
		try {
			doc = ReikaXMLBase.getXMLDocument(loadData.getInputStream());
			this.readFileToMap();
		}
		catch (FileNotFoundException e) {
			if (requireFile)
				throw new RuntimeException("XML not found at "+pathString, e);
			else
				e.printStackTrace();
		}
		catch (SAXException e) {
			if (requireFile)
				throw new RuntimeException("Could not parse XML at "+pathString, e);
			else
				e.printStackTrace();
		}
		catch (IOException e) {
			if (requireFile)
				throw new RuntimeException("Could not load XML at "+pathString, e);
			else
				e.printStackTrace();
		}
		hasLoaded = true;
	}

	public void reread() {
		hasLoaded = false;
		data.clear();
		tree.clear();
		this.init();
	}

	private void readFileToMap() {
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
		if (!hasLoaded)
			throw new MisuseException("You cannot query an XML data set before reading it from disk!");
		String dat = data.get(name);
		if (dat == null)
			dat = NULL_VALUE;
		return dat;
	}

	public boolean nodeExists(String name) {
		if (!hasLoaded)
			throw new MisuseException("You cannot query an XML data set before reading it from disk!");
		return data.containsKey(name);
	}

	/** Only returns "tree" nodes, not text ones. */
	public Collection<String> getNodesWithin(String name) {
		if (!hasLoaded)
			throw new MisuseException("You cannot query an XML data set before reading it from disk!");
		return name == null ? this.getTopNodes() : tree.get(name);
	}

	public Collection<String> getTopNodes() {
		if (!hasLoaded)
			throw new MisuseException("You cannot query an XML data set before reading it from disk!");
		return tree.get("$TOP$");
	}

	@Override
	public String toString() {
		if (!hasLoaded)
			return "NOT LOADED";
		return data.toString();
	}

	private class LoadPoint {

		private final ArrayList<Object> paths = new ArrayList();

		public void addEntry(String s) {
			Object add = s;
			switch(format) {
				case FILE:
					add = new File(s);
					break;
				case JARPATH:
					break;
			}
			paths.add(add);
		}

		private InputStream getInputStream() throws FileNotFoundException {
			FileNotFoundException ex = null;
			for (int i = 0; i < paths.size(); i++) {
				try {
					return format == LoadFormat.JARPATH ? format.getInputStream(referenceClass, paths.get(i)) : format.getInputStream(paths.get(i));
				}
				catch (FileNotFoundException e) {
					if (i == 0) {
						ex = e;
					}
					else if (i == paths.size()-1) {

					}
				}
			}
			throw ex;
		}

	}

	private static enum LoadFormat {
		JARPATH(),
		FILE();

		private InputStream getInputStream(Object... data) throws FileNotFoundException {
			switch(this) {
				case FILE:
					return new FileInputStream((File)data[0]);
				case JARPATH:
					InputStream ret = ((Class)data[0]).getResourceAsStream((String)data[1]);
					if (ret == null) {
						String s = ((Class)data[0]).getCanonicalName();
						throw new FileNotFoundException(s+" >> "+(String)data[1]);
					}
					return ret;
			}
			return null; //never happens
		}

	}
}

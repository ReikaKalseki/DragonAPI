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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.IO.ReikaFileReader;

public abstract class LuaBlock {

	public final String name;
	private final LuaBlock parent;
	private final HashMap<String, LuaBlock> children = new HashMap();
	private final HashMap<String, String> data = new HashMap();

	private final LuaBlockDatabase tree;

	protected final HashSet<String> requiredElements = new HashSet();

	private boolean isListEntry = false;
	private boolean isList = false;

	protected LuaBlock(String n, LuaBlock lb, LuaBlockDatabase db) {
		if (n.equals("{")) {
			n = Integer.toHexString(System.identityHashCode(this));
			isListEntry = true;
		}
		name = n;
		parent = lb;
		tree = lb != null ? lb.tree : db;
		if (tree == null)
			throw new MisuseException("You cannot create a LuaBlock without a containing tree!");
		if (parent != null) {
			parent.children.put(name, this);
			parent.checkListType();
		}

		requiredElements.add("type");
	}

	private void checkListType() {
		if (!data.isEmpty()) {
			isList = false;
			return;
		}
		for (LuaBlock lb : children.values()) {
			if (!lb.isListEntry()) {
				isList = false;
				return;
			}
		}
		isList = true;
	}

	public final boolean isList() {
		return isList;
	}

	public final boolean isListEntry() {
		return isListEntry;
	}

	public final LuaBlock getParent() {
		return parent;
	}

	public final LuaBlock getTopParent() {
		LuaBlock lb = this;
		while (lb.parent != null) {
			lb = lb.parent;
		}
		return lb;
	}

	public final double getDouble(String key) {
		return this.containsKey(key) ? Double.parseDouble(this.getString(key)) : 0;
	}

	public final boolean getBoolean(String key) {
		return this.containsKey(key) ? Boolean.parseBoolean(this.getString(key)) : false;
	}

	public final int getInt(String key) {
		return this.containsKey(key) ? Integer.parseInt(this.getString(key)) : 0;
	}

	public final long getLong(String key) {
		return Long.parseLong(this.getString(key));
	}

	public final String getString(String key) {
		if (data.containsKey(key))
			return data.get(key);
		if (!this.canInherit(key))
			throw new IllegalArgumentException("Missing key '"+key+"' for '"+name+"'");
		return this.inherit(key);
	}

	public final Collection<String> getDataValues() {
		return Collections.unmodifiableCollection(data.values());
	}

	public final void putData(String key, String val) {
		data.put(key, val);
	}

	public final boolean containsKey(String key) {
		return data.containsKey(key);
	}

	public boolean hasChild(String s) {
		return children.containsKey(s);
	}

	public final Collection<LuaBlock> getChildren() {
		return Collections.unmodifiableCollection(children.values());
	}

	private String inherit(String key) {
		LuaBlock b = this;
		Collection<String> steps = new ArrayList();
		LuaBlock orig = b;
		while (!b.data.containsKey("inherit") && b.parent != null) {
			steps.add(b.name);
			b = b.parent;
		}
		String inherit = b.data.get("inherit");
		if (inherit == null)
			return "[NULL KEY INHERIT]";
		LuaBlock lb = tree.getBlock(inherit);
		if (lb == null)
			return "[NULL PARENT INHERIT]";
		for (String s : steps) {
			if (lb.children.containsKey(s))
				lb = lb.children.get(s);
			else
				throw new IllegalStateException("'"+orig.parent.name+"/"+orig.name+"' tried to inherit property '"+key+"', but could not.");
		}
		return lb.data.containsKey(key) ? lb.getString(key) : "[NULL DATA]";
	}

	private boolean canInherit(String key) {
		return /*!requiredElements.contains(name) && */!requiredElements.contains(key);
	}

	public final LuaBlock getChild(String key) {
		return children.containsKey(key) ? children.get(key) : this.inheritChild(key);
	}

	private LuaBlock inheritChild(String key) {
		LuaBlock b = this;
		Collection<String> steps = new ArrayList();
		while (!b.data.containsKey("inherit") && b.parent != null) {
			if (b != this)
				steps.add(b.name);
			b = b.parent;
		}
		String inherit = b.data.get("inherit");
		if (inherit == null)
			return null;
		LuaBlock lb = tree.getBlock(inherit);
		if (lb == null)
			return null;
		for (String s : steps) {
			lb = lb.children.get(s);
		}
		return lb.children.containsKey(key) ? lb.children.get(key) : null;
	}

	@Override
	public String toString() {
		return this.toString(0);
	}

	private String toString(int indent) {
		StringBuilder sb = new StringBuilder();

		sb.append("\n");
		sb.append(this.getIndent("----", indent)+"-------------"+name+"-------------\n");
		sb.append(this.getIndent("====", indent)+"=============DATA=============\n");
		for (String s : data.keySet()) {
			String val = data.get(s);
			sb.append(this.getIndent("\t", indent)+s+"="+val);
			sb.append("\n");
		}
		if (!children.isEmpty()) {
			sb.append("\n");
			sb.append(this.getIndent("====", indent)+"=============CHILDREN=============\n");
			for (LuaBlock lb : children.values()) {
				sb.append(lb.toString(indent+1));
			}
		}
		sb.append(this.getIndent("----", indent)+"---------------------------------------\n");
		sb.append("\n");
		sb.append("\n");

		return sb.toString();
	}

	private String getIndent(String rpt, int idt) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < idt; i++) {
			sb.append(rpt);
		}
		return sb.toString();
	}

	private static class BasicLuaBlock extends LuaBlock {

		protected BasicLuaBlock(String n, LuaBlock lb, LuaBlockDatabase db) {
			super(n, lb, db);
		}

	}

	public static class LuaBlockDatabase {

		private LuaBlock block = new BasicLuaBlock("top", null, this);

		private final HashMap<String, LuaBlock> rawData = new HashMap();

		public LuaBlockDatabase() {
			//this.addBlock("top", block);
		}

		public final void loadFromFile(File f) {
			ArrayList<String> li = ReikaFileReader.getFileAsLines(f, false);
			ArrayList<ArrayList<String>> data = new ArrayList();
			int bracketLevel = 0;
			for (String s : li) {
				s = this.cleanString(s);
				if (s.isEmpty())
					continue;

				if (s.contains("{")) {
					bracketLevel++;
					if (s.endsWith(" = {"))
						s = s.substring(0, s.length()-4);
					block = new BasicLuaBlock(s, block, this);
				}
				else if (s.contains("}")) {
					block = block.getParent();
					bracketLevel--;
				}

				if (!s.equals("{") && !s.equals("}") && !s.equals(block.name)) {
					s = s.replaceAll("\"", "");
					String[] parts = s.split("=");
					if (parts.length == 2)
						block.putData(parts[0].substring(0, parts[0].length()-1), parts[1].substring(1));
					else
						block.putData(String.valueOf(block.data.size()), s);
				}
			}

			if (bracketLevel != 0) {
				throw new IllegalArgumentException("Malformed file: bracket mismatch");
			}
		}

		private String cleanString(String s) {
			if (s.startsWith("//") || s.startsWith("--"))
				return "";

			s = s.replaceAll("\t", "");
			if (s.contains("--")) {
				s = s.substring(0, s.indexOf("--"));
			}
			if (s.contains("//")) {
				s = s.substring(0, s.indexOf("//"));
			}
			if (s.length() > 0) {
				while (!s.isEmpty() && s.charAt(s.length()-1) == ' ')
					s = s.substring(0, s.length()-1);
				while (!s.isEmpty() && s.charAt(0) == ' ')
					s = s.substring(1, s.length());
			}
			return s;
		}

		public void addBlock(String key, LuaBlock b) {
			rawData.put(key, b);
		}

		public void clear() {
			rawData.clear();
		}

		public LuaBlock getBlock(String key) {
			return rawData.get(key);
		}

		public LuaBlock getRootBlock() {
			return block.getTopParent();
		}

	}

	public final HashMap<String, Object> asHashMap() {
		HashMap<String, Object> ret = new HashMap();
		for (String s : data.keySet()) {
			ret.put(s, this.parseObject(data.get(s)));
		}
		for (String s : children.keySet()) {
			LuaBlock b = children.get(s);
			ret.put(s, this.getObject(b));
		}
		return ret;
	}

	public final List<Object> asList() {
		List ret = new ArrayList();

		for (LuaBlock b : children.values()) {
			ret.add(this.getObject(b));
		}

		return ret;
	}

	private Object getObject(LuaBlock b) {
		return b.isListEntry() && b.data.size() == 1 && b.children.isEmpty() ? this.parseObject(b.data.values().iterator().next()) : b.isList() ? b.asList() : b.asHashMap();
	}

	private Object parseObject(String s) {
		if (s.equalsIgnoreCase("true"))
			return true;
		if (s.equalsIgnoreCase("false"))
			return false;
		try {
			return (int)Integer.parseInt(s);
		}
		catch (Exception e) {

		}
		try {
			return (long)Long.parseLong(s);
		}
		catch (Exception e) {

		}
		try {
			return (double)Double.parseDouble(s);
		}
		catch (Exception e) {

		}
		return s;
	}

}

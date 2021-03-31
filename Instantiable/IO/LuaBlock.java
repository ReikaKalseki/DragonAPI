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
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper.PrimitiveType;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

public abstract class LuaBlock {

	private static final Comparator<String> outputSorter = new OutputSorter();

	private static final Pattern TYPE_SPECIFIER = Pattern.compile("\\[(.*?)\\]");

	public static final String NULL_KEY_INHERIT = "[NULL KEY INHERIT]";
	public static final String NULL_PARENT_INHERIT = "[NULL PARENT INHERIT]";
	public static final String NULL_DATA = "[NULL DATA]";

	public final boolean isRoot;
	public final String name;
	private final LuaBlock parent;
	private final LinkedHashMap<LuaBlockKey, LuaBlock> children = new LinkedHashMap(); //linked to keep order
	private final LinkedHashMap<String, String> data = new LinkedHashMap();

	protected final LuaBlockDatabase tree;

	protected final HashSet<String> requiredElements = new HashSet();

	private boolean isListEntry = false;
	private boolean isList = true;

	private HashMap<String, String> comments = new HashMap();

	protected LuaBlock(String n, LuaBlock parent, LuaBlockDatabase db) {
		isRoot = parent == null;
		if (n.equals("{")) {
			n = Integer.toHexString(System.identityHashCode(this));
			isListEntry = true;
		}
		name = n;
		this.parent = parent;
		tree = parent != null ? parent.tree : db;
		if (tree == null)
			throw new MisuseException("You cannot create a LuaBlock without a containing tree!");
		if (parent != null) {
			parent.children.put(this.createKey(name), this);
		}

		requiredElements.add("type");
	}

	private LuaBlockKey createKey(String n) {
		return tree.hasDuplicateKeys ? new LuaBlockKey(n, n+"_"+parent.children.size()) : new LuaBlockKey(n);
	}

	public final boolean isList() {
		return isList && !isRoot && !(data.isEmpty() && !children.isEmpty());
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
		return this.containsKeyInherit(key) ? Double.parseDouble(this.getString(key)) : 0;
	}

	public final boolean getBoolean(String key) {
		return this.containsKeyInherit(key) ? Boolean.parseBoolean(this.getString(key)) : false;
	}

	public final int getInt(String key) {
		return this.containsKeyInherit(key) ? this.parseInt(this.getString(key)) : 0;
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

	private int parseInt(String s) {
		if (s.startsWith("0x"))
			return Integer.parseInt(s.substring(2), 16);
		else if (s.startsWith("0b"))
			return Integer.parseInt(s.substring(2), 2);
		else if (s.startsWith("0o"))
			return Integer.parseInt(s.substring(2), 8);
		else
			return Integer.decode(s);
	}

	private boolean isString(String s) {
		return !Character.isDigit(s.charAt(s.charAt(0) == '-' && s.length() > 1 ? 1 : 0)) && !s.equalsIgnoreCase("true") && !s.equalsIgnoreCase("false");
	}

	public final Collection<String> getKeys() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	public final Collection<String> getDataValues() {
		return Collections.unmodifiableCollection(data.values());
	}

	public final void putData(String key, boolean val) {
		this.putData(key, String.valueOf(val));
	}

	public final void putData(String key, double val) {
		this.putData(key, String.valueOf(val));
	}

	public final void putData(String key, int val) {
		this.putData(key, String.valueOf(val));
	}

	public final void putData(String key, String val) {
		data.put(key, val);
		isList = false;
	}

	public final void addListData(String val) {
		if (!isList)
			throw new MisuseException("You can only add list data to list-type entries!");
		data.put(String.valueOf(data.size()), val);
	}

	public final boolean containsKey(String key) {
		return data.containsKey(key);
	}

	public final boolean containsKeyInherit(String key) {
		if (data.containsKey(key))
			return true;
		return !this.inherit(key).startsWith("[NULL");
	}

	public boolean hasChild(String s) {
		return children.containsKey(this.createKey(s));
	}

	public final Collection<LuaBlock> getChildren() {
		return Collections.unmodifiableCollection(children.values());
	}

	private String inherit(String key) {
		LuaBlock b = this;
		Collection<LuaBlockKey> steps = new ArrayList();
		LuaBlock orig = b;
		while (!b.data.containsKey("inherit") && b.parent != null) {
			steps.add(this.createKey(b.name));
			b = b.parent;
		}
		String inherit = b.data.get("inherit");
		if (inherit == null)
			return NULL_KEY_INHERIT;
		LuaBlock lb = tree.getBlock(inherit);
		if (lb == null)
			return NULL_PARENT_INHERIT;
		for (LuaBlockKey s : steps) {
			if (lb.children.containsKey(s))
				lb = lb.children.get(s);
			else
				throw new IllegalStateException("'"+orig.parent.name+"/"+orig.name+"' tried to inherit property '"+key+"', but could not.");
		}
		//ReikaJavaLibrary.pConsole("'"+b.getString("type")+"' inheriting property '"+steps+"/"+key+"' from parent '"+inherit+"'");
		return lb.data.containsKey(key) ? lb.getString(key) : NULL_DATA;
	}

	private boolean canInherit(String key) {
		return /*!requiredElements.contains(name) && */!requiredElements.contains(key) && !key.equals("inherit");
	}

	public final LuaBlock getChild(String s) {
		LuaBlockKey key = this.createKey(s);
		return children.containsKey(key) ? children.get(key) : this.inheritChild(s);
	}

	private LuaBlock inheritChild(String sg) {
		LuaBlock b = this;
		Collection<LuaBlockKey> steps = new ArrayList();
		while (!b.data.containsKey("inherit") && b.parent != null) {
			if (b != this)
				steps.add(this.createKey(b.name));
			b = b.parent;
		}
		String inherit = b.data.get("inherit");
		if (inherit == null)
			return null;
		LuaBlock lb = tree.getBlock(inherit);
		if (lb == null)
			return null;
		for (LuaBlockKey s : steps) {
			lb = lb.children.get(s);
		}
		LuaBlockKey key = this.createKey(sg);
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

	/** Null key for a top-level comment (ie tagging the start of this block) */
	public void setComment(String key, String comment) {
		HashMap<String, String> map = comments;
		String ckey = key;
		if (key == null && parent != null) {
			ckey = name;
			map = parent.comments;
		}
		map.put(ckey, comment);
	}

	//public abstract LuaBlock createChild(String name);

	private static class BasicLuaBlock extends LuaBlock {

		protected BasicLuaBlock(String n, LuaBlock lb, LuaBlockDatabase db) {
			super(n, lb, db);
		}

	}

	public static final class ItemStackLuaBlock extends LuaBlock {

		public ItemStackLuaBlock(String n, LuaBlock parent, LuaBlockDatabase db) {
			super(n, parent, db);
		}

		public void write(ItemStack is, boolean writeSize) {
			this.putData("item_id", Item.itemRegistry.getNameForObject(is.getItem()));
			this.putData("metadata", String.valueOf(is.getItemDamage()));
			if (writeSize)
				this.putData("stack_size", String.valueOf(is.stackSize));
			this.putData("display_name", is.getDisplayName());
			LuaBlock nbt = is.stackTagCompound != null ? new NBTLuaBlock("nbt", this, tree, is.stackTagCompound, true) : null;
		}
	}

	public static final class NBTLuaBlock extends LuaBlock {

		public NBTLuaBlock(String n, LuaBlock parent, LuaBlockDatabase db, NBTTagCompound tag, boolean parseEnchants) {
			super(n, parent, db);
			HashMap<String, ?> map = ReikaNBTHelper.readMapFromNBT(tag);
			if (parseEnchants)
				map = this.parseEnchantments(map);
			this.writeData(map);
		}

		private HashMap parseEnchantments(HashMap map) {
			Object ench = map.remove("StoredEnchantments");
			if (ench == null) {
				ench = map.remove("ench");
			}
			if (ench instanceof ArrayList) {
				ArrayList<String> li = new ArrayList();
				ArrayList<HashMap> data = (ArrayList<HashMap>)ench;
				for (HashMap<String, Short> in : data) {
					short lvl = in.get("lvl");
					short id = in.get("id");
					li.add(Enchantment.enchantmentsList[id].getTranslatedName(lvl));
				}
				map.put("Enchantments", li);
			}
			return map;
		}
	}

	public static class LuaBlockDatabase {

		private LuaBlock activeBlock = new BasicLuaBlock("top", null, this);

		private final HashMap<String, LuaBlock> rawData = new HashMap();

		public boolean hasDuplicateKeys = false;

		public LuaBlockDatabase() {
			//this.addBlock("top", block);
		}

		public final void loadFromFile(File f) {
			this.loadFromLines(ReikaFileReader.getFileAsLines(f, false));
		}

		public final void loadFromLines(ArrayList<String> li) {
			//ArrayList<ArrayList<String>> data = new ArrayList();
			int bracketLevel = 0;
			for (String s : li) {
				s = this.cleanString(s);
				if (s.isEmpty())
					continue;

				if (s.contains("{")) {
					bracketLevel++;
					if (s.endsWith(" = {"))
						s = s.substring(0, s.length()-4);
					try {
						activeBlock = this.createChild(s, activeBlock);
					}
					catch (Exception e) {
						DragonAPICore.logError("Failed to construct proper child LuaBlock for "+s+"' in "+this+": ");
						e.printStackTrace();
						activeBlock = new BasicLuaBlock(s, activeBlock, this);
					}
				}
				else if (s.contains("}")) {
					activeBlock = activeBlock.getParent();
					bracketLevel--;
				}

				if (!s.equals("{") && !s.equals("}") && !s.equals(activeBlock.name)) {
					s = s.replaceAll("\"", "");
					String[] parts = s.split("=");
					if (parts.length == 2) {
						String s1 = parts[0].substring(0, parts[0].length()-1);
						if (s1.charAt(s1.length()-1) == ' ')
							s1 = s1.substring(1);
						String s2 = parts[1];
						if (s2.charAt(0) == ' ')
							s2 = s2.substring(1);
						activeBlock.putData(s1, s2);
					}
					else {
						activeBlock.addListData(s);
					}
				}
			}

			if (bracketLevel != 0) {
				throw new IllegalArgumentException("Malformed file: bracket mismatch");
			}
		}

		private LuaBlock createChild(String s, LuaBlock parent) throws Exception {
			Class<? extends LuaBlock> c = parent.getChildBlockType();
			Constructor<LuaBlock> ctr = (Constructor<LuaBlock>)c.getDeclaredConstructor(String.class, LuaBlock.class, LuaBlockDatabase.class);
			ctr.setAccessible(true);
			LuaBlock child = ctr.newInstance(s, parent, this);
			return child;
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
			return activeBlock.getTopParent();
		}

		public LuaBlock createRootBlock() {
			LuaBlock lb = new BasicLuaBlock("base", null, this);
			this.addBlock("base", lb);
			return lb;
		}

	}

	public final HashMap<String, Object> asHashMap() {
		HashMap<String, Object> ret = new HashMap();
		for (String s : data.keySet()) {
			ret.put(s, this.parseObject(data.get(s)));
		}
		for (LuaBlockKey s : children.keySet()) {
			LuaBlock b = children.get(s);
			ret.put(s.lookupKey, this.getObject(b));
		}
		return ret;
	}

	public final WeightedRandom<String> asWeightedRandom() {
		WeightedRandom<String> wr = new WeightedRandom();
		for (String s : data.keySet()) {
			wr.addEntry(s, this.getDouble(s));
		}
		return wr;
	}

	public void writeData(List li) {
		int idx = 0;
		for (Object o : li) {
			if (o instanceof Map) {
				LuaBlock child = new BasicLuaBlock(String.valueOf(idx), this, tree);
				child.writeData((Map)o);
			}
			else if (o instanceof List) {
				LuaBlock child = new BasicLuaBlock(String.valueOf(idx), this, tree);
				child.writeData((List)o);
			}
			else {
				this.putData(String.valueOf(idx), String.valueOf(o));
			}
			idx++;
		}
	}

	public void writeData(Map<String, ?> map) {
		for (Entry<String, ?> e : map.entrySet()) {
			if (e.getValue() instanceof Map) {
				LuaBlock child = new BasicLuaBlock(e.getKey(), this, tree);
				child.writeData((Map)e.getValue());
			}
			else if (e.getValue() instanceof List) {
				LuaBlock child = new BasicLuaBlock(e.getKey(), this, tree);
				child.writeData((List)e.getValue());
			}
			else {
				this.putData(e.getKey(), String.valueOf(e.getValue()));
			}
		}
	}

	public final List<Object> asList() {
		List ret = new ArrayList();

		for (LuaBlock b : children.values()) {
			ret.add(this.getObject(b));
		}

		return ret;
	}

	private Object getObject(LuaBlock b) {
		return b.isList() && b.data.size() == 1 && b.children.isEmpty() ? this.parseObject(b.data.values().iterator().next()) : b.isList() ? b.asList() : b.asHashMap();
	}

	private Object parseObject(String s) {
		if (s.equalsIgnoreCase("true"))
			return true;
		if (s.equalsIgnoreCase("false"))
			return false;
		PrimitiveType override = null;
		if (s.contains("[datatype=")) {
			String type = s.replace("datatype=", "");
			Matcher m = TYPE_SPECIFIER.matcher(type);
			type = m.find() ? m.group(1) : null;
			s = s.replace("[datatype="+type+"]", "");
			try {
				override = PrimitiveType.valueOf(type.toUpperCase(Locale.ENGLISH));
			}
			catch (IllegalArgumentException e) {

			}
		}
		if (override != null) {
			try {
				switch(override) {
					case BYTE:
						return (byte)Byte.parseByte(s);
					case SHORT:
						return (byte)Short.parseShort(s);
					case LONG:
						return (long)Long.parseLong(s);
					case FLOAT:
						return (float)Float.parseFloat(s);
					case DOUBLE:
						return (float)Double.parseDouble(s);
					default:
						break;
				}
			}
			catch (Exception e) {

			}
		}
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

	public ArrayList<String> writeToStrings() {
		return this.writeToStrings(1);
	}

	private ArrayList<String> writeToStrings(int indent) {
		ArrayList<String> li = new ArrayList();
		String pre = ReikaStringParser.getNOf("\t", indent);
		if (indent == 1) {
			String s = "{";
			li.add(s);
		}
		ArrayList<String> keys = new ArrayList(data.keySet());
		Collections.sort(keys, outputSorter);
		for (String s : keys) {
			String val = data.get(s);
			if (this.isString(val))
				val = "\""+val+"\"";
			if (this.isList()) {
				li.add(pre+val);
			}
			else {
				String put = pre+s+" = "+val;
				String comment = comments.get(s);
				if (comment != null) {
					put = put+" --"+comment;
				}
				li.add(put);
			}
		}
		ArrayList<LuaBlockKey> keys2 = new ArrayList(children.keySet());
		Collections.sort(keys2);
		for (LuaBlockKey s : keys2) {
			LuaBlock c = children.get(s);
			String put;
			if (c.isListEntry || s.name.equals("-")) {
				put = pre+"{";
			}
			else {
				put = pre+s.name+" = {";
			}
			String comment = comments.get(s.name);
			if (comment != null) {
				put = put+" --"+comment;
			}
			li.add(put);
			li.addAll(c.writeToStrings(indent+1));
			li.add(pre+"}");
		}
		if (indent == 1)
			li.add("}");
		return li;
	}

	public Class<? extends LuaBlock> getChildBlockType() {
		return this.getClass();
	}

	public static boolean isErrorCode(String s) {
		return NULL_DATA.equals(s) || NULL_KEY_INHERIT.equals(s) || NULL_PARENT_INHERIT.equals(s);
	}

	private static class OutputSorter implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			if (o1.equals("type"))
				return Integer.MIN_VALUE;
			else if (o2.equals("type"))
				return Integer.MAX_VALUE;
			else
				return o1.compareToIgnoreCase(o2);
		}

	}

	private static class LuaBlockKey implements Comparable<LuaBlockKey> {

		public final String name;
		public final String lookupKey;

		private LuaBlockKey(String s) {
			this(s, s);
		}

		private LuaBlockKey(String s, String k) {
			name = s;
			lookupKey = k;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof LuaBlockKey && ((LuaBlockKey)o).lookupKey.equals(lookupKey);
		}

		@Override
		public int hashCode() {
			return lookupKey.hashCode();
		}

		@Override
		public int compareTo(LuaBlockKey o) {
			return name.compareTo(o.name);
		}

	}

}

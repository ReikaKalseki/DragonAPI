/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;

public final class ReikaJavaLibrary extends DragonAPICore {

	private static int maxRecurse = -1;
	public static boolean dumpStack = false;
	public static boolean silent = false;

	private static final HashMap<String, Object> threadLock = new HashMap();

	/** Generic write-to-console function. Args: Object */
	public static void pConsole(Object obj) {
		pConsole(Level.INFO, obj);
	}

	/** Generic write-to-console function. Args: Object */
	public static void pConsole(Level level, Object obj) {
		if (silent)
			return;
		if (obj == null) {
			writeLineToConsoleAndLogs(level, "null arg");
		}
		else {
			Class cl = obj.getClass();
			//if (obj instanceof Object[]) {
			//	writeLineToConsoleAndLogs(level, Arrays.toString((Object[])obj));
			//}
			if (cl != String.class && cl != Integer.class && cl != Boolean.class)
				writeLineToConsoleAndLogs(level, String.valueOf(obj)+" of "+String.valueOf(cl));
			else
				writeLineToConsoleAndLogs(level, String.valueOf(obj));
		}
		if (dumpStack)
			dumpStack();
	}

	public static void dumpStack() {
		writeLineToConsoleAndLogs(Level.WARN, "Stack Trace:");
		StackTraceElement[] s = new Exception("Stack Trace").getStackTrace();
		for (int i = 1; i < s.length; i++)
			writeLineToConsoleAndLogs(Level.WARN, "\t"+s[i].toString());
	}

	private static void writeLineToConsoleAndLogs(Level level, String s) {
		//System.out.println(s);
		s = s.replaceAll("%", "%%"); //because FML logger fails at this
		FMLLog.log(level, s);
	}

	public static void spamConsole(Object obj) {
		String sg = String.valueOf(obj);
		for (int i = 0; i < 16; i++)
			pConsole(sg);
	}

	public static void writeCoord(World world, int x, int y, int z) {
		pConsole(world.getBlock(x, y, z)+":"+world.getBlockMetadata(x, y, z)+" @ "+x+", "+y+", "+z+" @DIM"+world.provider.dimensionId);
	}

	public static void pConsole(Object obj, Side s) {
		if (FMLCommonHandler.instance().getEffectiveSide() == s)
			pConsole(obj);
	}

	public static void pConsole(Object obj, Side s, boolean con) {
		if (con)
			pConsole(obj, s);
	}

	public static void pConsole(Object obj, boolean con) {
		if (con)
			pConsole(obj);
	}

	/** A complement to Java's built-in List-to-Array. Args: Array of any object (ints, strings, etc). */
	public static ArrayList makeListFromArray(Object[] obj) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < obj.length; i++) {
			li.add(obj[i]);
		}
		return li;
	}

	public static ArrayList makeIntListFromArray(int[] obj) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < obj.length; i++) {
			li.add(obj[i]);
		}
		return li;
	}

	public static ArrayList makeListFrom(Object obj) {
		ArrayList li = new ArrayList();
		li.add(obj);
		return li;
	}

	public static ArrayList makeListFrom(Object... obj) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < obj.length; i++) {
			li.add(obj[i]);
		}
		return li;
	}

	public static boolean isValidInteger(String s) {
		if (s.contentEquals("-"))
			return true;
		try {
			Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static int safeIntParse(String s) {
		boolean neg = false;
		int num = 0;
		if (s.startsWith("-")) {
			s = s.substring(1);
			neg = true;
		}
		if (s.matches("\\d+")) {
			num = Integer.parseInt(s);
		}
		return neg ? -num : num;
	}

	public static void printLine(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++)
			sb.append("-");
		pConsole(sb.toString());
	}

	public static <T, E> T getHashMapKeyByValue(HashMap<T,E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/** Copies a list. Can accept a null argument. */
	public static <T> List<T> copyList(List<T> li) {
		if (li == null)
			return null;
		List<T> n = new ArrayList<T>(li);
		return n;
	}

	public static boolean doesClassExist(String cl) {
		try {
			Class.forName(cl);
			return true;
		}
		catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static Class getClassNoException(String cl) {
		try {
			return Class.forName(cl);
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}

	/** Initializes a class. */
	public static void initClass(Class c) {
		if (c == null) {
			pConsole("DRAGONAPI: Cannot initalize a null class!");
			dumpStack();
			return;
		}
		try {
			Class.forName(c.getName(), true, ReikaJavaLibrary.class.getClassLoader());
		}
		catch (ClassNotFoundException e) {
			pConsole("DRAGONAPI: Failed to initalize class "+c.getName()+"! Class not found!");
			e.printStackTrace();
		}
		catch (NoClassDefFoundError e) {
			pConsole("DRAGONAPI: Failed to initalize class "+c.getName()+"! Class not found!");
			e.printStackTrace();
		}
		catch (RuntimeException e) {
			pConsole("DRAGONAPI: Failed to initalize class "+c.getName()+"!");
			String s = e.getMessage();
			if (s.endsWith("for invalid side SERVER")) {
				pConsole("Attemped to load a clientside class on the server! This is a significant programming error!");
			}
			e.printStackTrace();
		}
	}

	public static void initClassWithSubs(Class c) {
		initClass(c);
		for (Class c2 : c.getDeclaredClasses()) {
			initClass(c2);
		}
	}

	public static boolean listContainsArray(List<int[]> li, int[] arr) {
		for(int i = 0; i < li.size(); i++) {
			if (Arrays.equals(li.get(i), arr)){
				return true;
			}
		}
		return false;
	}

	public static int getEnumLengthWithoutInitializing(Class<? extends Enum> c) {
		Field[] q = c.getFields();
		int count = 0;
		for (int i = 0; i < q.length; i++) {
			Field f = q[i];
			if (f.isEnumConstant())
				count++;
		}
		return count;
	}

	public static ArrayList<String> getEnumEntriesWithoutInitializing(Class<? extends Enum> c) {
		ArrayList<String> li = new ArrayList();
		Field[] q = c.getFields();
		for (int i = 0; i < q.length; i++) {
			Field f = q[i];
			if (f.isEnumConstant())
				li.add(f.getName());
		}
		return li;
	}

	/** Returns the maximum allowable depth of recursion on the current system.
	 * Keep in mind that this number is the <i>total</i> stack depth and as such contains some
	 * vanilla MC and Forge calls as well. Subtract 100 or so to be safe. */
	public static int getMaximumRecursiveDepth() {
		if (maxRecurse <= 0) {
			recurse(0);
		}
		return maxRecurse;
	}

	private static int recurse(int i) {
		maxRecurse = Math.max(i, maxRecurse);
		//pConsole(i+":"+maxRecurse);
		try {
			recurse(i+1);
		}
		catch (StackOverflowError e) {
			return i;
		}
		return 0;
	}

	public static void toggleStackTrace() {
		dumpStack = !dumpStack;
	}

	public static void toggleSilentMode() {
		silent = !silent;
	}

	public static Class[] getObjectClasses(Object... objs) {
		Class[] c = new Class[objs.length];
		for (int i = 0; i < c.length; i++) {
			c[i] = getClassOf(objs[i]);
		}
		return c;
	}

	public static Class getClassOf(Object o) {
		Class p = getPrimitiveClass(o);
		return p != null ? p : o.getClass();
	}

	private static Class getPrimitiveClass(Object o) {
		String name = o.getClass().getSimpleName().toLowerCase();
		if (name.equals("byte"))
			return byte.class;
		if (name.equals("short"))
			return short.class;
		if (name.equals("int"))
			return int.class;
		if (name.equals("integer"))
			return int.class;
		if (name.equals("long"))
			return long.class;
		if (name.equals("char"))
			return char.class;
		if (name.equals("character"))
			return char.class;
		if (name.equals("float"))
			return float.class;
		if (name.equals("double"))
			return double.class;
		if (name.equals("boolean"))
			return boolean.class;
		if (name.equals("void"))
			return void.class;
		return null;
	}

	public static <K,T> boolean collectionMapContainsValue(HashMap<K, Collection<T>> map, T value) {
		for (Collection<T> c : map.values()) {
			if (c != null && c.contains(value))
				return true;
		}
		return false;
	}

	public static byte[] streamToBytes(InputStream in) {
		ArrayList<Byte> li = new ArrayList();
		try {
			int ret = in.read();
			while (ReikaMathLibrary.isValueInsideBoundsIncl(0, 255, ret)) {
				li.add((byte)ret);
				ret = in.read();
			}
			byte[] arr = new byte[li.size()];
			for (int i = 0; i < li.size(); i++) {
				arr[i] = li.get(i);
			}
			return arr;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <I, O> Collection<O> getConstructedCollection(Collection<I> inputs, Class<I> ci, Class<O> co) {
		try {
			return getConstructedCollection(inputs, co.getConstructor(ci));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <I, O> Collection<O> getConstructedCollection(Collection<I> inputs, Constructor<O> c) {
		Collection<O> outputs = new ArrayList();
		try {
			for (I in : inputs) {
				O out = c.newInstance(in);
				outputs.add(out);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return outputs;
	}

	public static HashMap sortMapByValues(HashMap map) {
		List<Map.Entry> list = new LinkedList(map.entrySet());
		Collections.sort(list, new MapValueSorter());
		HashMap sortedHashMap = new LinkedHashMap();
		for (Map.Entry e : list) {
			sortedHashMap.put(e.getKey(), e.getValue());
		}
		return sortedHashMap;
	}

	private static class MapValueSorter<V> implements Comparator<Map.Entry<V, Comparable>> {

		public int compare(Map.Entry<V, Comparable> o1, Map.Entry<V, Comparable> o2) {
			return (o1.getValue()).compareTo(o2.getValue());
		}
	}

	public static <E> E[] collectionToArray(Collection<E> li) {
		E[] arr = (E[])new Object[li.size()];
		int i = 0;
		for (E ps : li) {
			arr[i] = ps;
			i++;
		}
		return arr;
	}

	public static <E> E getRandomListEntry(ArrayList<E> li) {
		return li.isEmpty() ? null : li.get(rand.nextInt(li.size()));
	}

	public static <E> E getRandomCollectionEntry(Collection<E> c) {
		return c.isEmpty() ? null : new ArrayList<E>(c).get(rand.nextInt(c.size()));
	}

	public static String getTopLevelPackage(Class c) {
		String n = c.getName();
		return n.substring(0, n.indexOf('.'));
	}

	public static <E> Collection<E> getCompoundCollection(Collection<Collection<E>> colls) {
		Collection<E> c = new ArrayList();
		for (Collection<E> c2 : colls) {
			c.addAll(c2);
		}
		return c;
	}
}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import Reika.DragonAPI.DragonAPICore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public final class ReikaJavaLibrary extends DragonAPICore {

	private static int maxRecurse = -1;
	public static boolean dumpStack = false;
	public static boolean silent = false;

	/** Generic write-to-console function. Args: Object */
	public static void pConsole(Object obj) {
		if (silent)
			return;
		if (obj == null) {
			System.out.println("null arg");
			return;
		}
		Class cl = obj.getClass();
		if (cl != String.class && cl != Integer.class && cl != Boolean.class)
			System.out.println(String.valueOf(obj)+" of "+String.valueOf(cl));
		else
			System.out.println(String.valueOf(obj));
		if (dumpStack)
			Thread.dumpStack();
	}

	public static void spamConsole(Object obj) {
		String sg = String.valueOf(obj);
		for (int i = 0; i < 16; i++)
			System.out.println(sg);
	}

	public static void pConsole(Object obj, Side s) {
		if (FMLCommonHandler.instance().getEffectiveSide() == s)
			pConsole(obj);
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

	/** Initializes a class. */
	public static void initClass(Class c) {
		if (c == null) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot initalize a null class!");
			Thread.dumpStack();
			return;
		}
		try {
			Class.forName(c.getCanonicalName(), true, ReikaJavaLibrary.class.getClassLoader());
		}
		catch (ClassNotFoundException e) {}
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
		//ReikaJavaLibrary.pConsole(i+":"+maxRecurse);
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
}
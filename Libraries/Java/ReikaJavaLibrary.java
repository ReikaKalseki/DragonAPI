/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import Reika.DragonAPI.DragonAPICore;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public final class ReikaJavaLibrary extends DragonAPICore {

	/** Generic write-to-console function. Args: Object */
	public static void pConsole(Object obj) {
		if (obj == null) {
			System.out.println("null");
			return;
		}
		Class cl = obj.getClass();
		if (cl != String.class && cl != Integer.class && cl != Boolean.class)
			System.out.println(String.valueOf(obj)+" of "+String.valueOf(cl));
		else
			System.out.println(String.valueOf(obj));
		//Thread.dumpStack();
	}

	public static void spamConsole(Object obj) {
		for (int i = 0; i < 16; i++)
			System.out.println(String.valueOf(obj));
	}

	public static void pConsoleSideOnly(Object obj, Side s) {
		if (FMLCommonHandler.instance().getEffectiveSide() == s)
			pConsole(obj);
	}

	public static void pConsoleIf(Object obj, boolean con) {
		if (con)
			pConsole(obj);
	}

	/** A complement to Java's built-in List-to-Array. Args: Array of any object (ints, strings, etc). */
	public static List makeListFromArray(Object[] obj) {
		List li = new ArrayList();
		for (int i = 0; i < obj.length; i++) {
			li.add(obj[i]);
		}
		return li;
	}

	public static List makeListFrom(Object obj) {
		List li = new ArrayList();
		li.add(obj);
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
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			return 0;//Integer.MIN_VALUE;
		}
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

	public static <T> List<T> copyList(List<T> li) {
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

	public static void initClass(Class c) {
		try {
			Class.forName(c.getCanonicalName(), true, c.getClassLoader());
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
}

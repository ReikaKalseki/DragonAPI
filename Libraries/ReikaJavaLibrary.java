/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import Reika.DragonAPI.DragonAPICore;

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

	public static void pConsoleSideOnly(Object obj, int s) {
		Side side;
		switch (s) {
			case 0:
				side = Side.SERVER;
				break;
			case 1:
				side = Side.CLIENT;
				break;
			case 2:
				side = Side.BUKKIT;
				break;
			default:
				side = FMLCommonHandler.instance().getEffectiveSide();
		}
		if (FMLCommonHandler.instance().getEffectiveSide() == side)
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

	public static String stripSpaces(String s) {
		return s.replaceAll("\\s","");
	}

	public static String subtractFrom(String src, String p) {
		int len = p.length();
		return src.substring(len);
	}

	public static void printLine(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++)
			sb.append("-");
		pConsole(sb.toString());
	}
}

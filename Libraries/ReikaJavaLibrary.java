/*******************************************************************************
 * @author Reika
 * 
 * This code is the property of and owned and copyrighted by Reika.
 * This code is provided under a modified visible-source license that is as follows:
 * 
 * Any and all users are permitted to use the source for educational purposes, or to create other mods that call
 * parts of this code and use DragonAPI as a dependency.
 * 
 * Unless given explicit written permission - electronic writing is acceptable - no user may redistribute this
 * source code nor any derivative works. These pre-approved works must prominently contain this copyright notice.
 * 
 * Additionally, no attempt may be made to achieve monetary gain from this code by anyone except the original author.
 * In the case of pre-approved derivative works, any monetary gains made will be shared between the original author
 * and the other developer(s), proportional to the ratio of derived to original code.
 * 
 * Finally, any and all displays, duplicates or derivatives of this code must be prominently marked as such, and must
 * contain attribution to the original author, including a link to the original source. Any attempts to claim credit
 * for this code will be treated as intentional theft.
 * 
 * Due to the Mojang and Minecraft Mod Terms of Service and Licensing Restrictions, compiled versions of this code
 * must be provided for free. However, with the exception of pre-approved derivative works, only the original author
 * may distribute compiled binary versions of this code.
 * 
 * Failure to comply with these restrictions is a violation of copyright law and will be dealt with accordingly.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.ArrayList;
import java.util.List;

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
}

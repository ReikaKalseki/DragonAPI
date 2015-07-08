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

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashSet;

public class ReikaJVMParser {

	private static final HashSet<String> args = new HashSet();

	public static ArrayList<String> getAllArguments() {
		return new ArrayList(args);
	}

	public static boolean isArgumentPresent(String arg) {
		return args.contains(arg);
	}

	public static String getArgumentModifier(String pre) {
		for (String s : args) {
			if (s.startsWith(pre)) {
				return s.substring(pre.length());
			}
		}
		return null;
	}

	static {
		args.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries.java;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReikaJVMParser {

	private static final HashSet<String> args = new HashSet();

	private static final int[] version = getJavaVersion();

	public static Set<String> getAllArguments() {
		return Collections.unmodifiableSet(args);
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

	public static int getArgumentInteger(String pre) {
		for (String s : args) {
			if (s.startsWith(pre)) {
				int idx = s.indexOf('=');
				String ret = s.substring(idx);
				return ReikaJavaLibrary.safeIntParse(ret);
			}
		}
		return -1;
	}

	public static long getAllocatedHeapMemory() {
		for (String s : args) {
			if (s.startsWith("-Xmx")) {
				String ret = s.substring(4);
				char size = ret.charAt(ret.length()-1);
				if (Character.isLowerCase(size))
					size = Character.toUpperCase(size);
				ret = ret.substring(0, ret.length()-1);
				int base = Integer.parseInt(ret);
				if (size == 'K') {
					return base*1000L;
				}
				else if (size == 'M') {
					return base*1000000L;
				}
				else if (size == 'G') {
					return base*1000000000L;
				}
				else {
					throw new IllegalArgumentException("Invalid memory specification.");
				}
			}
		}
		return 1000000000; //1GB default on most PCs
	}

	static {
		args.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
		ReikaJavaLibrary.pConsole("Java Version: "+Arrays.toString(version));
		ReikaJavaLibrary.pConsole("Heap memory allocation: "+getAllocatedHeapMemory());
		ReikaJavaLibrary.pConsole(args.size()+" Java arguments present: "+args);
	}

	private static int[] getJavaVersion() {
		String v = System.getProperty("java.version");
		String[] parts = v.replaceAll("[^0-9\\._]", "").replaceAll("_", ".").split("\\.");
		int[] ret = new int[parts.length-1]; //ignore the "1."
		for (int i = 0; i < ret.length; i++) {
			ret[i] = Integer.parseInt(parts[i+1]);
		}
		return ret;
	}

	/** 0 for major (7, 8, etc), and 2 for release (eg 55 for 1.7_55) */
	public static int getJavaVersion(int subindex) {
		return version[subindex];
	}

}

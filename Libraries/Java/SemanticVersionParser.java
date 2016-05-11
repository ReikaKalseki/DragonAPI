/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import java.util.Arrays;

import Reika.DragonAPI.DragonAPICore;

public class SemanticVersionParser {

	public static int compareVersions(String v1, String v2) {
		return new SemanticVersion(v1).compareTo(new SemanticVersion(v2));
	}

	public static boolean isVersionAtLeast(String v1, String v2) {
		return compareVersions(v1, v2) >= 0;
	}

	public static boolean isVersionAtMost(String v1, String v2) {
		return compareVersions(v1, v2) <= 0;
	}

	public static SemanticVersion getVersion(String s) {
		return new SemanticVersion(s);
	}

	public static class SemanticVersion implements Comparable<SemanticVersion> {

		private final int[] versions;

		private SemanticVersion(String s) {
			String[] parts = s.split("\\.");
			versions = new int[parts.length];
			try {
				for (int i = 0; i < parts.length; i++) {
					versions[i] = Integer.parseInt(parts[i]);
				}
			}
			catch (NumberFormatException e) {
				String err = "'"+s+"' is not a valid semantic version! Must have '#.#.#...' formatting!";
				//throw new IllegalArgumentException(err);
				DragonAPICore.logError("Error parsing a semantic version! "+err);
			}
		}

		@Override
		/** Returns negative numbers for older versions, to keep with the "negative is before" rule */
		public int compareTo(SemanticVersion o) {
			for (int i = 0; i < versions.length; i++) {
				int us = versions[i];
				int them = o.versions.length > i ? o.versions[i] : 0;
				if (us != them) {
					return us-them;
				}
			}
			return 0;
		}

		public int[] getVersions() {
			return Arrays.copyOf(versions, versions.length);
		}

	}
}

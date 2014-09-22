/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ModVersion implements Comparable<ModVersion> {

	public static final ModVersion source = new ModVersion(0) {
		@Override
		public boolean equals(Object o) {return o == this;}
		@Override
		public String toString() {return "Source Code";}
		@Override
		public boolean isCompiled() {return false;}
	};

	private static final ModVersion error = new ModVersion(0) {
		@Override
		public boolean equals(Object o) {return o == this;}
		@Override
		public String toString() {return "[ERROR]";}
	};

	public final int majorVersion;
	public final String subVersion;

	private ModVersion(int major) {
		this(major, '\0');
	}

	private ModVersion(int major, char minor) {
		majorVersion = major;
		subVersion = minor == '\0' ? "" : Character.toString(minor).toLowerCase();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ModVersion) {
			ModVersion m = (ModVersion)o;
			return m.majorVersion == majorVersion && m.subVersion.equals(subVersion);
		}
		return false;
	}

	public boolean isCompiled() {
		return true;
	}

	@Override
	public String toString() {
		return "v"+majorVersion+subVersion;
	}

	public static ModVersion getFromString(String s) {
		if (s.startsWith("$"))
			return source;
		if (s.startsWith("v") || s.startsWith("V"))
			s = s.substring(1);
		char c = s.charAt(s.length()-1);
		if (Character.isDigit(c)) {
			return new ModVersion(Integer.parseInt(s));
		}
		else {
			String major = s.substring(0, s.length()-1);
			return new ModVersion(Integer.parseInt(major), c);
		}
	}

	private int getSubVersionIndex() {
		return subVersion == null || subVersion.isEmpty() ? 0 : subVersion.charAt(0)-'a';
	}

	@Override
	public int compareTo(ModVersion v) {
		return 32*(majorVersion-v.majorVersion)+(this.getSubVersionIndex()-v.getSubVersionIndex());
	}

	public boolean isNewerMinorVersion(ModVersion v) {
		return v.majorVersion == majorVersion && v.getSubVersionIndex() < this.getSubVersionIndex();
	}

	public static ModVersion readFromFile() {
		Properties p = new Properties();
		try {
			InputStream stream = ModVersion.class.getClassLoader().getResourceAsStream("version.properties");
			if (stream == null) {
				throw new FileNotFoundException();
			}
			p.load(stream);
			return getFromString(p.getProperty("Major")+p.getProperty("Minor"));
		}
		catch (IOException e) {
			e.printStackTrace();
			return error;
		}
	}
}

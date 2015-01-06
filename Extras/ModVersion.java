/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

public class ModVersion implements Comparable<ModVersion> {

	public static final ModVersion source = new ModVersion(0) {
		@Override public boolean equals(Object o) {return o == this;}
		@Override public String toString() {return "Source Code";}
		@Override public boolean isCompiled() {return false;}
		@Override public boolean verify() {return false;}
	};

	private static final ModVersion error = new ModVersion(0) {
		@Override public boolean equals(Object o) {return o == this;}
		@Override public String toString() {return "[NO FILE]";}
		@Override public boolean verify() {return false;}
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

	public boolean verify() {
		return true;
	}

	@Override
	public String toString() {
		return "v"+majorVersion+subVersion;
	}

	public static ModVersion getFromString(String s) {
		if (s.startsWith("$") || s.startsWith("@"))
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

	public static ModVersion readFromFile(DragonAPIMod mod) {
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment())
			return source;
		Properties p = new Properties();
		String path = ReikaStringParser.stripSpaces("version_"+ReikaStringParser.stripSpaces(mod.getTechnicalName().toLowerCase())+".properties");
		try {
			InputStream stream = ModVersion.class.getClassLoader().getResourceAsStream(path);
			if (stream == null) {
				throw new FileNotFoundException("Version file for "+mod.getDisplayName()+" is missing!");
			}
			p.load(stream);
			String mj = p.getProperty("Major");
			String mn = p.getProperty("Minor");
			if (mj == null || mn == null || mj.equals("null") || mn.equals("null") || mj.isEmpty() || mn.isEmpty())
				throw new InstallationException(mod, "The version file was either damaged, overwritten, or is missing!");
			return getFromString(mj+mn);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new InstallationException(mod, "The version file was either damaged, overwritten, or is missing!");
		}
	}
}

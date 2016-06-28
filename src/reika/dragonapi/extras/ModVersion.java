/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.extras;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import reika.dragonapi.DragonAPICore;
import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.exception.InstallationException;
import reika.dragonapi.libraries.java.ReikaObfuscationHelper;
import reika.dragonapi.libraries.java.ReikaStringParser;
import reika.dragonapi.libraries.java.SemanticVersionParser;
import reika.dragonapi.libraries.java.SemanticVersionParser.SemanticVersion;

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

	public static final ModVersion timeout = new ModVersion(0) {
		@Override public boolean equals(Object o) {return o == this;}
		@Override public String toString() {return "[URL TIMEOUT]";}
		@Override public boolean verify() {return false;}
	};

	public final int majorVersion;
	public final String subVersion;

	private ModVersion(int major) {
		this(major, '\0');
	}

	private ModVersion(int major, char minor) {
		majorVersion = major;
		subVersion = minor == '\0' ? "" : Character.toString(minor).toLowerCase(Locale.ENGLISH);
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
		String path = ReikaStringParser.stripSpaces("version_"+ReikaStringParser.stripSpaces(mod.getTechnicalName().toLowerCase(Locale.ENGLISH))+".properties");
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

	public String toSemanticVersion() {
		return String.format("%d.%d", majorVersion, 1+subVersion.charAt(0)-'a');
	}

	public static ModVersion fromSemanticVersion(String s) {
		SemanticVersion sm = SemanticVersionParser.getVersion(s);
		int[] ver = sm.getVersions();
		int major = ver.length > 0 ? ver[0] : 1;
		int minor = ver.length > 1 ? ver[1] : 1;
		return new ModVersion(major, Character.toChars('a'-1+minor)[0]);
	}
}

package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

public class ObfuscationMapping {

	private final String deobfName;
	private final String obfName;

	public ObfuscationMapping(String deobf, String obf) {
		deobfName = deobf;
		obfName = obf;
	}

	public String getEffectiveName() {
		return ReikaObfuscationHelper.isDeObfEnvironment() ? deobfName : obfName;
	}

}

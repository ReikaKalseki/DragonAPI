/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable;

import reika.dragonapi.libraries.java.ReikaObfuscationHelper;

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

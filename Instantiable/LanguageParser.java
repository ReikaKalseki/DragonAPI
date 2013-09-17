/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class LanguageParser {

	public final String language;

	public LanguageParser(String lang) {
		language = lang;
	}

	public void addMapping(String key, String value) {
		LanguageRegistry.instance().addStringLocalization(key, language, value);
	}

}

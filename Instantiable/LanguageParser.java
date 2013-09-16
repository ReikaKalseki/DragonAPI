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

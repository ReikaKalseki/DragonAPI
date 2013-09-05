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

import java.util.ArrayList;

import Reika.DragonAPI.Libraries.IO.ReikaLanguageReader;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class LanguageArray {

	public static final String FILE_EXT = ".xml";

	public LanguageArray(String path, String[] valid) {

		ArrayList<String> langs = new ArrayList<String>();
		for (int i = 0; i < valid.length; i++) {
			langs.add(path+valid[i]+FILE_EXT);
		}
		this.register(langs);
	}

	private void register(ArrayList<String> langs) {
		for (int i = 0; i < langs.size(); i++) {
			LanguageRegistry.instance().loadLocalization(langs.get(i), ReikaLanguageReader.getLanguageName(langs.get(i)), ReikaLanguageReader.isValidLanguageFile(langs.get(i)));
		}
	}

}

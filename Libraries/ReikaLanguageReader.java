/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * 
 * Distribution of the software in any form is only allowed
 * with explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.LanguageArray;
import cpw.mods.fml.common.registry.LanguageRegistry;

public final class ReikaLanguageReader extends DragonAPICore {

	public static boolean isValidLanguageFile(String name) {
		return name.endsWith(LanguageArray.FILE_EXT);
	}

	public static String getLanguageName(String file) {
		return file.substring(file.lastIndexOf('/')+1, file.lastIndexOf('.'));
	}

	public static String getLangString(String key) {
		return LanguageRegistry.instance().getStringLocalization(key);
	}

}

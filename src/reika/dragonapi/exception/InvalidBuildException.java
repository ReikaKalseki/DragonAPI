/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.exception;

import java.io.File;

import reika.dragonapi.base.DragonAPIMod;

public class InvalidBuildException extends DragonAPIException {

	public InvalidBuildException(DragonAPIMod mod, File jar) {
		message.append(mod.getDisplayName()+" is an invalid JarFile:\n");
		message.append(jar.getPath()+" is not a valid compiled copy of the mod.\n");
		message.append("If you are attempting to make a custom build of the code, consult "+mod.getModAuthorName()+".\n");
		message.append("Note that not all mods permit this, or of distributed versions of custom code, for security reasons.\n");
		message.append("If you got this by editing the mod jar, you may have to redownload the mod. Consult the developer for further questions.");
		this.crash();
	}

}

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

import reika.dragonapi.base.DragonAPIMod;

public class JarZipException extends DragonAPIException {

	public JarZipException(DragonAPIMod mod) {
		super();
		message.append("Your mod file for "+mod.getDisplayName()+" is a .jar.zip file!\n");
		message.append("Java does not recognize .zip files as jars, and the manifest will fail to load, causing a crash.\n");
		message.append("Rename your file .jar (you may need to enable viewing of file extensions),\n");
		message.append("and disable any browser plugins that automatically rename jars as zips.");
		this.crash();
	}

}

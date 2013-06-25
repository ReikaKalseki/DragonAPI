/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import Reika.DragonAPI.Base.DragonAPIMod;

public class InstallationException extends DragonAPIException {

	public InstallationException(DragonAPIMod mod, String msg) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append(msg+"\n");
		message.append("Try consulting "+mod.getDocumentationSite().toString()+"for information.\n");
		message.append("This is not a RotaryCraft bug. Do not post it to "+mod.getDocumentationSite().toString()+" unless you are really stuck.");
		this.crash();
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;

public class InstallationException extends DragonAPIException {

	public InstallationException(DragonAPIMod mod, String msg) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append(msg+"\n");
		message.append("Try consulting "+mod.getDocumentationSite().toString()+"for information.\n");
		message.append("This is not a "+mod.getDisplayName()+" bug. Do not post it to "+mod.getDocumentationSite().toString()+" unless you are really stuck.");
		this.crash();
	}

	public InstallationException(ModList mod, String msg) {
		message.append(mod.name()+" was not installed correctly:\n");
		message.append(msg+"\n");
		message.append("This is not a mod bug. Do not post it to the forums/websites unless you are really stuck.");
		this.crash();
	}

}
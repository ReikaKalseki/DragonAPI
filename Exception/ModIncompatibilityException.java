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

public class ModIncompatibilityException extends DragonAPIException {

	public ModIncompatibilityException(DragonAPIMod mod, String otherModName, String msg, boolean fatal) {
		message.append(mod.getDisplayName()+" has compatibility issues with the following mod:\n");
		message.append(otherModName+"\n");
		message.append("Reason: "+msg+"\n");
		message.append("Consult "+mod.getDocumentationSite().toString()+"for details.\n");
		if (fatal) {
			message.append("This is a fatal incompatibility. Loading cannot continue.");
			this.crash();
		}
		else {
			this.printStackTrace();
		}
	}

}

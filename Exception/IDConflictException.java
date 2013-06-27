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

public class IDConflictException extends DragonAPIException {

	public IDConflictException(DragonAPIMod mod, String msg) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append("CONFLICT: "+msg+"\n");
		message.append("Check your IDs and change them if possible.\n");
		message.append("This is NOT a mod bug. Do not post it to "+mod.getDocumentationSite().toString());
		this.crash();
	}

}

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

public class MissingDependencyException extends DragonAPIException {

	public MissingDependencyException(DragonAPIMod mod, String mod2) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append(mod.getDisplayName()+" was installed without its dependency "+mod2+"!\n");
		message.append("This is not a "+mod.getDisplayName()+" bug. Do not post it to "+mod.getDocumentationSite().toString()+".");
		this.crash();
	}

}

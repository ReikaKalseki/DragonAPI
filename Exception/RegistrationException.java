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

public class RegistrationException extends DragonAPIException {

	public RegistrationException(DragonAPIMod mod, String msg) {
		message.append(mod.getDisplayName()+" has a registration error:\n");
		message.append(msg+"\n");
		message.append("Contact "+mod.getModAuthorName()+" immediately!\n");
		message.append("Include the following information:");
		this.crash();
	}

}

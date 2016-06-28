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

public class RegistrationException extends DragonAPIException {

	public RegistrationException(DragonAPIMod mod, String msg) {
		this(mod, msg, null);
	}

	public RegistrationException(DragonAPIMod mod, String msg, Exception e) {
		message.append(mod.getTechnicalName()+" has a registration error:\n");
		message.append(msg+"\n");
		message.append("Contact "+mod.getModAuthorName()+" immediately!\n");
		message.append("Include the following information:");
		if (e != null)
			this.initCause(e);
		this.crash();
	}

}

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

public class ModIntegrityException extends DragonAPIException {

	public ModIntegrityException(DragonAPIMod mod, String tampering) {
		message.append("Something has tampered with and broken "+mod.getDisplayName()+"!\n");
		message.append(tampering+" has occurred!\n");
		message.append("Contact "+mod.getModAuthorName()+" immediately, with your full mod list!\n");
		this.crash();
	}

}

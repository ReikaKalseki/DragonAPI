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

public class ModIntegrityException extends DragonAPIException {

	public ModIntegrityException(DragonAPIMod mod) {
		message.append("Something has tampered with and broken "+mod.getDisplayName()+"!\n");
		message.append("Contact "+mod.getModAuthorName()+" immediately, with your full mod list!\n");
		this.crash();
	}

}

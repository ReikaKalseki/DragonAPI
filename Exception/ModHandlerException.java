/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import Reika.DragonAPI.ModList;

public class ModHandlerException extends DragonAPIException {

	public ModHandlerException(ModList mod) {
		message.append("You cannot call a mod handler before its parent mod initializes!\n");
		message.append("Target mod: "+mod);
		this.crash();
	}

}

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

import Reika.DragonAPI.Auxiliary.APIRegistry;

public class OreHandlerException extends DragonAPIException {

	public OreHandlerException(APIRegistry mod) {
		message.append("You cannot call an ore handler before its parent mod initializes!\n");
		message.append("Target mod: "+mod);
		this.crash();
	}

}

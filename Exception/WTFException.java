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

import Reika.DragonAPI.Interfaces.DragonAPIMod;

public class WTFException extends DragonAPIException {

	public WTFException(DragonAPIMod mod, String msg, boolean fatal) {
		message.append("Either you or "+mod.getDisplayName()+" did something really stupid:\n");
		message.append(msg+"\n");
		if (fatal) {
			message.append("What you did was so bad that the game cannot continue.");
			this.crash();
		}
	}

}
/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import Reika.DragonAPI.Base.DragonAPIMod;

public class WTFException extends DragonAPIException {

	public WTFException(DragonAPIMod mod, String msg, boolean fatal) {
		message.append("Either you or "+mod.getDisplayName()+" did something really stupid:\n");
		message.append(msg+"\n");
		if (fatal) {
			message.append("This is such a bad thing to do that the mods cannot load.");
			this.crash();
		}
		else {
			this.printStackTrace();
		}
	}

	public WTFException(String msg, boolean fatal) {
		message.append("Someone did something really stupid:\n");
		message.append(msg+"\n");
		if (fatal) {
			message.append("This is such a bad thing to do that the mods cannot load.");
			this.crash();
		}
		else {
			this.printStackTrace();
		}
	}

}

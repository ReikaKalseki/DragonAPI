/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import Reika.DragonAPI.Base.DragonAPIMod;

public class StupidIDException extends DragonAPIException {

	public StupidIDException(DragonAPIMod mod, int ID, boolean block) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append("ID "+ID+" is completely invalid, as it is "+this.getError(ID, block)+".\n");
		message.append("Please learn how IDs work before attempting to modify configs.\n");
		if (ID > 100000)
			message.append("No sane ID would be this large.\n");
		message.append("This is NOT a mod bug. Do not post it to the mod website or you will look extremely foolish.");
		this.crash();
	}

	public StupidIDException(DragonAPIMod mod, int ID) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append("ID "+ID+" is completely invalid.\n");
		message.append("Please learn how IDs work before attempting to modify configs.\n");
		if (ID > 100000)
			message.append("No sane ID would be this large.\n");
		message.append("This is NOT a mod bug. Do not post it to the mod website or you will look extremely foolish.");
		this.crash();
	}

	private String getError(int id, boolean block) {
		int max = block ? 4095 : 31999;
		return id < 0 ? "negative" : id > max ? "too large" : "";
	}

}

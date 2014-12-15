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

import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Extras.IDType;

public class StupidIDException extends DragonAPIException {

	public StupidIDException(DragonAPIMod mod, int ID, IDType type) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append(type.getName()+" ID "+ID+" is completely invalid, as it is "+this.getError(ID, type)+".\n");
		message.append("Please learn how IDs work before attempting to modify configs.\n");
		if (ID > 100000)
			message.append("No sane ID would be this large.\n");
		message.append("This is NOT a mod bug. Do not post it to the mod website or you will look extremely foolish.");
		this.crash();
	}

	private String getError(int id, IDType type) {
		int max = this.getMaxAllowable(type);
		return id < 0 ? "negative" : id > max ? "too large" : "";
	}

	private int getMaxAllowable(IDType type) {
		switch(type) {
		case BIOME:
			return BiomeGenBase.biomeList.length-1;
		case BLOCK:
			return 4095;
		case ITEM:
			return 32767;
		case POTION:
			return Potion.potionTypes.length-1;
		default:
			return -1;
		}
	}

}

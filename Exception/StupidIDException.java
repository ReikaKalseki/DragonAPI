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
			message.append("No sane ID would be this large, and you would do well to realize this.\n");
		message.append("This is NOT a mod bug. Do not post it or ask for support or you will look extremely foolish.");
		if (ID <= 0) {
			message.append("\nIf you picked "+ID+" in an attempt to disable the feature, this is not how, and disabling may not be possible.");
		}
		this.crash();
	}

	private String getError(int id, IDType type) {
		int max = this.getMaxAllowable(type);
		return id < 0 ? "negative" : id > max ? "too large" : "wrong";
	}

	private int getMaxAllowable(IDType type) {
		switch(type) {
		case BIOME:
			return BiomeGenBase.biomeList.length-1-1; //255 is reserved
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

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

/** Put this on a Merchant recipe subclass in order to control whether a player can see (and thus use) this trade. */
public interface PlayerSpecificTrade {

	public boolean isValid(EntityPlayer ep);

	/** This is just used to actually call the above. You can call it on your own lists if you so desire. */
	public static class MerchantRecipeHooks {

		public static MerchantRecipeList filterRecipeList(MerchantRecipeList li, EntityPlayer ep) {
			MerchantRecipeList ret = new MerchantRecipeList();
			for (Object o : li) {
				MerchantRecipe mr = (MerchantRecipe)o;
				boolean flag = false;
				if (!(mr instanceof PlayerSpecificTrade)) {
					flag = true;
				}
				else {
					if (((PlayerSpecificTrade)mr).isValid(ep)) {
						flag = true;
					}
				}
				if (flag) {
					ret.add(mr);
				}
			}
			return ret;
		}
	}

}

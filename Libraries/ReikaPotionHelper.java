/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.Iterator;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import Reika.DragonAPI.DragonAPICore;

public final class ReikaPotionHelper extends DragonAPICore {

	public static final int SPLASH_BIT = 16384;
	public static final int EXTENDED_BIT = 64;
	public static final int BOOST_BIT = 32;
	public static final int WATER_META = 0;
	public static final int AWKWARD_META = 16;
	public static final int POTION_BIT = 8192;

	/** Returns a potion ID from the damage value. Returns -1 if invalid damage value. */
	public static int getPotionID(int dmg) {
		List effects = Item.potion.getEffects(dmg);
		if (effects != null && !effects.isEmpty()) {
			Iterator potioneffects = effects.iterator();
			while (potioneffects.hasNext()) {
				PotionEffect effect = (PotionEffect)potioneffects.next();
				return effect.getPotionID();
			}
		}
		return -1;
	}

	public static boolean isSplashPotion(int dmg) {
		return (dmg & SPLASH_BIT) != 0;
	}

	public static boolean isBoosted(int dmg) {
		return (dmg & BOOST_BIT) != 0;
	}

	public static boolean isExtended(int dmg) {
		return (dmg & EXTENDED_BIT) != 0;
	}

	public static boolean isActualPotion(int dmg) {
		return (dmg & POTION_BIT) != 0;
	}

}

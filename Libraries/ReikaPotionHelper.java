/*******************************************************************************
 * @author Reika
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

}

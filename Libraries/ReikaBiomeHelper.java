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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;
import Reika.DragonAPI.DragonAPICore;

public class ReikaBiomeHelper extends DragonAPICore {

	public static int getFirstEmptyBiomeIndex() {
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			if (BiomeGenBase.biomeList[i] == null)
				return i;
		}
		return -1;
	}

	/** Note that this is affected by other mods, so exclusive calls on this will end up including mod biomes */
	public static List<BiomeGenBase> getAllBiomes() {
		List<BiomeGenBase> li = new ArrayList<BiomeGenBase>();
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			li.add(BiomeGenBase.biomeList[i]);
		}
		return li;
	}
}

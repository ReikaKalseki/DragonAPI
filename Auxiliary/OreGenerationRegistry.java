/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModRegistry.ModOreList;

public class OreGenerationRegistry {

	public static final OreGenerationRegistry instance = new OreGenerationRegistry();

	private HashMap<ModOreList, HashMap<ItemStack, Block>> data = new HashMap();

	private OreGenerationRegistry() {

	}

}

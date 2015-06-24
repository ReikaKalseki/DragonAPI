/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;


public interface OreType extends RegistryType {

	public OreRarity getRarity();

	public String[] getOreDictNames();

	public Collection<ItemStack> getAllOreBlocks();
	public ItemStack getFirstOreBlock();

	public boolean isNether();
	public boolean isEnd();

	public boolean canGenerateIn(Block b);

	public static enum OreRarity {
		EVERYWHERE("Large and very common veins", "Copper and Fluorite"), //Copper, Fluorite
		COMMON("Larger sized and common veins", "Tin and Redstone"), //Tin, Redstone
		AVERAGE("Average sized veins of average rarity", "Iron"), //Iron
		SCATTERED("Average sized but rarer veins", "Gold and Calcite"), //Gold, Calcite
		SCARCE("Veins are smaller and often hard to find", "Lapis and Diamond"), //Lapis, Diamond
		RARE("Generally a single block or two per chunk", "Emerald and Platinum"); //Emerald, Platinum

		public final String desc;
		public final String examples;

		private OreRarity(String d, String e) {
			desc = d;
			examples = e;
		}
	}

	public int ordinal();

}

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


public interface OreType {

	public OreRarity getRarity();

	public Collection<ItemStack> getAllOreBlocks();
	public ItemStack getFirstOreBlock();

	public boolean isNether();
	public boolean isEnd();

	public boolean canGenerateIn(Block b);

	public boolean existsInGame();

	public static enum OreRarity {
		EVERYWHERE(), //Copper, Fluorite
		COMMON(), //Tin, Redstone
		AVERAGE(), //Iron
		SCATTERED(), //Gold, Calcite
		SCARCE(), //Lapis, Diamond
		RARE(); //Emerald, Platinum
	}

}

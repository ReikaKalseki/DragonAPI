/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Registry;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public interface TreeType {

	public ItemStack getItem();
	public Block getLogID();
	public Block getLeafID();
	public Block getSaplingID();
	public List<Integer> getLogMetadatas();
	public List<Integer> getLeafMetadatas();
	public boolean canBePlacedSideways();
	public boolean exists();
	public ItemStack getBasicLeaf();

}

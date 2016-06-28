/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces.registry;

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
	public int getSaplingMeta();
	public boolean canBePlacedSideways();
	public boolean exists();
	public ItemStack getBasicLeaf();

}

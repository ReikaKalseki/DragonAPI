/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface TreeType {

	public ItemStack getItem();
	public int getLogID();
	public int getLeafID();
	public int getSaplingID();
	public List<Integer> getLogMetadatas();
	public List<Integer> getLeafMetadatas();
	public boolean canBePlacedSideways();

}

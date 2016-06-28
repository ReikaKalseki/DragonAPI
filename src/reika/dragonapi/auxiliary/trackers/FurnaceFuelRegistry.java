/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary.trackers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import reika.dragonapi.instantiable.data.maps.ItemHashMap;
import cpw.mods.fml.common.IFuelHandler;

public final class FurnaceFuelRegistry implements IFuelHandler {

	public static final FurnaceFuelRegistry instance = new FurnaceFuelRegistry();

	private final ItemHashMap<Integer> data = new ItemHashMap().setOneWay();

	private FurnaceFuelRegistry() {

	}

	public void registerItemSimple(ItemStack is, float smelts) {
		this.registerItem(is, (int)(smelts*200));
	}

	public void registerItem(ItemStack is, int ticks) {
		data.put(is, ticks);
	}

	/** The amount a block of coal burns over the time an item of coal burns. 10x as of 1.7.10 */
	public int getBlockOverItemFactor() {
		return TileEntityFurnace.getItemBurnTime(new ItemStack(Blocks.coal_block))/TileEntityFurnace.getItemBurnTime(new ItemStack(Items.coal));
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		Integer get = data.get(fuel);
		return get != null ? get.intValue() : 0;
	}

}

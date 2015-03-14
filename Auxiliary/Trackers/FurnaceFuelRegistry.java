/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
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

	@Override
	public int getBurnTime(ItemStack fuel) {
		Integer get = data.get(fuel);
		return get != null ? get.intValue() : 0;
	}

}

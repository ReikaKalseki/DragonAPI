/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;


public class HarvestLevelEvent extends PlayerEvent {

	private final ItemStack item;
	public final int originalHarvestLevel;
	public int harvestLevel;
	public final String toolType;

	public HarvestLevelEvent(EntityPlayer ep, ItemStack is, String type, int lvl) {
		super(ep);
		item = is;
		toolType = type;
		originalHarvestLevel = harvestLevel = lvl;
	}

	public ItemStack getItem() {
		return item.copy();
	}

	public static int fire(int lvl, EntityPlayer ep, String type) {
		HarvestLevelEvent evt = new HarvestLevelEvent(ep, ep.getCurrentEquippedItem(), type, lvl);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.harvestLevel;
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;

@Deprecated
public class ItemTooltipEvent extends Event {

	private final List<String> entries;
	private final ItemStack item;
	public final EntityPlayer player;
	public final boolean verbose;

	public ItemTooltipEvent(ItemStack is, EntityPlayer ep, List<String> li, boolean vb) {
		item = is;
		entries = li;
		player = ep;
		verbose = vb;
	}

	public void addLine(String s) {
		entries.add(s);
	}

	public ItemStack getItem() {
		return item.copy();
	}

	public static void fire(ItemStack is, EntityPlayer ep, List<String> li, boolean vb) {
		MinecraftForge.EVENT_BUS.post(new ItemTooltipEvent(is, ep, li, vb));
	}

}

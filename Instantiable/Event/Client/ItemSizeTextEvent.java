/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;


public class ItemSizeTextEvent extends Event {

	private final ItemStack item;

	public final String originalString;
	public String newString;

	public ItemSizeTextEvent(ItemStack is, String s) {
		item = is;
		originalString = s;
		newString = originalString;
	}

	public ItemStack getItem() {
		return item.copy();
	}

	public static String fire(ItemStack is, String s) {
		ItemSizeTextEvent evt = new ItemSizeTextEvent(is, s);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.newString;
	}

}

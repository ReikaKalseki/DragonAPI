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

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;



public class VillagerTradeEvent extends PlayerEvent {

	public final IMerchant villager;
	public final MerchantRecipe trade;

	public VillagerTradeEvent(IMerchant ev, MerchantRecipe r, EntityPlayer ep) {
		super(ep);
		villager = ev;
		trade = r;
	}

	public static void fire(IMerchant im, EntityPlayer ep, MerchantRecipe r) {
		//ReikaJavaLibrary.pConsole("Firing villager trade event from V="+im+" / P="+ep+" / T="+r);
		//Thread.dumpStack();
		MinecraftForge.EVENT_BUS.post(new VillagerTradeEvent(im, r, ep));
	}

}

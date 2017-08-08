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

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.common.eventhandler.Event.HasResult;


@HasResult
/** Result is for the aggroing, so ALLOW forces aggro (overriding pumpkin), and DENY prevents it. */
public class EnderLookAggroEvent extends PlayerEvent {

	public final EntityEnderman mob;

	public EnderLookAggroEvent(EntityPlayer player, EntityEnderman e) {
		super(player);
		mob = e;
	}

	public static boolean fire(EntityPlayer player, EntityEnderman e) {
		EnderLookAggroEvent evt = new EnderLookAggroEvent(player, e);
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return false;
			case DENY:
				return true;
			default:
			case DEFAULT:
				ItemStack is = player.inventory.armorInventory[3];
				return is != null && ReikaItemHelper.matchStackWithBlock(is, Blocks.pumpkin);
		}
	}

}

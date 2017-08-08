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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;


public class PlayerSprintEvent extends PlayerEvent {

	public PlayerSprintEvent(EntityPlayer ep) {
		super(ep);
	}

	public static void fire(EntityPlayer ep) {
		MinecraftForge.EVENT_BUS.post(new PlayerSprintEvent(ep));
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent;
import Reika.DragonAPI.Interfaces.PlayerBreakHook;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DragonAPIEventWatcher {

	public static final DragonAPIEventWatcher instance = new DragonAPIEventWatcher();

	private DragonAPIEventWatcher() {

	}

	@SubscribeEvent
	public void trackBrokenBlocks(BlockEvent.BreakEvent evt) {
		TileEntity te = evt.world.getTileEntity(evt.x, evt.y, evt.z);
		if (te instanceof PlayerBreakHook) {
			if (!((PlayerBreakHook)te).breakByPlayer(evt.getPlayer())) {
				evt.setCanceled(true);
			}
		}
	}

}

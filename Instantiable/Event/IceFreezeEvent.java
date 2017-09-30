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

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class IceFreezeEvent extends PositionEvent {

	public final boolean needsEdge;

	public IceFreezeEvent(World world, int x, int y, int z, boolean edge) {
		super(world, x, y, z);
		needsEdge = edge;
	}

	public final boolean wouldFreezeNaturally() {
		return world.provider.canBlockFreeze(xCoord, yCoord, zCoord, needsEdge);
	}

	public static boolean fire(World world, int x, int y, int z, boolean edge) {
		IceFreezeEvent evt = new IceFreezeEvent(world, x, y, z, edge);
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return true;
			case DEFAULT:
			default:
				return evt.wouldFreezeNaturally();
			case DENY:
				return false;
		}
	}

}

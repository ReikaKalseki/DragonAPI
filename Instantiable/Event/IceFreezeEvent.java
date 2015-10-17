/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class IceFreezeEvent extends WorldEvent {

	public final int x;
	public final int y;
	public final int z;
	public final boolean needsEdge;

	public IceFreezeEvent(World world, int x, int y, int z, boolean edge) {
		super(world);
		this.x = x;
		this.y = y;
		this.z = z;
		needsEdge = edge;
	}

	public final boolean wouldFreezeNaturally() {
		return world.provider.canBlockFreeze(x, y, z, needsEdge);
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

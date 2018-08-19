/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class CanSeeSkyEvent extends PositionEvent {

	private final Chunk chunk;

	public CanSeeSkyEvent(Chunk c, int x, int y, int z) {
		super(c.worldObj, x, y, z);
		chunk = c;
	}

	public static boolean fire(Chunk c, int x, int y, int z) {
		CanSeeSkyEvent evt = new CanSeeSkyEvent(c, x+(c.xPosition << 4), y, z+(c.zPosition << 4));
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return true;
			case DENY:
				return false;
			case DEFAULT:
			default:
				return defaultResult(c, x, y, z);
		}
	}

	private static boolean defaultResult(Chunk c, int x, int y, int z) {
		return y >= c.heightMap[z << 4 | x];
	}

}

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
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Event;


public class DimensionSeedEvent extends Event {

	public final World world;
	public final long originalSeed;

	public long seed;

	public DimensionSeedEvent(World world) {
		this.world = world;
		originalSeed = world.getWorldInfo().getSeed();

		seed = originalSeed;
	}

	public static long fire(WorldProvider p) {
		DimensionSeedEvent evt = new DimensionSeedEvent(p.worldObj);
		if (!p.worldObj.isRemote) {
			MinecraftForge.EVENT_BUS.post(evt);
		}
		return evt.seed;
	}
}

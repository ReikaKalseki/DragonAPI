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

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class PlaceBucketEvent extends WorldPositionEvent {

	public final Block original;

	/** Does not necessarily have to be a fluid. */
	public Block fluid;
	/** Usually zero, for source blocks */
	public int fluidMetadata;

	public PlaceBucketEvent(World world, int x, int y, int z, Block b) {
		super(world, x, y, z);
		original = b;
		fluid = original;
		fluidMetadata = 0;
	}

	public static boolean fire(World world, int x, int y, int z, Block orig, int meta, int flags) {
		PlaceBucketEvent evt = new PlaceBucketEvent(world, x, y, z, orig);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.isCanceled() ? false : world.setBlock(x, y, z, evt.fluid, evt.fluidMetadata, flags);
	}

}

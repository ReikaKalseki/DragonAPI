/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.event;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;


public class BlockTickEvent extends Event {

	public final World world;
	public final Block block;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	private final int flags;

	public BlockTickEvent(World world, int x, int y, int z, Block b, int flags) {
		this.world = world;
		block = b;
		xCoord = x;
		yCoord = y;
		zCoord = z;
		this.flags = flags;
	}

	public static void fire(World world, int x, int y, int z, Block b, int flags) {
		MinecraftForge.EVENT_BUS.post(new BlockTickEvent(world, x, y, z, b, flags));
	}

	public final boolean isFlagPresent(UpdateFlags f) {
		return (flags & f.flag) != 0;
	}

	public static enum UpdateFlags {
		NATURAL(),
		SCHEDULED(),
		FORCED();

		public final int flag;

		private UpdateFlags() {
			flag = 1 << this.ordinal();
		}
	}

}

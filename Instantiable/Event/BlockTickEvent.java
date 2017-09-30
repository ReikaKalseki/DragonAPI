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


public class BlockTickEvent extends PositionEvent {

	public final Block block;

	private final int flags;

	public BlockTickEvent(World world, int x, int y, int z, Block b, int flags) {
		super(world, x, y, z);
		block = b;
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

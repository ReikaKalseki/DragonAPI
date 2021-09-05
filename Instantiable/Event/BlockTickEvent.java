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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockGrass;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;

//@Cancelable do not add annotation, since that ends up triggering forge ASM
/** This method is fired whenever blocks are ticked via updateTick() via ambient world block ticks and any of my mods' code running forced ticks.
 * The event may or may not be cancelable (check isCancelable() first), in which case the tick will not occur. */
public class BlockTickEvent extends WorldPositionEvent {

	public static boolean disallowAllUpdates = false;

	public final Block block;

	private final int flags;

	public BlockTickEvent(World world, int x, int y, int z, Block b, int flags) {
		super(world, x, y, z);
		block = b;
		this.flags = flags;
	}

	@Override
	public boolean isCancelable() {
		return !UpdateFlags.REQUIRE.isFlagPresent(flags);
	}

	public boolean isFlagPresent(UpdateFlags flag) {
		return flag.isFlagPresent(flags);
	}

	public static void fire(World world, int x, int y, int z, int flags) {
		fire(world.getBlock(x, y, z), world, x, y, z, world.rand, flags);
	}

	public static void fire(World world, int x, int y, int z, UpdateFlags flag) {
		fire(world.getBlock(x, y, z), world, x, y, z, flag);
	}

	public static void fire(Block b, World world, int x, int y, int z, UpdateFlags flag) {
		fire(b, world, x, y, z, world.rand, flag);
	}

	public static void fire(World world, int x, int y, int z, Random rand, int flags) {
		fire(world.getBlock(x, y, z), world, x, y, z, rand, flags);
	}

	public static void fire(World world, int x, int y, int z, Random rand, UpdateFlags flag) {
		fire(world.getBlock(x, y, z), world, x, y, z, rand, flag);
	}

	public static void fire(Block b, World world, int x, int y, int z, Random rand, UpdateFlags flag) {
		fire(b, world, x, y, z, rand, flag.flag);
	}

	public static void fire(Block b, World world, int x, int y, int z, Random rand, int flags) {
		if (!disallowAllUpdates && canTickAt(b, world, x, y, z, flags) && !MinecraftForge.EVENT_BUS.post(new BlockTickEvent(world, x, y, z, b, flags))) {
			b.updateTick(world, x, y, z, rand);
		}
	}

	private static boolean canTickAt(Block b, World world, int x, int y, int z, int flags) {
		if (UpdateFlags.FORCED.isFlagPresent(flags) || UpdateFlags.REQUIRE.isFlagPresent(flags))
			return true;
		return DragonOptions.STOPUNLOADSPREAD.getState() && requireLoadedArea(b) ? hasLoadedRadius(world, x, y, z) : true;
	}

	private static boolean requireLoadedArea(Block b) {
		return ReikaBlockHelper.isLiquid(b) || b instanceof BlockGrass || b instanceof BlockFire;
	}

	private static boolean hasLoadedRadius(World world, int x, int y, int z) {
		int cx = (x >> 4) << 4;
		int cz = (z >> 4) << 4;
		return world.checkChunksExist(cx-16, y, cz-16, cx+16, y, cz+16);
	}

	public static enum UpdateFlags {
		NATURAL(),
		SCHEDULED(),
		FORCED(),
		REQUIRE();

		public final int flag;

		private UpdateFlags() {
			flag = 1 << this.ordinal();
		}

		public boolean isFlagPresent(int flags) {
			return (flags & flag) != 0;
		}

		public static int getForcedUnstoppableTick() {
			return FORCED.flag+REQUIRE.flag;
		}
	}

}

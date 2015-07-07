/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.util.ArrayList;

import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.BlockEnum;

public class LegacyWailaHelper {

	private static final ArrayList<DataHandler> wailaData = new ArrayList();

	private static long worldTime;
	private static WorldLocation lastLocation;

	@ModDependent(ModList.WAILA)
	public static void registerObjects(IWailaRegistrar reg) {
		for (DataHandler h : wailaData) {
			IWailaDataProvider b = (IWailaDataProvider)h.getBlock();
			Class<?extends Block> c = h.getObjectClass();
			//reg.registerHeadProvider(b, c);
			reg.registerBodyProvider(b, c);
			//reg.registerTailProvider(b, c);
			//reg.registerStackProvider(b, c);
		}
	}

	public static void registerLegacyWAILACompat(BlockEnum r) {
		wailaData.add(new BlockEnumHandler(r));
	}

	public static void registerLegacyWAILACompat(Block b) {
		wailaData.add(new BlockHandler(b));
	}

	@ModDependent(ModList.WAILA)
	public static boolean cacheAndReturn(IWailaDataAccessor acc) {
		World world = acc.getWorld();
		long time = world.getTotalWorldTime();
		if (time != worldTime)
			lastLocation = null;
		worldTime = time;
		WorldLocation loc = new WorldLocation(world, acc.getPosition().blockX, acc.getPosition().blockY, acc.getPosition().blockZ);
		if (loc.equals(lastLocation)) {
			return true;
		}
		lastLocation = loc;
		return false;
	}

	private static interface DataHandler {

		public Block getBlock();
		public Class<? extends Block> getObjectClass();

	}

	private static class BlockHandler implements DataHandler {

		private final Block block;

		private BlockHandler(Block b) {
			block = b;
		}

		@Override
		public Block getBlock() {
			return block;
		}

		@Override
		public Class<? extends Block> getObjectClass() {
			return block.getClass();
		}

	}

	private static class BlockEnumHandler implements DataHandler {

		private final BlockEnum entry;

		private BlockEnumHandler(BlockEnum b) {
			entry = b;
		}

		@Override
		public Block getBlock() {
			return entry.getBlockInstance();
		}

		@Override
		public Class<? extends Block> getObjectClass() {
			return entry.getObjectClass();
		}

	}

}

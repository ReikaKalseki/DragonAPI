/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;

import Reika.DragonAPI.Interfaces.Callbacks.EventWatchers;
import Reika.DragonAPI.Interfaces.Callbacks.EventWatchers.EventWatcher;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RenderBlockAtPosEvent {

	private static BlockRenderWatcher[] listeners = null;

	public static boolean continueRendering = false;

	public static void addListener(BlockRenderWatcher l) {
		listeners = ReikaArrayHelper.addToFastArray(listeners, l, BlockRenderWatcher.class);
		Arrays.sort(listeners, EventWatchers.comparator);
	}

	@SideOnly(Side.CLIENT)
	public static boolean fire(RenderBlocks rb, Block b, int x, int y, int z, WorldRenderer wr, int pass) {
		if (listeners != null) {
			for (BlockRenderWatcher l : listeners) {
				if (l.onBlockTriedRender(b, x, y, z, wr, rb, pass)) {
					continueRendering = false;
					return false;
				}
			}
		}
		return rb.renderBlockByRenderType(b, x, y, z) || continueRendering;
	}
	/*
	@SideOnly(Side.CLIENT)
	public static int getMixedBrightnessForBlock(Block b, IBlockAccess iba, int x, int y, int z) {
		if (listeners != null) {
			for (BlockRenderWatcher l : listeners) {
				if (l.onBlockTriedRender(b, x, y, z, wr, rb, pass)) {
					continueRendering = false;
					return false;
				}
			}
		}
		return rb.renderBlockByRenderType(b, x, y, z) || continueRendering;
	}
	 */
	public static interface BlockRenderWatcher extends EventWatcher {

		/** Return true to act like an event cancel */
		@SideOnly(Side.CLIENT)
		boolean onBlockTriedRender(Block b, int x, int y, int z, WorldRenderer wr, RenderBlocks rb, int pass);

		//boolean getMixedBrightnessForBlock(Block b, IBlockAccess iba, int x, int y, int z);

	}

}

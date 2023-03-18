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

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.Interfaces.Callbacks.EventWatchers;
import Reika.DragonAPI.Interfaces.Callbacks.EventWatchers.EventWatcher;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityRenderEvent {

	private static TileRenderWatcher[] listeners = null;

	public static void addListener(TileRenderWatcher l) {
		listeners = ReikaArrayHelper.addToFastArray(listeners, l, TileRenderWatcher.class);
		Arrays.sort(listeners, EventWatchers.comparator);
	}

	@SideOnly(Side.CLIENT)
	public static void fire(TileEntitySpecialRenderer tesr, TileEntity te, double par2, double par4, double par6, float par8) {
		if (listeners != null) {
			for (TileRenderWatcher l : listeners) {
				if (l.preTileRender(tesr, te, par2, par4, par6, par8)) {
					return;
				}
			}
		}
		tesr.renderTileEntityAt(te, par2, par4, par6, par8);
		if (listeners != null) {
			for (TileRenderWatcher l : listeners) {
				l.postTileRender(tesr, te, par2, par4, par6, par8);
			}
		}
	}

	public static interface TileRenderWatcher extends EventWatcher {

		/** Return true to act like an event cancel */
		@SideOnly(Side.CLIENT)
		boolean preTileRender(TileEntitySpecialRenderer tesr, TileEntity te, double par2, double par4, double par6, float par8);
		@SideOnly(Side.CLIENT)
		void postTileRender(TileEntitySpecialRenderer tesr, TileEntity te, double par2, double par4, double par6, float par8);

	}

}

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

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.Interfaces.Callbacks.EventWatchers;
import Reika.DragonAPI.Interfaces.Callbacks.EventWatchers.EventWatcher;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityRenderEvent {

	private static final ArrayList<TileRenderWatcher> listeners = new ArrayList();

	public static void addListener(TileRenderWatcher l) {
		listeners.add(l);
		Collections.sort(listeners, EventWatchers.comparator);
	}

	@SideOnly(Side.CLIENT)
	public static void fire(TileEntitySpecialRenderer tesr, TileEntity te, double par2, double par4, double par6, float par8) {
		for (TileRenderWatcher l : listeners) {
			if (l.preTileRender(tesr, te, par2, par4, par6, par8)) {
				return;
			}
		}
		tesr.renderTileEntityAt(te, par2, par4, par6, par8);
		for (TileRenderWatcher l : listeners) {
			l.postTileRender(tesr, te, par2, par4, par6, par8);
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

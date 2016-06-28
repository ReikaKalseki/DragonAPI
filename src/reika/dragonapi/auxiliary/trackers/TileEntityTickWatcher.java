/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary.trackers;

import java.util.Collection;

import net.minecraft.tileentity.TileEntity;
import reika.dragonapi.instantiable.data.maps.MultiMap;

public class TileEntityTickWatcher {

	public static final TileEntityTickWatcher instance = new TileEntityTickWatcher();

	private final MultiMap<Class, TileWatcher> data = new MultiMap();

	private TileEntityTickWatcher() {

	}

	public void watchTiles(Class type, TileWatcher t) {
		data.addValue(type, t);
	}

	public static void tick(TileEntity te) {
		instance.tickTile(te);
	}

	private void tickTile(TileEntity te) {
		Collection<TileWatcher> c = data.get(te.getClass());
		if (c != null) {
			for (TileWatcher tile : c) {
				tile.onTick(te);
			}
		}
	}

	public static interface TileWatcher {

		public void onTick(TileEntity te);
		public boolean tickOnce();

	}

}

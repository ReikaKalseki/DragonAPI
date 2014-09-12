/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;

public final class TileEntityCache<V> {

	private final HashMap<Coordinate, V> data = new HashMap();

	public TileEntityCache() {

	}

	public V put(int x, int y, int z, V tile) {
		return this.put(x, y, z, tile);
	}

	public V put(Coordinate c, V tile) {
		return data.put(c, tile);
	}

	public V put(WorldLocation loc, V tile) {
		return data.put(new Coordinate(loc), tile);
	}

	public V put(V tile) {
		TileEntity te = (TileEntity)tile;
		return this.put(te.xCoord, te.yCoord, te.zCoord, tile);
	}

	public V get(int x, int y, int z) {
		return this.get(new Coordinate(x, y, z));
	}

	public V get(Coordinate c) {
		return data.get(c);
	}

	public boolean containsKey(int x, int y, int z) {
		return this.containsKey(new Coordinate(x, y, z));
	}

	public boolean containsKey(Coordinate c) {
		return data.containsKey(c);
	}

	public V remove(int x, int y, int z) {
		return this.remove(new Coordinate(x, y, z));
	}

	public V remove(Coordinate c) {
		return data.remove(c);
	}

	public Collection<Coordinate> keySet() {
		ArrayList set = new ArrayList();
		set.addAll(data.keySet());
		return set;
	}

	public void clear() {
		data.clear();
	}

}

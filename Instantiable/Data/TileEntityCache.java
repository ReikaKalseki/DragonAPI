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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class TileEntityCache<V> {

	private final HashMap<WorldLocation, V> data = new HashMap();

	public TileEntityCache() {

	}

	public V put(World world, int x, int y, int z, V tile) {
		return this.put(new WorldLocation(world, x, y, z), tile);
	}

	public V put(WorldLocation loc, V tile) {
		return data.put(loc, tile);
	}

	public V put(V tile) {
		TileEntity te = (TileEntity)tile;
		return this.put(te.worldObj, te.xCoord, te.yCoord, te.zCoord, tile);
	}

	public V get(World world, int x, int y, int z) {
		return this.get(new WorldLocation(world, x, y, z));
	}

	public V get(WorldLocation c) {
		return data.get(c);
	}

	public boolean containsKey(World world, int x, int y, int z) {
		return this.containsKey(new WorldLocation(world, x, y, z));
	}

	public boolean containsKey(WorldLocation c) {
		return data.containsKey(c);
	}

	public V remove(World world, int x, int y, int z) {
		return this.remove(new WorldLocation(world, x, y, z));
	}

	public V remove(WorldLocation c) {
		return data.remove(c);
	}

	public Set<WorldLocation> keySet() {
		return Collections.unmodifiableSet(data.keySet());
	}

	public void clear() {
		data.clear();
	}

	public void removeWorld(World world) {
		Iterator<WorldLocation> it = data.keySet().iterator();
		while (it.hasNext()) {
			WorldLocation loc = it.next();
			if (loc.dimensionID == world.provider.dimensionId)
				it.remove();
		}
	}

	public int size() {
		return data.size();
	}

	public Collection<V> values() {
		return Collections.unmodifiableCollection(data.values());
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public String toString() {
		return data.toString();
	}

}

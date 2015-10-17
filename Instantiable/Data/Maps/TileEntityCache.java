/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionFactory;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

public final class TileEntityCache<V> {

	private final HashMap<WorldLocation, V> data = new HashMap();

	public TileEntityCache() {

	}

	public V put(World world, int x, int y, int z, V value) {
		return this.put(new WorldLocation(world, x, y, z), value);
	}

	public V put(TileEntity te, V value) {
		return this.put(new WorldLocation(te), value);
	}

	public V put(WorldLocation loc, V value) {
		return data.put(loc, value);
	}

	public V put(V tile) {
		if (!(tile instanceof TileEntity))
			throw new MisuseException("You cannot self-put an entry if it is not a TileEntity!");
		TileEntity te = (TileEntity)tile;
		return this.put(te.worldObj, te.xCoord, te.yCoord, te.zCoord, tile);
	}

	public void putAll(TileEntityCache<V> tc) {
		data.putAll(tc.data);
	}

	public V get(World world, int x, int y, int z) {
		return this.get(new WorldLocation(world, x, y, z));
	}

	public V get(TileEntity te) {
		return te != null ? this.get(new WorldLocation(te)) : null;
	}

	public V get(WorldLocation c) {
		return data.get(c);
	}

	public boolean containsKey(World world, int x, int y, int z) {
		return this.containsKey(new WorldLocation(world, x, y, z));
	}

	public boolean containsKey(TileEntity te) {
		return te != null && this.containsKey(new WorldLocation(te));
	}

	public boolean containsKey(WorldLocation c) {
		return data.containsKey(c);
	}

	public V remove(World world, int x, int y, int z) {
		return this.remove(new WorldLocation(world, x, y, z));
	}

	public V remove(TileEntity te) {
		return this.remove(new WorldLocation(te));
	}

	public V remove(WorldLocation c) {
		return data.remove(c);
	}

	public V remove(V tile) {
		if (!(tile instanceof TileEntity))
			throw new MisuseException("You cannot self-remove an entry if it is not a TileEntity!");
		TileEntity te = (TileEntity)tile;
		return this.remove(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
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

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList li = new NBTTagList();
		for (WorldLocation loc : data.keySet()) {
			NBTTagCompound data = new NBTTagCompound();
			loc.writeToNBT(data);
			li.appendTag(data);
		}
		tag.setTag("locs", li);
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList li = tag.getTagList("locs", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound data = (NBTTagCompound)o;
			WorldLocation loc = WorldLocation.readFromNBT(data);
			TileEntity te = loc.getTileEntity();
			try {
				V v = (V)te;
				this.data.put(loc, v);
			}
			catch (ClassCastException e) { //ugly, but no other way to test if te instanceof V
				DragonAPICore.logError("Tried to load a TileEntityCache from invalid NBT!");
			}
		}
	}

	public boolean containsValue(V value) {
		return data.containsValue(value);
	}

	public MultiMap<V, WorldLocation> invert(CollectionFactory cf) {
		MultiMap map = new MultiMap(cf);
		for (WorldLocation loc : this.data.keySet()) {
			map.addValue(data.get(loc), loc);
		}
		return map;
	}

	public MultiMap<V, WorldLocation> invert() {
		return this.invert(null);
	}

}

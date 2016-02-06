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
import java.util.HashSet;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionFactory;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public final class TileEntityCache<V> {

	private static final String LOGGER_ID = "TileCache";

	private final NestedMap<WorldChunk, WorldLocation, V> data = new NestedMap();

	public TileEntityCache() {

	}

	public V put(World world, int x, int y, int z, V value) {
		return this.put(new WorldLocation(world, x, y, z), value);
	}

	public V put(TileEntity te, V value) {
		return this.put(new WorldLocation(te), value);
	}

	public V put(WorldLocation loc, V value) {
		return data.put(this.getChunk(loc), loc, value);
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
		return data.get(this.getChunk(c), c);
	}

	public boolean containsKey(World world, int x, int y, int z) {
		return this.containsKey(new WorldLocation(world, x, y, z));
	}

	public boolean containsKey(TileEntity te) {
		return te != null && this.containsKey(new WorldLocation(te));
	}

	public boolean containsKey(WorldLocation c) {
		return data.containsInnerKey(c);
	}

	public V remove(World world, int x, int y, int z) {
		return this.remove(new WorldLocation(world, x, y, z));
	}

	public V remove(TileEntity te) {
		return this.remove(new WorldLocation(te));
	}

	public V remove(WorldLocation c) {
		return data.remove(this.getChunk(c), c);
	}

	public V remove(V tile) {
		if (!(tile instanceof TileEntity))
			throw new MisuseException("You cannot self-remove an entry if it is not a TileEntity!");
		TileEntity te = (TileEntity)tile;
		return this.remove(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
	}

	public Collection<WorldLocation> keySet() {
		return Collections.unmodifiableCollection(data.innerSet());
	}

	public void clear() {
		data.clear();
	}

	public void removeWorld(World world) {
		HashSet<WorldLocation> set = new HashSet(data.innerSet());
		for (WorldLocation loc : set) {
			data.removeAll(loc);
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
		for (WorldLocation loc : data.innerSet()) {
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
				this.data.put(this.getChunk(loc), loc, v);
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
		for (WorldLocation loc : this.data.innerSet()) {
			map.addValue(data.get(this.getChunk(loc), loc), loc);
		}
		return map;
	}

	public MultiMap<V, WorldLocation> invert() {
		return this.invert(null);
	}

	/** Note that this returns everything in all chunks intersected by the radius. Distances to the actual WorldLocs may be somewhat larger than
	 * the radius, and as such a normal pythagorean distance check is still required. */
	public Collection<WorldLocation> getAllLocationsNear(WorldLocation loc, double radius) {
		Collection<WorldLocation> ret = new HashSet();
		int cx = ReikaMathLibrary.bitRound(loc.xCoord, 4);
		int cz = ReikaMathLibrary.bitRound(loc.zCoord, 4);
		int dx = loc.xCoord-cx;
		int dz = loc.zCoord-cz;
		double xmax = dx+radius;
		double xmin = Math.abs(dx-radius);
		double zmax = dz+radius;
		double zmin = Math.abs(dz-radius);
		int nxp = (int)Math.floor(xmax/16D);
		int nxn = (int)Math.ceil(xmin/16D);
		int nzp = (int)Math.floor(zmax/16D);
		int nzn = (int)Math.ceil(zmin/16D);
		//ReikaJavaLibrary.pConsole(data);
		for (int i = -nxn; i <= nxp; i++) {
			for (int k = -nzn; k <= nzp; k++) {
				WorldChunk pos = new WorldChunk(loc.dimensionID, (cx >> 4)+i, (cz >> 4)+k);
				Collection<WorldLocation> locs = data.getAllKeysIn(pos);
				if (ModularLogger.instance.isEnabled(LOGGER_ID) && radius > 16)
					ReikaJavaLibrary.pConsole(loc+" ->@ "+dx+","+dz+" in "+cx+","+cz+"; "+nxn+" > "+nxp+", "+nzn+" > "+nzp+" pos["+pos+"]= "+locs+" of "+data);
				if (locs != null) {
					ret.addAll(locs);
				}
			}
		}
		return ret;
	}

	public Map<WorldLocation, V> getChunkData(WorldChunk c) {
		return data.getMap(c);
	}

	public Map<WorldLocation, V> getChunkData(WorldLocation c) {
		return this.getChunkData(this.getChunk(c));
	}

	private static WorldChunk getChunk(WorldLocation loc) {
		return new WorldChunk(loc.dimensionID, loc.getChunk());
	}

}

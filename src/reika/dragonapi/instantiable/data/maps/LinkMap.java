/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import reika.dragonapi.instantiable.data.immutable.WorldLocation;
import reika.dragonapi.libraries.ReikaNBTHelper.NBTTypes;

public class LinkMap {

	private final HashMap<WorldLocation, HashMap<WorldLocation, Double>> data = new HashMap();

	public void addLink(WorldLocation src, WorldLocation tg) {
		this.connect(src, tg);
	}

	public void addBiLink(WorldLocation src, WorldLocation tg) {
		this.connect(src, tg);
		this.connect(tg, src);
	}

	private void connect(WorldLocation src, WorldLocation tg) {
		HashMap<WorldLocation, Double> c = data.get(src);
		if (c == null) {
			c = new HashMap();
			data.put(src, c);
		}
		c.put(tg, tg.getDistanceTo(src));
	}

	public double getDistance(WorldLocation src, WorldLocation tg) {
		HashMap<WorldLocation, Double> c = data.get(src);
		if (c != null) {
			Double d = c.get(tg);
			return d != null ? d.doubleValue() : Double.POSITIVE_INFINITY;
		}
		return Double.POSITIVE_INFINITY;
	}

	public boolean isConnected(WorldLocation src, WorldLocation tg) {
		HashMap<WorldLocation, Double> c = data.get(src);
		return c != null && c.containsKey(tg);
	}

	public boolean removeSource(WorldLocation loc) {
		return data.remove(loc) != null;
	}

	public boolean removeLocation(WorldLocation loc) {
		boolean flag = this.removeSource(loc);
		for (HashMap<WorldLocation, Double> map : data.values()) {
			flag |= map.remove(loc) != null;
		}
		return flag;
	}

	public Set<WorldLocation> keySet() {
		return Collections.unmodifiableSet(data.keySet());
	}

	public Map<WorldLocation, Double> getTargets(WorldLocation loc) {
		Map map = data.get(loc);
		return map != null ? Collections.unmodifiableMap(map) : null;
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public void removeWorld(World world) {
		Collection<WorldLocation> c = new ArrayList();
		for (WorldLocation loc : data.keySet()) {
			if (loc.dimensionID == world.provider.dimensionId) {
				c.add(loc);
			}
		}
		for (WorldLocation loc : c)
			data.remove(loc);
	}

	public void clear() {
		data.clear();
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList li = new NBTTagList();
		for (WorldLocation src : data.keySet()) {
			NBTTagCompound entry = new NBTTagCompound();
			src.writeToNBT(entry);
			NBTTagList map = new NBTTagList();
			HashMap<WorldLocation, Double> dat = data.get(src);
			for (WorldLocation tg : dat.keySet()) {
				NBTTagCompound nbt = new NBTTagCompound();
				tg.writeToNBT(nbt);
				map.appendTag(nbt);
			}
			entry.setTag("map", map);
			li.appendTag(entry);
		}
		tag.setTag("locs", li);
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList li = tag.getTagList("locs", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound entry = (NBTTagCompound)o;
			WorldLocation src = WorldLocation.readFromNBT(entry);
			NBTTagList map = tag.getTagList("map", NBTTypes.COMPOUND.ID);
			for (Object o2 : li.tagList) {
				NBTTagCompound nbt = (NBTTagCompound)o2;
				WorldLocation tg = WorldLocation.readFromNBT(nbt);
				this.addLink(src, tg);
			}
		}
	}

}

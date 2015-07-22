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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Interfaces.Matcher;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class FluidHashMap<V> {

	private final HashMap<FluidKey, V> data = new HashMap();
	private ArrayList<FluidStack> sorted = null;
	private Collection<FluidStack> keyset = null;
	private boolean modifiedKeys = true;
	private Matcher<V> matcher = null;
	private boolean oneWay = false;
	private boolean GEqualMatching = false;

	public FluidHashMap() {

	}

	public FluidHashMap<V> setGEMatching(boolean match) {
		this.GEqualMatching = match;
		return this;
	}

	public FluidHashMap<V> setOneWay() {
		return this.setOneWay(null);
	}

	public FluidHashMap<V> setOneWay(Matcher m) {
		oneWay = true;
		this.matcher = m;
		return this;
	}

	private void updateKeysets() {
		this.modifiedKeys = false;
		this.keyset = this.createKeySet();
		sorted = new ArrayList(this.keySet());
		ReikaFluidHelper.sortFluids(sorted);
	}

	private V put(FluidKey is, V value) {
		if (oneWay && data.containsKey(is)) {
			if (matcher != null) {
				V v = data.get(is);
				if (v == value || matcher.match(v, value))
					return v;
			}
			throw new UnsupportedOperationException("This map does not support overwriting values! Fluid "+is+" already mapped to '"+data.get(is)+"'!");
		}
		V ret = data.put(is, value);
		this.modifiedKeys = true;
		return ret;
	}

	private V get(FluidKey is) {
		return data.get(is);
	}

	private boolean containsKey(FluidKey is) {
		return data.containsKey(is);
	}

	public V put(FluidStack is, V value) {
		return this.put(new FluidKey(is, this.GEqualMatching), value);
	}

	public V get(FluidStack is) {
		return this.get(new FluidKey(is, this.GEqualMatching));
	}

	public boolean containsKey(FluidStack is) {
		return this.containsKey(new FluidKey(is, this.GEqualMatching));
	}

	public boolean containsKey(Fluid i, int amt) {
		return this.containsKey(new FluidStack(i, amt));
	}

	public boolean containsKey(Fluid f) {
		return this.containsKey(f, -1);
	}

	public V put(Fluid i, int amt, V value) {
		return this.put(new FluidStack(i, amt), value);
	}

	public V get(Fluid i, int amt) {
		return this.get(new FluidStack(i, amt));
	}

	public int size() {
		return data.size();
	}

	public Collection<FluidStack> keySet() {
		if (this.modifiedKeys || keyset == null) {
			this.updateKeysets();
		}
		return Collections.unmodifiableCollection(keyset);
	}

	public Collection<V> values() {
		return Collections.unmodifiableCollection(data.values());
	}

	private Collection<FluidStack> createKeySet() {
		ArrayList<FluidStack> li = new ArrayList();
		for (FluidKey key : data.keySet()) {
			li.add(key.asFluidStack());
		}
		return li;
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public V remove(FluidStack is) {
		return this.remove(new FluidKey(is, this.GEqualMatching));
	}

	private V remove(FluidKey is) {
		if (oneWay)
			throw new UnsupportedOperationException("This map does not support removing values!");
		V ret = data.remove(is);
		this.modifiedKeys = true;
		return ret;
	}

	public boolean removeValue(V value) {
		return ReikaJavaLibrary.removeValuesFromMap(data, value);
	}

	public void clear() {
		if (oneWay)
			throw new UnsupportedOperationException("This map does not support removing values!");
		data.clear();
		this.modifiedKeys = true;
	}

	public List<FluidStack> sortedKeyset() {
		if (this.modifiedKeys || this.sorted == null) {
			this.updateKeysets();
		}
		return Collections.unmodifiableList(sorted);
	}

	public boolean isEmpty() {
		return this.data.isEmpty();
	}

	private static final class FluidKey implements Comparable<FluidKey> {

		public final Fluid itemID;
		private final int amount;
		private boolean gequal;

		private FluidKey(FluidStack is, boolean ge) {
			if (is == null)
				throw new MisuseException("You cannot add a null fluidstack to the map!");
			if (is.getFluid() == null)
				throw new MisuseException("You cannot add a null-fluid fluidstack to the map!");
			this.itemID = is.getFluid();
			this.amount = is.amount;
			this.gequal = ge;
		}

		@Override
		public int hashCode() {
			return itemID.hashCode()/* + metadata << 24*/;
		}

		@Override
		public boolean equals(Object o) {
			//ReikaJavaLibrary.pConsole(this+" & "+o);
			if (o instanceof FluidKey) {
				FluidKey i = (FluidKey)o;
				return i.itemID == itemID && (amount >= 0 && i.amount >= 0) ? (this.gequal ? i.amount >= this.amount : i.amount == amount) : true;
			}
			return false;
		}

		@Override
		public String toString() {
			return itemID.getUnlocalizedName()+":"+amount;
		}

		public FluidStack asFluidStack() {
			return new FluidStack(itemID, amount);
		}

		@Override
		public int compareTo(FluidKey o) {
			return ReikaFluidHelper.fluidStackComparator.compare(this.asFluidStack(), o.asFluidStack());
		}

	}

	@Override
	public FluidHashMap<V> clone() {
		FluidHashMap map = new FluidHashMap();
		for (FluidKey is : this.data.keySet()) {
			map.data.put(is, data.get(is));
		}
		return map;
	}

}

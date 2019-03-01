/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import Reika.DragonAPI.Interfaces.Matcher;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class FluidHashMap<V> {

	private final HashMap<String, ThresholdMapping<V>> data = new HashMap();
	private ArrayList<FluidStack> sorted = null;
	private Collection<FluidStack> keyset = null;
	private boolean modifiedKeys = true;
	private Matcher<V> matcher = null;
	private boolean oneWay = false;

	public FluidHashMap() {

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

	private V putKey(FluidStack is, V value) {
		ThresholdMapping<V> map = this.data.get(is.getFluid().getName());
		if (map == null) {
			map = new ThresholdMapping(new ReikaJavaLibrary.ReverseComparator());
			this.data.put(is.getFluid().getName(), map);
		}
		return map.addMapping(is.amount, value);
	}

	public V get(FluidStack is) {
		ThresholdMapping<V> map = this.data.get(is.getFluid().getName());
		if (map != null) {
			for (double key : map.keySet()) {
				if (is.amount >= key) {
					return map.getForValue(key, false);
				}
			}
		}
		return null;
	}

	public boolean containsKey(FluidStack is) {
		ThresholdMapping<V> map = this.data.get(is.getFluid().getName());
		if (map != null) {
			for (double key : map.keySet()) {
				if (is.amount >= key) {
					return true;
				}
			}
		}
		return false;
	}

	public V put(FluidStack is, V value) {
		if (oneWay && data.containsKey(is.getFluid().getName())) {
			if (matcher != null) {
				V v = this.get(is);
				if (v == value || matcher.match(v, value))
					return v;
			}
			throw new UnsupportedOperationException("This map does not support overwriting values! Fluid "+is+" already mapped to '"+this.get(is)+"'!");
		}
		V ret = this.putKey(is, value);
		this.modifiedKeys = true;
		return ret;
	}

	public boolean containsKey(Fluid i, int amt) {
		return this.containsKey(new FluidStack(i, amt));
	}

	public boolean containsKey(Fluid f) {
		return data.containsKey(f.getName());
	}

	public V put(Fluid i, int amt, V value) {
		return this.put(new FluidStack(i, amt), value);
	}

	public V get(Fluid i, int amt) {
		return this.get(new FluidStack(i, amt));
	}

	public int size() {
		return ReikaJavaLibrary.getNestedMapSize(data);
	}

	public Collection<FluidStack> keySet() {
		if (this.modifiedKeys || keyset == null) {
			this.updateKeysets();
		}
		return Collections.unmodifiableCollection(keyset);
	}

	public Collection<V> values() {
		Collection<V> li = new ArrayList();
		for (Object k : data.keySet()) {
			ThresholdMapping o = data.get(k);
			li.addAll(o.values());
		}
		return li;
	}

	private Collection<FluidStack> createKeySet() {
		ArrayList<FluidStack> li = new ArrayList();
		for (String fs : this.data.keySet()) {
			for (double s : this.data.get(fs).keySet()) {
				li.add(new FluidStack(FluidRegistry.getFluid(fs), (int)s));
			}
		}
		return li;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (String fs : data.keySet()) {
			Fluid f = FluidRegistry.getFluid(fs);
			sb.append(ReikaFluidHelper.fluidToString(f)+"="+data.get(fs));
			sb.append(",\n");
		}
		sb.append("}\n");
		return sb.toString();
	}

	public V remove(FluidStack is) {
		if (oneWay)
			throw new UnsupportedOperationException("This map does not support removing values!");
		V ret = this.removeKey(is);
		this.modifiedKeys = true;
		return ret;
	}

	private V removeKey(FluidStack is) {
		ThresholdMapping<V> map = this.data.get(is.getFluid().getName());
		return map != null ? map.remove(is.amount) : null;
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

	@Override
	public FluidHashMap<V> clone() {
		FluidHashMap map = new FluidHashMap();
		map.data.putAll(data);
		return map;
	}

}

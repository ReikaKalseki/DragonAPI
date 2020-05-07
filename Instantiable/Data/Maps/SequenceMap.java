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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class SequenceMap<V> {

	//private final ObjectWeb<V> data = new ObjectWeb();

	private final HashMap<V, TreeEntry<V>> data = new HashMap();

	public SequenceMap() {

	}

	public Collection<V> getParents(V obj) {
		TreeEntry t = data.get(obj);
		return t != null ? Collections.unmodifiableCollection(t.parents) : null;
	}

	public Collection<V> getChildren(V obj) {
		TreeEntry t = data.get(obj);
		return t != null ? Collections.unmodifiableCollection(t.children) : null;
	}

	public Collection<V> getRecursiveParents(V obj) {
		Collection c = new ArrayList();
		TreeEntry<V> tree = data.get(obj);
		if (tree != null) {
			for (V par : tree.parents) {
				c.add(par);
				c.addAll(this.getRecursiveParents(par));
			}
		}
		return c;
	}

	public Collection<V> getRecursiveChildren(V obj) {
		Collection c = new ArrayList();
		TreeEntry<V> tree = data.get(obj);
		if (tree != null) {
			for (V par : tree.children) {
				c.add(par);
				c.addAll(this.getRecursiveChildren(par));
			}
		}
		return c;
	}

	public void addParent(V obj, V parent) {
		this.addParent(obj, parent, true);
	}

	public void addChild(V obj, V child) {
		this.addChild(obj, child, true);
	}

	public void addChildless(V obj) {
		data.put(obj, new TreeEntry());
	}
	/*
	public void addOrphan(V obj) {
		V p = this.getOrphanParent();
		TreeEntry tree = this.data.get(p);
		if (tree == null) {
			tree = new TreeEntry();
			data.put(p, tree);
		}
		tree.children.add(obj);
	}

	private V getOrphanParent() {
		return null;
	}
	 */
	private void addParent(V obj, V parent, boolean cross) {
		TreeEntry tree = this.data.get(obj);
		if (tree == null) {
			tree = new TreeEntry();
			data.put(obj, tree);
		}
		tree.parents.add(parent);
		if (cross)
			this.addChild(parent, obj, false);
	}

	private void addChild(V obj, V child, boolean cross) {
		TreeEntry tree = this.data.get(obj);
		if (tree == null) {
			tree = new TreeEntry();
			data.put(obj, tree);
		}
		tree.children.add(child);
		if (cross)
			this.addParent(child, obj, false);
	}

	public boolean containsStep(V p1, V p2) {
		TreeEntry tree = this.data.get(p1);
		return tree != null && tree.children.contains(p2);
	}

	public boolean hasElementAsParent(V obj) {
		return this.data.containsKey(obj);
	}

	public boolean hasElementAsChild(V obj) {
		for (TreeEntry e : data.values()) {
			if (e.children.contains(obj))
				return true;
		}
		return false;
	}

	public void clear() {
		data.clear();
	}

	public Topology<V> getTopology() {
		return this.getTopology(new HashMap());
	}

	public Topology<V> getTopology(Map<V, Integer> initialValues) {
		return new Topology(this, initialValues);
	}

	public Collection valueSet() {
		Collection<V> values = new ArrayList();
		for (TreeEntry<V> t : data.values()) {
			values.addAll(t.children);
		}
		return values;
	}

	public Collection fullSet() {
		Collection<V> values = this.valueSet();
		for (V obj : data.keySet()) {
			if (!values.contains(obj))
				values.add(obj);
		}
		return values;
	}

	private static class TreeEntry<V> {

		private Collection<V> parents = new ArrayList();
		private Collection<V> children = new ArrayList();

		private TreeEntry() {

		}

	}

	public static class Topology<V> {

		private final SequenceMap<V> map;
		private final HashMap<V, Integer> depths = new HashMap();
		private final MultiMap<Integer, V> depthInverse = new MultiMap();

		private int maxDepth = 0;

		private Topology(SequenceMap m, Map<V, Integer> initialValues) {
			map = m;
			this.calculateDepths(initialValues);
		}

		private void calculateDepths(Map<V, Integer> initialValues) {
			Collection<V> c = new ArrayList(map.fullSet());
			for (V obj : c) {
				Integer base = initialValues.get(obj);
				int val = base != null ? base.intValue() : 0;
				depths.put(obj, val);
			}

			boolean change = false;
			do {
				change = false;
				Iterator<V> it = c.iterator();
				while (it.hasNext()) {
					V obj = it.next();
					boolean locchange = false;
					Collection<V> par = this.getParents(obj);
					for (V p : par) {
						int o = depths.get(obj);
						int d = depths.get(p)+1;
						if (d > o) {
							depths.put(obj, d);
							maxDepth = Math.max(maxDepth, d);
							change = true;
							locchange = true;
						}
					}
					if (!locchange)
						it.remove();
				}
			} while(change);

			for (Entry<V, Integer> e : depths.entrySet()) {
				this.depthInverse.addValue(e.getValue(), e.getKey());
			}

			if (this.depthInverse.keySet().size() < maxDepth) {
				//HashMap<V, Integer> old = new HashMap(depths);
				//ReikaJavaLibrary.pConsole(this.depthInverse);
				ArrayList<Integer> li = new ArrayList(this.depthInverse.keySet());
				HashMap<Integer, Integer> convert = new HashMap();
				Collections.sort(li);
				for (int idx = 0; idx < li.size(); idx++) {
					int val = li.get(idx);
					if (idx != val) {
						convert.put(val, idx);
					}
				}
				if (!convert.isEmpty()) {
					HashMap<Integer, Collection<V>> replInv = new HashMap();
					for (Entry<Integer, Integer> e : convert.entrySet()) {
						Collection<V> c2 = this.depthInverse.remove(e.getKey());
						replInv.put(e.getValue(), c2);
					}
					for (Entry<Integer, Collection<V>> e : replInv.entrySet()) {
						depthInverse.put(e.getKey(), e.getValue());
					}
					this.depths.clear();
					for (Integer depth : depthInverse.keySet()) {
						Collection<V> c2 = this.depthInverse.get(depth);
						for (V v : c2) {
							depths.put(v, depth);
						}
					}
					//HashSet<V> missing = new HashSet(old.keySet());
					//missing.removeAll(depths.keySet());
					//ReikaJavaLibrary.pConsole(missing);
				}
			}
		}

		public int getNumberParents(V obj) {
			return map.data.get(obj).parents.size();
		}

		public int getNumberChildren(V obj) {
			return map.data.get(obj).children.size();
		}

		public Collection<V> getChildren(V obj) {
			return map.getChildren(obj);
		}

		public Collection<V> getParents(V obj) {
			return map.getParents(obj);
		}

		public Map<V, Integer> getDepthMap() {
			return Collections.unmodifiableMap(depths);
		}

		public Collection<V> getByDepth(int depth) {
			return Collections.unmodifiableCollection(this.depthInverse.get(depth));
		}

		@Override
		public String toString() {
			return depths.toString();
		}

		public int getMaxDepth() {
			return maxDepth;
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (V v : data.keySet()) {
			TreeEntry<V> e = data.get(v);
			if (e.parents.isEmpty())
				sb.append(this.getKeyString(v));
		}
		return sb.toString();
	}

	private String getKeyString(V v) {
		StringBuilder sb = new StringBuilder();
		TreeEntry<V> e = data.get(v);
		sb.append(v);
		sb.append("={");
		for (V in : e.children) {
			sb.append(this.getKeyString(in));
		}
		sb.append("}");
		return sb.toString();
	}

}

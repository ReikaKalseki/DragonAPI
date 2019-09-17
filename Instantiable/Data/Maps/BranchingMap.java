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
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import Reika.DragonAPI.Libraries.Java.ReikaStringParser;


public class BranchingMap<V> {

	//private final ObjectWeb<V> data = new ObjectWeb();

	private final HashMap<V, TreeEntry<V>> data = new HashMap();

	public BranchingMap() {

	}

	public V getParent(V obj) {
		TreeEntry<V> t = data.get(obj);
		return t != null && t.parent != null ? t.parent.value : null;
	}

	public Collection<V> getChildren(V obj) {
		TreeEntry<V> t = data.get(obj);
		if (t == null)
			return null;
		ArrayList<V> ret = new ArrayList();
		for (TreeEntry<V> c : t.children) {
			ret.add(c.value);
		}
		return ret;
	}

	public Collection<V> getRecursiveChildren(V obj) {
		return this.getRecursiveChildren(data.get(obj));
	}

	private Collection<V> getRecursiveChildren(TreeEntry<V> tree) {
		Collection<V> c = new ArrayList();
		if (tree != null) {
			for (TreeEntry<V> par : tree.children) {
				c.add(par.value);
				c.addAll(this.getRecursiveChildren(par));
			}
		}
		return c;
	}

	public void addChildless(V obj) {
		data.put(obj, new TreeEntry(obj));
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

	public void addChild(V obj, V child) {
		TreeEntry<V> tree = this.data.get(obj);
		if (tree == null) {
			tree = new TreeEntry(obj);
			data.put(obj, tree);
		}
		data.put(child, tree.addChild(child));
	}

	public boolean containsStep(V p1, V p2) {
		TreeEntry<V> tree = this.data.get(p1);
		return tree != null && tree.children.contains(p2);
	}

	public boolean hasElementAsParent(V obj) {
		return this.data.containsKey(obj);
	}

	public boolean hasElementAsChild(V obj) {
		for (TreeEntry<V> e : data.values()) {
			if (e.containsChild(obj))
				return true;
		}
		return false;
	}

	public void clear() {
		data.clear();
	}

	public Topology<V> getTopology() {
		return new Topology(this);
	}

	public Collection valueSet() {
		Collection<V> values = new ArrayList();
		for (TreeEntry<V> t : data.values()) {
			values.addAll(t.getChildValues());
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

	/** Only works for nodes which exist as parents! */
	public LinkedList<V> getPathTo(V obj) {
		LinkedList<V> ret = new LinkedList();
		TreeEntry<V> e = this.data.get(obj);
		while (e != null) {
			ret.add(e.value);
			e = e.parent;
		}
		Collections.reverse(ret);
		return ret;
	}

	private static class TreeEntry<V> {

		private TreeEntry<V> parent;
		private Collection<TreeEntry<V>> children = new ArrayList();
		public final V value;

		private TreeEntry(V val) {
			this.value = val;
		}

		private TreeEntry<V> addChild(V c) {
			TreeEntry ret = new TreeEntry(c);
			ret.parent = this;
			this.children.add(ret);
			return ret;
		}

		private boolean containsChild(V c) {
			for (TreeEntry<V> e : this.children) {
				if (e.value.equals(c))
					return true;
			}
			return false;
		}

		private Collection<V> getChildValues() {
			Collection<V> c = new ArrayList();
			for (TreeEntry<V> e : this.children) {
				c.add(e.value);
			}
			return c;
		}

	}

	public static class Topology<V> {

		private final BranchingMap<V> map;
		private final HashMap<V, Integer> depths = new HashMap();
		private final MultiMap<Integer, V> depthInverse = new MultiMap();

		private int maxDepth = 0;

		private Topology(BranchingMap m) {
			map = m;
			this.calculateDepths();
		}

		private void calculateDepths() {
			Collection<V> c = new ArrayList(map.fullSet());
			for (V obj : c) {
				depths.put(obj, 0);
			}

			boolean change = false;
			do {
				change = false;
				Iterator<V> it = c.iterator();
				while (it.hasNext()) {
					V obj = it.next();
					boolean locchange = false;
					V par = this.getParent(obj);
					int o = depths.get(obj);
					int d = par == null ? 0 : depths.get(par)+1;
					if (d > o) {
						depths.put(obj, d);
						maxDepth = Math.max(maxDepth, d);
						change = true;
						locchange = true;
					}
					if (!locchange)
						it.remove();
				}
			} while(change);

			for (Entry<V, Integer> e : depths.entrySet()) {
				this.depthInverse.addValue(e.getValue(), e.getKey());
			}
		}

		public int getNumberChildren(V obj) {
			return map.data.get(obj).children.size();
		}

		public Collection<V> getChildren(V obj) {
			return map.getChildren(obj);
		}

		public V getParent(V obj) {
			return map.getParent(obj);
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

	private Collection<TreeEntry<V>> getRoots() {
		Collection<TreeEntry<V>> ret = new ArrayList();
		for (TreeEntry<V> e : data.values()) {
			if (e.parent == null) {
				ret.add(e);
			}
		}
		return ret;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Branching map:\n");
		for (TreeEntry<V> v : this.getRoots()) {
			if (v != null)
				sb.append(this.getString(v, 0));
		}
		return sb.toString();
	}

	private String getString(TreeEntry<V> e, int depth) {
		StringBuilder sb = new StringBuilder();
		sb.append(ReikaStringParser.getNOf(" ", 4*depth)+e.value+"\n");
		for (TreeEntry<V> e2 : e.children) {
			sb.append(this.getString(e2, depth+1));
		}
		return sb.toString();
	}

	private String getKeyString(V v) {
		StringBuilder sb = new StringBuilder();
		TreeEntry<V> e = data.get(v);
		sb.append(v);
		sb.append("={");
		for (TreeEntry<V> in : e.children) {
			sb.append(this.getKeyString(in.value));
		}
		sb.append("}");
		return sb.toString();
	}

}

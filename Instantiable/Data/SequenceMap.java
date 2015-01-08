/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SequenceMap<V> {

	//private final ObjectWeb<V> data = new ObjectWeb();

	private final HashMap<V, TreeEntry<V>> data = new HashMap();

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

	public boolean hasElement(V obj) {
		return this.data.containsKey(obj);
	}

	public Topology<V> getTopology() {
		return new Topology(this);
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
		//private final ArrayList<V> sortedList;

		private final Comparator topologySorter = new TopologySorter();

		private Topology(SequenceMap m) {
			map = m;
			this.calculateDepths();
			//this.sortedList = new ArrayList(map.data.keySet());
			//Collections.sort(this.sortedList, this.topologySorter);
		}

		private void calculateDepths() {
			Collection<V> c = new ArrayList(map.data.keySet());
			for (V obj : c) {
				//depths.addValue(this.getNumberParents(obj), obj);
				depths.put(obj, 0);
			}

			boolean change = false;
			do {
				change = false;
				Iterator<V> it = c.iterator();
				while (it.hasNext()) {
					V obj = it.next();
					//depths.addValue(this.getNumberParents(obj), obj);
					boolean locchange = false;
					Collection<V> par = this.getParents(obj);
					for (V p : par) {
						int o = depths.get(obj);
						int d = depths.get(p)+1;
						if (d > o) {
							depths.put(obj, d);
							change = true;
							locchange = true;
						}
					}
					if (!locchange)
						it.remove();
				}
			} while(change);
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

		//public List<V> getSortedList() {
		//	return Collections.unmodifiableList(this.sortedList);
		//}

		@Override
		public String toString() {
			return depths.toString();
		}

		private class TopologySorter implements Comparator<V> {

			@Override
			public int compare(V o1, V o2) {
				return Topology.this.getNumberParents(o1)-Topology.this.getNumberParents(o2);
			}

		}

	}

}

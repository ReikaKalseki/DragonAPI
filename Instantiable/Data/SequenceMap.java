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
import java.util.HashMap;


public class SequenceMap<V> {

	//private final ObjectWeb<V> data = new ObjectWeb();

	private final HashMap<V, TreeEntry<V>> data = new HashMap();

	public Collection<V> getParents(V obj) {
		Collection c = new ArrayList();
		TreeEntry<V> tree = data.get(obj);
		if (tree != null) {
			c.addAll(tree.parents);
		}
		return c;
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

	public Collection<V> getChildren(V obj) {
		Collection c = new ArrayList();
		TreeEntry<V> tree = data.get(obj);
		if (tree != null) {
			c.addAll(tree.children);
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

	private static class TreeEntry<V> {

		private Collection<V> parents = new ArrayList();
		private Collection<V> children = new ArrayList();

		private TreeEntry() {

		}

	}

}

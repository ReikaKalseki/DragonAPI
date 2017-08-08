/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class ObjectWeb<V> {

	private final HashMap<V, CountMap<V>> web = new HashMap();

	public ObjectWeb() {

	}

	public boolean isDirectionallyConnectedTo(V parent, V child) {
		if (!this.hasNode(parent))
			return false;
		return web.get(parent).containsKey(child);
	}

	public boolean isBilaterallyConnectedTo(V a, V b) {
		if (this.hasNode(a) && this.hasNode(b))
			return web.get(a).containsKey(b) || web.get(b).containsKey(a);
		else if (this.hasNode(a))
			return web.get(a).containsKey(b);
		else if (this.hasNode(b))
			return web.get(b).containsKey(a);
		return false;
	}

	public Collection<V> getChildren(V obj) {
		if (!this.hasNode(obj))
			return new HashSet();
		return Collections.unmodifiableCollection(web.get(obj).keySet());
	}

	public int getNumberConnections(V o1, V o2) {
		if (!this.hasNode(o1))
			return 0;
		return this.web.get(o1).get(o2);
	}

	public void addNode(V obj) {
		if (!this.hasNode(obj))
			web.put(obj, new CountMap());
	}

	public boolean hasNode(V obj) {
		return web.containsKey(obj);
	}

	public void addDirectionalConnection(V parent, V child) {
		if (!this.hasNode(parent))
			this.addNode(parent);
		this.addChild(parent, child);
	}

	public void addBilateralConnection(V a, V b) {
		if (!this.hasNode(a))
			this.addNode(a);
		this.addChild(a, b);

		if (!this.hasNode(b))
			this.addNode(b);
		this.addChild(b, a);
	}

	private void addChild(V parent, V child) {
		if (!this.hasNode(parent)) {
			DragonAPICore.logError("Cannot add a child to a nonexistent node "+parent+"!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		CountMap<V> li = web.get(parent);
		li.increment(child);
		web.put(parent, li);
	}

	public void removeChild(V parent, V child) {
		if (!this.hasNode(parent)) {
			DragonAPICore.logError("Cannot remove a child from a nonexistent node "+parent+"!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		CountMap<V> li = web.get(parent);
		if (!li.containsKey(child)) {
			DragonAPICore.logError("Child "+child+" does not exist for node "+parent+"! Cannot remove!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		li.remove(child);
		web.put(parent, li);
	}

	public Collection<V> objects() {
		return Collections.unmodifiableCollection(web.keySet());
	}

	public void clear() {
		web.clear();
	}

	@Override
	public String toString() {
		return web.toString();
	}

}

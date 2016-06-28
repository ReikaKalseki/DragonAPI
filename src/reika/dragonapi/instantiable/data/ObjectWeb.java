/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import reika.dragonapi.DragonAPICore;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;

public class ObjectWeb<V> {

	private final HashMap<V, ArrayList<V>> web = new HashMap();

	public ObjectWeb() {

	}

	public boolean isDirectionallyConnectedTo(V parent, V child) {
		if (!this.hasNode(parent))
			return false;
		return web.get(parent).contains(child);
	}

	public boolean isBilaterallyConnectedTo(V a, V b) {
		if (this.hasNode(a) && this.hasNode(b))
			return web.get(a).contains(b) || web.get(b).contains(a);
		else if (this.hasNode(a))
			return web.get(a).contains(b);
		else if (this.hasNode(b))
			return web.get(b).contains(a);
		return false;
	}

	public List<V> getChildren(V obj) {
		if (!this.hasNode(obj))
			return new ArrayList();
		return Collections.unmodifiableList(web.get(obj));
	}

	public void addNode(V obj) {
		if (!this.hasNode(obj))
			web.put(obj, new ArrayList());
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
		ArrayList<V> li = web.get(parent);
		if (li.contains(child)) {
			DragonAPICore.logError("Child "+child+" already exists for node "+parent+"!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		li.add(child);
		web.put(parent, li);
	}

	public void removeChild(V parent, V child) {
		if (!this.hasNode(parent)) {
			DragonAPICore.logError("Cannot remove a child from a nonexistent node "+parent+"!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		ArrayList<V> li = web.get(parent);
		if (!li.contains(child)) {
			DragonAPICore.logError("Child "+child+" does not exist for node "+parent+"! Cannot remove!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		li.remove(child);
		web.put(parent, li);
	}

}

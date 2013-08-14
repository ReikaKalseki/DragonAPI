/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.HashMap;
import java.util.List;

import Reika.DragonAPI.Libraries.ReikaJavaLibrary;

public class ObjectWeb {

	private HashMap<Object, List> web = new HashMap<Object, List>();

	public boolean isDirectionallyConnectedTo(Object parent, Object child) {
		if (!this.hasNode(parent))
			return false;
		return web.get(parent).contains(child);
	}

	public boolean isBilaterallyConnectedTo(Object a, Object b) {
		if (this.hasNode(a) && this.hasNode(b))
			return web.get(a).contains(b) || web.get(b).contains(a);
		else if (this.hasNode(a))
			return web.get(a).contains(b);
		else if (this.hasNode(b))
			return web.get(b).contains(a);
		return false;
	}

	public List getChildren(Object obj) {
		return web.get(obj);
	}

	public void addNode(Object obj) {
		if (!this.hasNode(obj))
			web.put(obj, null);
	}

	public boolean hasNode(Object obj) {
		return web.containsKey(obj);
	}

	public void addDirectionalConnection(Object parent, Object child) {
		if (this.hasNode(parent)) {
			this.addChild(parent, child);
		}
		else {
			web.put(parent, ReikaJavaLibrary.makeListFrom(child));
		}
	}

	public void addBilateralConnection(Object a, Object b) {
		if (this.hasNode(a)) {
			this.addChild(a, b);
		}
		else {
			web.put(a, ReikaJavaLibrary.makeListFrom(b));
		}

		if (this.hasNode(b)) {
			this.addChild(b, a);
		}
		else {
			web.put(b, ReikaJavaLibrary.makeListFrom(a));
		}
	}

	public void addChild(Object parent, Object child) {
		if (!this.hasNode(parent)) {
			ReikaJavaLibrary.pConsole("Cannot add a child to a nonexistent node "+parent+"!");
			Thread.dumpStack();
			return;
		}
		List li = web.get(parent);
		if (li.contains(child)) {
			ReikaJavaLibrary.pConsole("Child "+child+" already exists for node "+parent+"!");
			Thread.dumpStack();
			return;
		}
		li.add(child);
		web.put(parent, li);
	}

}

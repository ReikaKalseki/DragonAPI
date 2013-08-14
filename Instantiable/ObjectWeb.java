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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Reika.DragonAPI.Libraries.ReikaJavaLibrary;

public class ObjectWeb {

	private final HashMap<Object, List> web = new HashMap<Object, List>();

	private final Class nodeClass;

	public ObjectWeb() {
		nodeClass = null;
	}

	public ObjectWeb(Class node) {
		nodeClass = node;
	}

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
		if (nodeClass != null && nodeClass != obj.getClass()) {
			ReikaJavaLibrary.pConsole("Node "+obj+" is an invalid class type!");
			Thread.dumpStack();
			return;
		}
		if (!this.hasNode(obj))
			web.put(obj, new ArrayList());
	}

	public boolean hasNode(Object obj) {
		return web.containsKey(obj);
	}

	public void addDirectionalConnection(Object parent, Object child) {
		if (parent.getClass() != child.getClass()) {
			ReikaJavaLibrary.pConsole("Cannot add links between incompatible class types!");
			Thread.dumpStack();
			return;
		}
		if (!this.hasNode(parent))
			this.addNode(parent);
		this.addChild(parent, child);
	}

	public void addBilateralConnection(Object a, Object b) {
		if (a.getClass() != b.getClass()) {
			ReikaJavaLibrary.pConsole("Cannot add links between incompatible class types!");
			Thread.dumpStack();
			return;
		}

		if (!this.hasNode(a))
			this.addNode(a);
		this.addChild(a, b);

		if (!this.hasNode(b))
			this.addNode(b);
		this.addChild(b, a);
	}

	public void addChild(Object parent, Object child) {
		if (parent.getClass() != child.getClass()) {
			ReikaJavaLibrary.pConsole("Cannot add links between incompatible class types!");
			Thread.dumpStack();
			return;
		}

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

	public void removeChild(Object parent, Object child) {
		if (parent.getClass() != child.getClass()) {
			ReikaJavaLibrary.pConsole("Incompatible object class types!");
			Thread.dumpStack();
			return;
		}

		if (!this.hasNode(parent)) {
			ReikaJavaLibrary.pConsole("Cannot remove a child from a nonexistent node "+parent+"!");
			Thread.dumpStack();
			return;
		}
		List li = web.get(parent);
		if (!li.contains(child)) {
			ReikaJavaLibrary.pConsole("Child "+child+" does not exist for node "+parent+"! Cannot remove!");
			Thread.dumpStack();
			return;
		}
		li.remove(child);
		web.put(parent, li);
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ObjectWeb {

	private final HashMap<Object, List> web = new HashMap<Object, List>();

	private final Class nodeClass;

	public ObjectWeb(Class node) {
		if (node == null)
			throw new MisuseException("You must specify a node class!");
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
		if (!this.hasNode(obj))
			return new ArrayList();
		return web.get(obj);
	}

	public void addNode(Object obj) {
		if (!this.matchClass(obj)) {
			ReikaJavaLibrary.pConsole("Node "+obj+" is an invalid class type!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		if (!this.hasNode(obj))
			web.put(obj, new ArrayList());
	}

	public boolean hasNode(Object obj) {
		return web.containsKey(obj);
	}

	private boolean matchClass(Object o) {
		return nodeClass.isAssignableFrom(o.getClass());
	}

	private boolean matchClasses(Object o1, Object o2) {
		return nodeClass.isAssignableFrom(o1.getClass()) && nodeClass.isAssignableFrom(o2.getClass());
	}

	public void addDirectionalConnection(Object parent, Object child) {
		if (!this.matchClasses(parent, child)) {
			ReikaJavaLibrary.pConsole("Cannot add links between incompatible class types!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		if (!this.hasNode(parent))
			this.addNode(parent);
		this.addChild(parent, child);
	}

	public void addBilateralConnection(Object a, Object b) {
		if (!this.matchClasses(a, b)) {
			ReikaJavaLibrary.pConsole("Cannot add links between incompatible class types!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		if (!this.hasNode(a))
			this.addNode(a);
		this.addChild(a, b);

		if (!this.hasNode(b))
			this.addNode(b);
		this.addChild(b, a);
	}

	private void addChild(Object parent, Object child) {
		if (!this.matchClasses(parent, child)) {
			ReikaJavaLibrary.pConsole("Cannot add links between incompatible class types!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		if (!this.hasNode(parent)) {
			ReikaJavaLibrary.pConsole("Cannot add a child to a nonexistent node "+parent+"!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		List li = web.get(parent);
		if (li.contains(child)) {
			ReikaJavaLibrary.pConsole("Child "+child+" already exists for node "+parent+"!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		li.add(child);
		web.put(parent, li);
	}

	public void removeChild(Object parent, Object child) {
		if (!this.matchClasses(parent, child)) {
			ReikaJavaLibrary.pConsole("Incompatible object class types!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		if (!this.hasNode(parent)) {
			ReikaJavaLibrary.pConsole("Cannot remove a child from a nonexistent node "+parent+"!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		List li = web.get(parent);
		if (!li.contains(child)) {
			ReikaJavaLibrary.pConsole("Child "+child+" does not exist for node "+parent+"! Cannot remove!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		li.remove(child);
		web.put(parent, li);
	}

}

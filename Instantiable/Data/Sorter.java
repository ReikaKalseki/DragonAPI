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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class Sorter<O> {

	private final Element[] elements;

	private final HashMap<O, Integer> indexes = new HashMap();

	public Sorter(O... objects) {
		elements = new Element[objects.length];
		for (int i = 0; i < elements.length; i++) {
			O o = objects[i];
			if (indexes.containsKey(o))
				throw new MisuseException("You cannot have duplicate objects!");
			elements[i] = new Element(o);
			indexes.put(o, i);
		}
	}

	public void increment(O o) {
		int idx = indexes.get(o);
		elements[idx].count++;
	}

	public ArrayList<O> getSorted() {
		ArrayList<Element> li = ReikaJavaLibrary.makeListFromArray(elements);
		Collections.sort(li, new ElementSorter());
		ArrayList<O> ret = new ArrayList();
		for (Element e : li) {
			ret.add((O)e.object);
		}
		return ret;
	}

	private static class Element<O> {

		private final O object;
		private int count = 0;

		private Element(O o) {
			object = o;
		}

	}

	private static class ElementSorter implements Comparator<Element> {

		@Override
		public int compare(Element o1, Element o2) {
			return o2.count-o1.count;
		}
	}

}

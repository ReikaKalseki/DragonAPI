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

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Creates a data structure similar to a frequency graph, where any "x" value corresponds to
 * n "y" values, where n >= 0. It can also be converted into a bar graph. */
public class SortedPairs {

	private HashMap<Integer, ArrayList<Object>> data = new HashMap();
	private ArrayList<Integer> keys = new ArrayList();

	public SortedPairs addPair(int x, Object y) {
		if (keys.contains(x)) {
			ArrayList<Object> li = data.get(x);
			li.add(y);
			data.put(x, li);
		}
		else {
			keys.add(x);
			ArrayList li = new ArrayList();
			li.add(y);
			data.put(x, li);
		}
		return this;
	}

	public List<Object> getAllAtX(int x) {
		List li = data.get(x);
		return ReikaJavaLibrary.copyList(li);
	}

	public BarGraphData toBarGraph() {
		BarGraphData bdg = new BarGraphData();
		for (int i = 0; i < keys.size(); i++) {
			int key = keys.get(i);
			List li = this.getAllAtX(key);
			bdg.addEntries(key, li.size());
		}
		return bdg;
	}
}
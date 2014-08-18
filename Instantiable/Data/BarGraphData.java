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

public class BarGraphData {
	private HashMap<Integer, Integer> data = new HashMap();
	private ArrayList<Integer> values = new ArrayList();

	public BarGraphData() {

	}

	/** The entries are self-sorting. */
	public BarGraphData addEntries(int x, int number) {
		if (values.contains(x)) {
			int amt = data.get(x);
			data.put(x, amt+number);
		}
		else {
			int place = 0;
			for (int i = 0; i < values.size(); i++) {
				int p = values.get(i);
				if (x < p) {
					i = values.size();
					place = i;
				}
			}
			values.add(place, x);
			data.put(x, number);
		}
		return this;
	}

	public BarGraphData addOneEntry(int x) {
		return this.addEntries(x, 1);
	}

	public int getNumberEntries() {
		return values.size();
	}

	public int getYOfX(int x) {
		return data.get(x);
	}

	public List<Integer> getXValues() {
		return ReikaJavaLibrary.copyList(values);
	}

	public void clear() {
		data.clear();
		values.clear();
	}
}

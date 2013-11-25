package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class BarGraphData {
	private HashMap<Integer, Integer> data = new HashMap();
	private ArrayList<Integer> values = new ArrayList();

	public BarGraphData() {

	}

	public BarGraphData addEntries(int x, int number) {
		if (values.contains(x)) {
			int amt = data.get(x);
			data.put(x, amt+number);
		}
		else {
			values.add(x);
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
}

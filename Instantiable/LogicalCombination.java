package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Logic.LogicalOperators;

public class LogicalCombination implements Callable<Boolean> {

	private ArrayList<Callable<Boolean>> params = new ArrayList();
	public final LogicalOperators rule;

	public LogicalCombination(LogicalOperators lc) {
		rule = lc;
	}

	public void addArgument(Callable<Boolean> arg) {
		params.add(arg);
	}

	public Boolean call() throws Exception {
		boolean[] arr = new boolean[params.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = params.get(i).call();
		}
		return rule.evaluate(arr);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		ArrayList<String> li = this.writeToStrings();
		for (String s : li) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}

	public ArrayList<String> writeToStrings() {
		return this.writeToStrings(1);
	}

	private ArrayList<String> writeToStrings(int indent) {
		ArrayList<String> li = new ArrayList();
		String pre = ReikaStringParser.getNOf("\t", indent);
		if (indent == 1)
			li.add("{");
		for (Callable<Boolean> c : params) {
			if (c instanceof LogicalCombination) {
				LogicalCombination lg = (LogicalCombination)c;
				//s = lg.rule+" = {";
				li.addAll(lg.writeToStrings(indent+1));
			}
			else {
				li.add(pre+c.toString());
			}
		}
		if (indent == 1)
			li.add("}");
		return li;
	}

}

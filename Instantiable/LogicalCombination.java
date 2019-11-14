package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.function.Function;

import Reika.DragonAPI.Instantiable.IO.LuaBlock;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Logic.LogicalOperators;

public class LogicalCombination<E> implements Function<E, Boolean> {

	private ArrayList<Function<E, Boolean>> params = new ArrayList();
	public final LogicalOperators rule;

	public LogicalCombination(LogicalOperators lc) {
		rule = lc;
	}

	public void addArgument(Function<E, Boolean> arg) {
		params.add(arg);
	}

	public Boolean apply(E val) {
		boolean[] arr = new boolean[params.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = params.get(i).apply(val);
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
		for (Function<E, Boolean> c : params) {
			if (c instanceof LogicalCombination) {
				LogicalCombination lg = (LogicalCombination)c;
				//s = lg.rule+" = {";
				li.add(pre+lg.rule.toString()+" = {");
				li.addAll(lg.writeToStrings(indent+1));
				li.add(pre+"}");
			}
			else {
				li.add(pre+c.toString());
			}
		}
		if (indent == 1)
			li.add("}");
		return li;
	}

	public void populate(LuaBlock b, EvaluatorConstructor<E> ec) {
		for (String s : b.getKeys()) {
			String val = b.getString(s);
			params.add(ec.create(val));
		}
		for (LuaBlock c : b.getChildren()) {
			LogicalOperators lo = LogicalOperators.valueOf(c.name);
			LogicalCombination lc = new LogicalCombination(lo);
			lc.populate(c, ec);
			params.add(lc);
		}
	}

	public static interface EvaluatorConstructor<E> {

		public Function<E, Boolean> create(String s);

	}

}

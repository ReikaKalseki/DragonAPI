package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.MisuseException;

public class PatreonController {

	public static final PatreonController instance = new PatreonController();

	private final HashMap<DragonAPIMod, Patrons> data = new HashMap();

	private PatreonController() {

	}

	public void addPatron(DragonAPIMod mod, String name, int amt) {
		Patrons p = this.getOrCreate(mod);
		p.addPatron(name, amt);
	}

	private Patrons getOrCreate(DragonAPIMod mod) {
		Patrons p = data.get(mod);
		if (p == null) {
			p = new Patrons();
			data.put(mod, p);
		}
		return p;
	}

	public String getModPatrons(DragonAPIMod mod) {
		return data.get(mod).toString();
	}

	public int getAmount(DragonAPIMod mod, String name) {
		return data.get(mod).getAmount(name);
	}

	public Collection<String> getPatronsOver(DragonAPIMod mod, int amount) {
		return data.get(mod).getPatronsOver(amount);
	}

	public boolean isPatronAtLeast(DragonAPIMod mod, String name, int amount) {
		return data.get(mod).isPatronAtLeast(name, amount);
	}

	public int getTotal(DragonAPIMod mod) {
		return data.get(mod).getTotal();
	}

	@Override
	public String toString() {
		return data.toString();
	}

	private static class Patrons {

		private final HashMap<String, Integer> data = new HashMap();

		private int total;

		private void addPatron(String name, int amt) {
			if (data.containsKey(name)) {
				throw new MisuseException("You cannot have two copies of the same patron!");
			}
			else {
				data.put(name, amt);
				total += amt;
			}
		}

		public int getTotal() {
			return total;
		}

		private int getAmount(String name) {
			Integer e = data.get(name);
			return e != null ? e.intValue() : 0;
		}

		private Collection<String> getPatronsOver(int amount) {
			ArrayList<String> li = new ArrayList();
			for (String s : data.keySet()) {
				int f = data.get(s);
				if (f >= amount)
					li.add(s);
			}
			return li;
		}

		private boolean isPatronAtLeast(String name, int amount) {
			return this.getAmount(name) >= amount;
		}

		@Override
		public String toString() {
			return data.toString();
		}

	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import Reika.DragonAPI.Auxiliary.Trackers.DonatorController.Donator;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.MisuseException;

public final class PatreonController {

	public static final PatreonController instance = new PatreonController();

	private final HashMap<DragonAPIMod, Patrons> data = new HashMap();

	private PatreonController() {

	}

	public void addPatron(DragonAPIMod mod, String name, int amt) {
		this.addPatron(mod, name, null, amt);
	}

	public void addPatron(DragonAPIMod mod, String name, String ingame, int amt) {
		Patrons p = this.getOrCreate(mod);
		p.addPatron(name, ingame != null ? UUID.fromString(ingame) : null, amt);
	}

	private Patrons getOrCreate(DragonAPIMod mod) {
		Patrons p = data.get(mod);
		if (p == null) {
			p = new Patrons();
			data.put(mod, p);
		}
		return p;
	}

	public Collection<Donator> getModPatrons(DragonAPIMod mod) {
		return Collections.unmodifiableCollection(data.get(mod).data.keySet());
	}

	public int getAmount(DragonAPIMod mod, String name) {
		return data.get(mod).getAmount(name);
	}

	public Collection<Donator> getPatronsOver(DragonAPIMod mod, int amount) {
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

		private final HashMap<Donator, Integer> data = new HashMap();

		private int total;

		private void addPatron(String name, UUID id, int amt) {
			if (data.containsKey(name)) {
				throw new MisuseException("You cannot have two copies of the same patron!");
			}
			else {
				data.put(new Donator(name, id), amt);
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

		private Collection<Donator> getPatronsOver(int amount) {
			ArrayList<Donator> li = new ArrayList();
			for (Donator d : data.keySet()) {
				int f = data.get(d);
				if (f >= amount)
					li.add(d);
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

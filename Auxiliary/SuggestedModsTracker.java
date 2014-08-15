/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ModLogger;

import java.util.ArrayList;
import java.util.HashMap;

public class SuggestedModsTracker {

	private final HashMap<DragonAPIMod, ArrayList<SuggestedMod>> data = new HashMap();
	private final HashMap<DragonAPIMod, Boolean> print = new HashMap();

	public static final SuggestedModsTracker instance = new SuggestedModsTracker();

	private SuggestedModsTracker() {

	}

	private static class SuggestedMod {

		public final String reason;
		public final ModList mod;

		private SuggestedMod(ModList mod, String reason) {
			this.reason = reason;
			this.mod = mod;
		}

		public String getName() {
			return mod.getDisplayName();
		}

		public boolean isLoaded() {
			return mod.isLoaded();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof SuggestedMod) {
				SuggestedMod mod = (SuggestedMod)o;
				return this.mod == mod.mod;
			}
			return false;
		}
	}

	public void setPrint(DragonAPIMod mod, boolean doPrint) {
		print.put(mod, doPrint);
	}

	public void addSuggestedMod(DragonAPIMod mod, ModList suggested, String reason) {
		ArrayList<SuggestedMod> li = this.getOrCreate(mod);
		SuggestedMod s = new SuggestedMod(suggested, reason);
		if (!li.contains(s))
			li.add(s);
	}

	private ArrayList<SuggestedMod> getOrCreate(DragonAPIMod mod) {
		ArrayList<SuggestedMod> li = data.get(mod);
		if (li == null) {
			li = new ArrayList();
			data.put(mod, li);
			print.put(mod, true);
		}
		return li;
	}

	public void addSuggestedMods(DragonAPIMod mod, String reason, ModList... suggested) {
		for (int i = 0; i < suggested.length; i++) {
			this.addSuggestedMod(mod, suggested[i], reason);
		}
	}

	public void printConsole(DragonAPIMod mod) {
		if (print.get(mod)) {
			ModLogger log = mod.getModLogger();
			ArrayList<SuggestedMod> li = data.get(mod);
			for (int i = 0; i < li.size(); i++) {
				SuggestedMod sug = li.get(i);
				//if (!sug.isLoaded()) {
				String s = String.format("Consider installing %s: %s", sug.getName(), sug.reason);
				log.log(s);
				//}
			}
		}
	}

	public void printConsole() {
		for (DragonAPIMod mod : data.keySet()) {
			this.printConsole(mod);
		}
	}

}
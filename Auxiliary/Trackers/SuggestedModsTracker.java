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

import java.util.Collection;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.Data.MultiMap;
import Reika.DragonAPI.Instantiable.IO.ModLogger;

public class SuggestedModsTracker {

	private final MultiMap<DragonAPIMod, SuggestedMod> data = new MultiMap();

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

	public void addSuggestedMod(DragonAPIMod mod, ModList suggested, String reason) {
		SuggestedMod s = new SuggestedMod(suggested, reason);
		data.addValue(mod, s);
	}

	public void addSuggestedMods(DragonAPIMod mod, String reason, ModList... suggested) {
		for (int i = 0; i < suggested.length; i++) {
			this.addSuggestedMod(mod, suggested[i], reason);
		}
	}

	public void printConsole(DragonAPIMod mod) {
		ModLogger log = mod.getModLogger();
		Collection<SuggestedMod> li = data.get(mod);
		for (SuggestedMod sug : li) {
			if (!sug.isLoaded()) {
				String s = String.format("Consider installing %s: %s", sug.getName(), sug.reason);
				log.log(s);
			}
		}
	}

	public void printConsole() {
		for (DragonAPIMod mod : data.keySet()) {
			this.printConsole(mod);
		}
	}

}

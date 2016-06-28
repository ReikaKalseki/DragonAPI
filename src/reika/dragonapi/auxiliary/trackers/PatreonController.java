/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary.trackers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import reika.dragonapi.DragonAPICore;
import reika.dragonapi.auxiliary.trackers.DonatorController.Donator;
import reika.dragonapi.exception.MisuseException;
import reika.dragonapi.instantiable.data.maps.CountMap;
import reika.dragonapi.io.ReikaFileReader;
import reika.dragonapi.io.ReikaFileReader.ConnectionErrorHandler;
import reika.dragonapi.libraries.java.ReikaStringParser;

public final class PatreonController {

	public static final PatreonController instance = new PatreonController();

	public static final String reikaURL = "http://server.techjargaming.com/Reika/Donator/patreon_";

	private final HashMap<String, Patrons> data = new HashMap();

	private PatreonController() {

	}

	public void registerMod(String dev, String root) {
		String url = root+ReikaStringParser.stripSpaces(dev)+".txt";
		URL file = this.getURL(url);
		if (file == null) {
			DragonAPICore.logError("Could not create URL to patreon file. Donators will not be loaded.");
			return;
		}
		DonatorFile f = new DonatorFile(dev);
		ArrayList<String> lines = ReikaFileReader.getFileAsLines(file, 10000, false, f);
		if (lines != null) {
			DragonAPICore.log("Loading "+lines.size()+" patrons for "+dev);
			this.addPatrons(dev, lines);
		}
	}

	private void addPatrons(String dev, ArrayList<String> lines) {
		for (String s : lines) {
			s = ReikaStringParser.stripSpaces(s);
			String[] parts = s.split(":");
			parts[parts.length-1] = ReikaStringParser.clipStringBefore(parts[parts.length-1], "//");
			if (parts.length == 3) {
				this.addPatron(dev, parts[0], parts[1], Integer.parseInt(parts[2]));
			}
			else {
				this.addPatron(dev, parts[0], Integer.parseInt(parts[1]));
			}
		}
	}

	private URL getURL(String url) {
		try {
			return new URL(url);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static class DonatorFile implements ConnectionErrorHandler {

		private final String dev;

		private DonatorFile(String dev) {
			this.dev = dev;
		}

		@Override
		public void onServerRedirected() {
			DragonAPICore.logError("Donator server not found!");
		}

		@Override
		public void onNoInternet() {
			DragonAPICore.logError("Error accessing online file: Is your internet disconnected?");
		}

		@Override
		public void onServerNotFound() {
			DragonAPICore.logError("Donator server not found!");
		}

		@Override
		public void onTimedOut() {
			DragonAPICore.logError("Error accessing online file: Timed Out");
		}

	}

	private void addPatron(String dev, String name, int amt) {
		this.addPatron(dev, name, null, amt);
	}

	private void addPatron(String dev, String name, String ingame, int amt) {
		Patrons p = this.getOrCreate(dev);
		p.addPatron(name, ingame != null ? UUID.fromString(ingame) : null, amt);
		DragonAPICore.log("Adding patron to "+dev+": "+name+" / "+ingame+" @ $"+amt);
	}

	private Patrons getOrCreate(String dev) {
		Patrons p = data.get(dev);
		if (p == null) {
			p = new Patrons();
			data.put(dev, p);
		}
		return p;
	}

	public Collection<Donator> getModPatrons(String dev) {
		Patrons p = data.get(dev);
		return p != null ? Collections.unmodifiableCollection(p.data.keySet()) : new ArrayList();
	}

	public int getAmount(String dev, String name, UUID id) {
		return data.get(dev).getAmount(name, id);
	}

	public Collection<Donator> getPatronsOver(String dev, int amount) {
		return data.get(dev).getPatronsOver(amount);
	}

	public boolean isPatronAtLeast(String dev, String name, UUID id, int amount) {
		return data.get(dev).isPatronAtLeast(name, id, amount);
	}

	public int getTotal(String dev) {
		return data.get(dev).getTotal();
	}

	@Override
	public String toString() {
		return data.toString();
	}

	private static class Patrons {

		private final CountMap<Donator> data = new CountMap();

		private int total;

		private void addPatron(String name, UUID id, int amt) {
			Donator d = new Donator(name, id);
			if (data.containsKey(d)) {
				throw new MisuseException("You cannot have two copies of the same patron!");
			}
			else {
				data.increment(d, amt);
				total += amt;
			}
		}

		public int getTotal() {
			return total;
		}

		private int getAmount(String name, UUID id) {
			Donator d = new Donator(name, id);
			return data.get(d);
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

		private boolean isPatronAtLeast(String name, UUID id, int amount) {
			return this.getAmount(name, id) >= amount;
		}

		@Override
		public String toString() {
			return data.toString();
		}

	}

}

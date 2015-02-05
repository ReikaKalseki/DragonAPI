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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;

public final class DonatorController {

	public static final DonatorController instance = new DonatorController();

	private final HashMap<DragonAPIMod, DonationList> data = new HashMap();
	private final MultiMap<DragonAPIMod, Donator> byModDonators = new MultiMap();
	private final HashSet<Donator> reikaDonators = new HashSet();

	private DonatorController() {

	}

	/** This function does all the work for you. Provide the donation in dollar.cent amounts (eg 12.50F).
	 * Returns the total from this donator. */
	public float addDonation(DragonAPIMod mod, String donator, float donation) {
		return this.addDonation(mod, donator, null, donation);
	}
	/** This function does all the work for you. Provide the donation in dollar.cent amounts (eg 12.50F).
	 * Returns the total from this donator. */
	public float addDonation(DragonAPIMod mod, String donator, String ingame, float donation) {
		boolean flag = false;
		DonationList li = data.get(mod);
		if (li == null) {
			li = new DonationList();
			data.put(mod, li);
		}
		Donator d = li.addDonation(donator, ingame != null ? UUID.fromString(ingame) : null, donation);
		byModDonators.addValue(mod, d);
		if (mod.isReikasMod()) {
			reikaDonators.add(d);
		}
		return li.data.get(donator).donationAmount;
	}

	public String getTotalDonationsFromAllMods(String donator) {
		float amt = 0;
		for (DragonAPIMod mod : data.keySet()) {
			amt += this.getNumericalTotalDonationsFrom(mod, donator);
		}
		return String.format("$%.2f", amt);
	}

	private float getNumericalTotalDonationsFrom(DragonAPIMod mod, String donator) {
		DonationList li = data.get(mod);
		if (li != null) {
			Donation d = li.data.get(donator);
			if (d != null)
				return d.donationAmount;
		}
		return 0;
	}

	public String getTotalDonationsFrom(DragonAPIMod mod, String donator) {
		return String.format("$%.2f", this.getNumericalTotalDonationsFrom(mod, donator));

	}

	public Collection<Donator> getAllDonatorsFor(DragonAPIMod mod) {
		return Collections.unmodifiableCollection(byModDonators.get(mod));
	}

	public Set<Donator> getReikasDonators() {
		return Collections.unmodifiableSet(reikaDonators);
	}

	public boolean donatedTo(UUID ingame, DragonAPIMod mod) {
		Collection<Donator> c = byModDonators.get(mod);
		for (Donator d : c) {
			if (d.ingameName.equals(ingame))
				return true;
		}
		return false;
	}

	public String getDisplayList() {
		StringBuilder sb = new StringBuilder();
		sb.append(EnumChatFormatting.AQUA.toString());
		sb.append("Thank you to all these people whose donations made the following mods possible:");
		sb.append("\n\n");
		for (DragonAPIMod mod : data.keySet()) {
			DonationList li = data.get(mod);
			sb.append(EnumChatFormatting.BLUE.toString());
			sb.append(mod.getDisplayName());
			sb.append(":\n");
			sb.append(li.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	private static class Donation implements Comparable<Donation> {

		private final Donator donator;
		private float donationAmount;

		public Donation(Donator d) {
			this(d, 0);
		}

		public Donation(Donator d, float amt) {
			donator = d;
			donationAmount = amt;
		}

		@Override
		public String toString() {
			return String.format("  %s%s%s: %s%.2f", this.getDisplayColor().toString(), this.getFormatting(), donator.toString(), "$", donationAmount);
		}

		public String getFormatting() {
			return /*donationAmount >= 250 ? EnumChatFormatting.BOLD.toString() : */"";
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Donation) {
				Donation d = (Donation)o;
				return d.donationAmount == donationAmount && d.donator.equals(donator);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return donator.hashCode()+(int)(donationAmount*100);
		}

		public EnumChatFormatting getDisplayColor() {
			if (donationAmount >= 100) {
				return EnumChatFormatting.GOLD;
			}
			else if (donationAmount >= 50) {
				return EnumChatFormatting.LIGHT_PURPLE;
			}
			else if (donationAmount >= 20) {
				return EnumChatFormatting.GREEN;
			}
			else {
				return EnumChatFormatting.WHITE;
			}
		}

		@Override
		public int compareTo(Donation o) {
			return (int)Math.signum(o.donationAmount-donationAmount);
		}

	}

	public static class Donator {

		public final UUID ingameName;
		public final String displayName;

		Donator(String name, UUID ign) {
			displayName = name;
			ingameName = ign;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Donator) {
				Donator d = (Donator)o;
				if (d.ingameName == null || ingameName == null)
					return false;
				return d.ingameName.equals(ingameName) && d.displayName.equals(displayName);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return (ingameName != null ? ingameName.hashCode() : 0)^displayName.hashCode();
		}

		@Override
		public String toString() {
			return displayName+" ("+ingameName+")";
		}

	}

	private static class DonationList {

		private final HashMap<String, Donation> data = new HashMap();

		private Donator addDonation(String name, UUID ign, float amt) {
			Donator d = new Donator(name, ign);
			Donation dn = data.get(d);
			if (dn == null) {
				dn = new Donation(d);
				data.put(name, dn);
			}
			dn.donationAmount += amt;
			return d;
		}

	}
}

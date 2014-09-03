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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.MisuseException;

public final class DonatorController {

	public static final DonatorController instance = new DonatorController();

	private final HashMap<DragonAPIMod, DonationList> data = new HashMap();

	private DonatorController() {

	}

	/** This function does all the work for you. Provide the donation in dollar.cent amounts (eg 12.50F).
	 * Returns true if the donator had previous donations. */
	public boolean addDonation(DragonAPIMod mod, String donator, float donation) {
		boolean flag = false;
		DonationList li = data.get(mod);
		if (li == null) {
			li = new DonationList(donator, donation);
		}
		else {
			Donation d = new Donation(donator, donation);
			flag = li.hasPreviousFrom(donator);
			li.add(d);
		}
		data.put(mod, li);
		return flag;
	}

	public String getTotalDonationsFromAllMods(String donator) {
		float amt = 0;
		for (DragonAPIMod mod : data.keySet()) {
			amt += this.getNumericalTotalDonationsFrom(mod, donator);
		}
		return String.format("$%.2f", amt);
	}

	private float getNumericalTotalDonationsFrom(DragonAPIMod mod, String donator) {
		float amt = 0;
		DonationList li = data.get(mod);
		if (li == null)
			return 0;
		else {
			for (int i = 0; i < li.size(); i++) {
				Donation d = (Donation)li.get(i);
				if (d.displayName.equals(donator))
					amt += d.donationAmount;
			}
			return amt;
		}
	}

	public String getTotalDonationsFrom(DragonAPIMod mod, String donator) {
		float amt = 0;
		DonationList li = data.get(mod);
		if (li == null)
			return "$0.00";
		else {
			for (int i = 0; i < li.size(); i++) {
				Donation d = (Donation)li.get(i);
				if (d.displayName.equals(donator))
					amt += d.donationAmount;
			}
			return String.format("$%.2f", amt);
		}
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

	private class Donation {

		public final String displayName;
		public final float donationAmount;

		public Donation(String name, float amount) {
			displayName = name;
			donationAmount = amount;
		}

		@Override
		public String toString() {
			return String.format("  %s%s%s: %s%.2f", this.getDisplayColor().toString(), this.getFormatting(), displayName, "$", donationAmount);
		}

		public String getFormatting() {
			return /*donationAmount >= 250 ? EnumChatFormatting.BOLD.toString() : */"";
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Donation) {
				Donation d = (Donation)o;
				return d.displayName.equals(displayName) && d.donationAmount == donationAmount;
			}
			return false;
		}

		public boolean comesBefore(Donation d) {
			return donationAmount >= d.donationAmount;
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

		public Donation merge(Donation d) {
			return new Donation(displayName, donationAmount+d.donationAmount);
		}

		public Donation merge(Donation... dons) {
			float amt = donationAmount;
			for (int i = 0; i < dons.length; i++) {
				Donation d = dons[i];
				amt += d.donationAmount;
			}
			return new Donation(displayName, amt);
		}

	}

	private class DonationList extends ArrayList {

		/** Creates a new instance with one entry. */
		public DonationList(String donator, float amount) {
			Donation d = new Donation(donator, amount);
			this.add(d);
		}

		public boolean hasPreviousFrom(String donator) {
			for (int i = 0; i < this.size(); i++) {
				Donation d = (Donation)this.get(i);
				if (d.displayName.equals(donator))
					return true;
			}
			return false;
		}

		@Override
		public boolean add(Object o) {
			if (!(o instanceof Donation))
				throw new MisuseException("You can only use a DonationList to store donations!");
			Donation d = (Donation)o;
			boolean flag = this.hasPreviousFrom(d.displayName);
			int index = this.getInsertionIndex(d);
			if (index == -1)
				super.add(d);
			else
				super.add(index, d);
			if (flag)
				this.mergeAndSort(d);
			return true;
		}

		private void mergeAndSort(Donation src) {
			Iterator<Donation> it = this.iterator();
			while (it.hasNext()) {
				Donation d = it.next();
				if (d.displayName.equals(src.displayName)) {
					src = src.merge(d);
					it.remove();
				}
			}
			this.add(src);
		}

		private int getInsertionIndex(Donation d) {
			for (int i = 0; i < this.size(); i++) {
				Donation at = (Donation)this.get(i);
				if (d.comesBefore(at))
					return i;
			}
			return -1;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < this.size(); i++) {
				sb.append(this.get(i).toString());
				if (i < this.size()-1)
					sb.append("\n");
			}
			return sb.toString();
		}
	}
}

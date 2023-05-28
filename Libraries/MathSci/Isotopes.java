/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.MathSci;

import java.util.ArrayList;

import net.minecraft.util.EnumChatFormatting;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public enum Isotopes implements ChemicalElement {

	C14(5730, ReikaTimeHelper.YEAR, "Carbon-14", ElementGroup.NONMETAL),
	U235(704e6, ReikaTimeHelper.YEAR, "Uranium-235", ElementGroup.LANTHACTINIDE),
	U238(4.468e9, ReikaTimeHelper.YEAR, "Uranium-238", ElementGroup.LANTHACTINIDE),
	Pu239(2410, ReikaTimeHelper.YEAR, "Plutonium-239", ElementGroup.LANTHACTINIDE, true),
	Pu244(80.8e6, ReikaTimeHelper.YEAR, "Plutonium-244", ElementGroup.LANTHACTINIDE),
	Th232(14.05e9, ReikaTimeHelper.YEAR, "Thorium-232", ElementGroup.LANTHACTINIDE),
	Rn222(3.8235, ReikaTimeHelper.DAY, "Radon-222", ElementGroup.NONMETAL, true),
	Ra226(1601, ReikaTimeHelper.YEAR, "Radium-226", ElementGroup.ALKALI, true),
	Sr90(28.9, ReikaTimeHelper.YEAR, "Strontium-90", ElementGroup.ALKALI, true),
	Po210(138.376, ReikaTimeHelper.DAY, "Polonium-210", ElementGroup.TRANSITION, true),
	Cs134(2.065, ReikaTimeHelper.YEAR, "Cesium-134", ElementGroup.ALKALI),
	Xe135(6.57, ReikaTimeHelper.HOUR, "Xenon-135", ElementGroup.NONMETAL),
	Zr93(1.53e6, ReikaTimeHelper.YEAR, "Zirconium-93", ElementGroup.TRANSITION),
	Mo99(65.94, ReikaTimeHelper.HOUR, "Molybdenum-99", ElementGroup.TRANSITION),
	Cs137(30.17, ReikaTimeHelper.YEAR, "Cesium-137", ElementGroup.ALKALI, true),
	Tc99(211000, ReikaTimeHelper.YEAR, "Technetium-99", ElementGroup.TRANSITION),
	I131(8.02, ReikaTimeHelper.DAY, "Iodine-131", ElementGroup.NONMETAL, true),
	Pm147(2.62, ReikaTimeHelper.YEAR, "Promethium-147", ElementGroup.LANTHACTINIDE),
	I129(15.7e6, ReikaTimeHelper.YEAR, "Iodine-129", ElementGroup.NONMETAL),
	Sm151(90, ReikaTimeHelper.YEAR, "Samarium-151", ElementGroup.LANTHACTINIDE),
	Ru106(373.6, ReikaTimeHelper.DAY, "Ruthenium-106", ElementGroup.TRANSITION),
	Kr85(10.78, ReikaTimeHelper.YEAR, "Krypton-85", ElementGroup.NONMETAL),
	Pd107(6.5e6, ReikaTimeHelper.YEAR, "Palladium-107", ElementGroup.TRANSITION),
	Se79(327000, ReikaTimeHelper.YEAR, "Selenium-79", ElementGroup.NONMETAL),
	Gd155(4.76, ReikaTimeHelper.YEAR, "Gadolinium-155", ElementGroup.LANTHACTINIDE),
	Sb125(2.76, ReikaTimeHelper.YEAR, "Antimony-125", ElementGroup.TRANSITION),
	Sn126(230000, ReikaTimeHelper.YEAR, "Tin-126", ElementGroup.TRANSITION),
	Xe136(10e21, ReikaTimeHelper.YEAR, "Xenon-136", ElementGroup.NONMETAL), //basically stable
	//Mo95("Molybdenum-95", ElementGroup.TRANSITION),
	//Xe134("Xenon-134", ElementGroup.NONMETAL),
	//Nd143("Neodymium-143", ElementGroup.LANTHACTINIDE),
	//stable Cs133("Cesium-133", ElementGroup.ALKALI), //so stable it is used in atomic clocks
	I135(6.6, ReikaTimeHelper.HOUR, "Iodine-135", ElementGroup.NONMETAL),
	Xe131(12, ReikaTimeHelper.DAY, "Xenon-131", ElementGroup.NONMETAL),
	//Nd145("Neodymium-145", ElementGroup.LANTHACTINIDE),
	//Ru101("Ruthenium-101", ElementGroup.TRANSITION),
	Ru103(1.69, ReikaTimeHelper.TICK, "Ruthenium-103", ElementGroup.TRANSITION), //actually millis
	//Kr83("Krypton-83", ElementGroup.NONMETAL),
	Pm149(53.08, ReikaTimeHelper.HOUR, "Promethium-149", ElementGroup.LANTHACTINIDE),
	Rh105(35.36, ReikaTimeHelper.HOUR, "Rhodium-105", ElementGroup.TRANSITION),
	//I127("Iodine-127", ElementGroup.NONMETAL);
	;

	private final double half;
	public final int atomicWeight;
	public final String element;
	private final ReikaTimeHelper base;
	private final String name;
	public final boolean extraDanger;
	public final ElementGroup group;
	private DecayData decay;

	private static final Isotopes[] isoList = values();

	private Isotopes(double t, ReikaTimeHelper time, String n, ElementGroup g) {
		this(t, time, n, g, false);
	}

	private Isotopes(double t, ReikaTimeHelper time, String n, ElementGroup g, boolean d) {
		half = t;
		base = time;
		name = n;
		extraDanger = d;
		group = g;
		String s = this.name();
		while (!Character.isDigit(s.charAt(0)))
			s = s.substring(1);
		atomicWeight = Integer.parseInt(s);
		s = this.name();
		while (Character.isDigit(s.charAt(s.length()-1)))
			s = s.substring(0, s.length()-1);
		element = s;
	}

	public static Isotopes getIsotope(int i) {
		return isoList[i];
	}

	public static int getNumberIsotopes() {
		return isoList.length;
	}

	/** In ticks */
	public double getHalfLife() {
		return half*base.getDuration();
	}

	/** In ticks */
	public double getMCHalfLife() {
		return half*base.getMinecraftDuration();
	}

	@Override
	public String toString() {
		return name;
	}

	public String getDisplayName() {
		if (extraDanger) {
			return EnumChatFormatting.RED.toString()+name;
		}
		else
			return name;
	}

	public DecayData getDecay() {
		return decay;
	}

	public int getAtomicWeight() {
		return atomicWeight;
	}

	@Override
	public String getChemicalSymbol() {
		return element;
	}

	public String getHalfLifeAsDisplay() {
		return String.format("%.3f%s%s", ReikaMathLibrary.getThousandBase(half), ReikaEngLibrary.getSIPrefix(half), base.getAbbreviation());
	}

	public static enum ElementGroup {
		ALKALI("Alkali Metals"),
		TRANSITION("Transition Metals"),
		LANTHACTINIDE("Lanthanides and Actinides"),
		NONMETAL("Nonmetals");

		public final String displayName;

		private ElementGroup(String n) {
			displayName = n;
		}
	}

	public static class DecayData {

		public final ChemicalElement isotope;
		public final double amount;

		private DecayData(ChemicalElement iso, double amt) {
			isotope = iso;
			amount = amt;
		}

	}

	static {
		for (Isotopes s : Isotopes.isoList) {
			s.decay = computeDecayData(s);
		}
	}

	private static DecayData computeDecayData(Isotopes s) {
		ArrayList<ChemicalElement> li = new ArrayList(ReikaJavaLibrary.makeListFromArray(Isotopes.isoList));
		li.add(new ChemicalElement() {
			@Override
			public int getAtomicWeight() {
				return 204;
			}

			@Override
			public String getChemicalSymbol() {
				return "Pb";
			}
		});
		li.add(new ChemicalElement() {
			@Override
			public int getAtomicWeight() {
				return 206;
			}

			@Override
			public String getChemicalSymbol() {
				return "Pb";
			}
		});
		li.add(new ChemicalElement() {
			@Override
			public int getAtomicWeight() {
				return 207;
			}

			@Override
			public String getChemicalSymbol() {
				return "Pb";
			}
		});
		li.add(new ChemicalElement() {
			@Override
			public int getAtomicWeight() {
				return 208;
			}

			@Override
			public String getChemicalSymbol() {
				return "Pb";
			}
		});
		for (ChemicalElement s2 : li) {
			if (s2.getAtomicWeight() == s.atomicWeight+1) {
				return new DecayData(s2, 1);
			}
		}
		for (ChemicalElement s2 : li) {
			if (s2.getAtomicWeight() == s.atomicWeight+2) {
				return new DecayData(s2, 0.5);
			}
		}
		ChemicalElement use = null;
		for (ChemicalElement s2 : li) {
			if ((s2 instanceof Isotopes && s2 == s) || s2.getAtomicWeight() > s.atomicWeight-3)
				continue;
			if (use == null || s2.getAtomicWeight() > use.getAtomicWeight())
				use = s2;
		}
		return use == null ? null : new DecayData(use, s.atomicWeight/(double)use.getAtomicWeight());
	}
}

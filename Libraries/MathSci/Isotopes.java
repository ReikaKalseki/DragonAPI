/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.MathSci;

import net.minecraft.util.EnumChatFormatting;

public enum Isotopes {

	C14(5730, ReikaTimeHelper.YEAR, "Carbon-14"),
	U235(704e6, ReikaTimeHelper.YEAR, "Uranium-235"),
	U238(4.468e9, ReikaTimeHelper.YEAR, "Uranium-238"),
	Pu239(2410, ReikaTimeHelper.YEAR, "Plutonium-239", true),
	Pu244(80.8e6, ReikaTimeHelper.YEAR, "Plutonium-244"),
	Th232(14.05e9, ReikaTimeHelper.YEAR, "Thorium-232"),
	Rn222(3.8235, ReikaTimeHelper.DAY, "Radon-222", true),
	Ra226(1601, ReikaTimeHelper.YEAR, "Radium-226", true),
	Sr90(28.9, ReikaTimeHelper.YEAR, "Strontium-90", true),
	Po210(138.376, ReikaTimeHelper.DAY, "Polonium-210", true),
	Cs134(2.065, ReikaTimeHelper.YEAR, "Cesium-134"),
	Xe135(6.57, ReikaTimeHelper.HOUR, "Xenon-135"),
	Zr93(1.53e6, ReikaTimeHelper.YEAR, "Zirconium-93"),
	Mo99(65.94, ReikaTimeHelper.HOUR, "Molybdenum-99"),
	Cs137(30.17, ReikaTimeHelper.YEAR, "Cesium-137", true),
	Tc99(211000, ReikaTimeHelper.YEAR, "Technetium-99"),
	I131(8.02, ReikaTimeHelper.DAY, "Iodine-131", true),
	Pm147(2.62, ReikaTimeHelper.YEAR, "Promethium-147"),
	I129(15.7e6, ReikaTimeHelper.YEAR, "Iodine-129"),
	Sm151(90, ReikaTimeHelper.YEAR, "Samarium-151"),
	Ru106(373.6, ReikaTimeHelper.DAY, "Ruthenium-106"),
	Kr85(10.78, ReikaTimeHelper.YEAR, "Krypton-85"),
	Pd107(6.5e6, ReikaTimeHelper.YEAR, "Palladium-107"),
	Se79(327000, ReikaTimeHelper.YEAR, "Selenium-79"),
	Gd155(4.76, ReikaTimeHelper.YEAR, "Gadolinium-155"),
	Sb125(2.76, ReikaTimeHelper.YEAR, "Antimony-125"),
	Sn126(230000, ReikaTimeHelper.YEAR, "Tin-126"),
	Xe136(10e21, ReikaTimeHelper.YEAR, "Xenon-136"), //basically stable
	//Mo95("Molybdenum-95"),
	//Xe134("Xenon-134"),
	//Nd143("Neodymium-143"),
	//stable Cs133("Cesium-133"), //so stable it is used in atomic clocks
	I135(6.6, ReikaTimeHelper.HOUR, "Iodine-135"),
	Xe131(12, ReikaTimeHelper.DAY, "Xenon-131"),
	//Nd145("Neodymium-145"),
	//Ru101("Ruthenium-101"),
	Ru103(1.69, ReikaTimeHelper.TICK, "Ruthenium-103"), //actually millis
	//Kr83("Krypton-83"),
	Pm149(53.08, ReikaTimeHelper.HOUR, "Promethium-149"),
	Rh105(35.36, ReikaTimeHelper.HOUR, "Rhodium-105"),
	//I127("Iodine-127");
	;

	private double half;
	private ReikaTimeHelper base;
	private String name;
	private boolean danger;

	private static final Isotopes[] isoList = values();

	private Isotopes(double t, ReikaTimeHelper time, String n) {
		this(t, time, n, false);
	}

	private Isotopes(double t, ReikaTimeHelper time, String n, boolean d) {
		half = t;
		base = time;
		name = n;
		danger = d;
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

	public boolean isExtraDangerous() {
		return danger;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getDisplayName() {
		if (danger) {
			return EnumChatFormatting.RED.toString()+name;
		}
		else
			return name;
	}

	public String getHalfLifeAsDisplay() {
		return String.format("%.3f%s%s", ReikaMathLibrary.getThousandBase(half), ReikaEngLibrary.getSIPrefix(half), base.getAbbreviation());
	}
}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.MathSci;

import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;



public class ReikaNuclearHelper {

	/** In eV */
	public static final double URANIUM_FISSION_ENERGY = 2e8;
	public static final double AVOGADRO = 6.02e23;

	public static final double NEUTRON_ESCAPE_SPEED = 2752;

	public static double getEnergyJ(double E) {
		return E*ReikaPhysicsHelper.ELECTRON_CHARGE;
	}

	public static double getUraniumFissionNeutronE() {
		return 0.5*NEUTRON_ESCAPE_SPEED*NEUTRON_ESCAPE_SPEED*ReikaPhysicsHelper.NEUTRON_MASS*AVOGADRO;
	}

	public static double getWasteDecayHeat() {
		return ReikaThermoHelper.getTemperatureIncrease(1, 15000, AVOGADRO*getEnergyJ(0.5));
	}

	public static double getDecayChanceFromHalflife(double halflife) {
		return Math.log(2)/halflife;
	}

	public static boolean shouldDecay(Isotopes iso) {
		return shouldDecay(iso, 1);
	}

	public static boolean shouldDecay(Isotopes iso, double multiplier) {
		double chance = 6*multiplier*getDecayChanceFromHalflife(iso.getMCHalfLife());
		return ReikaRandomHelper.doWithChance(chance);
	}

}

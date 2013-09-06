/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.MathSci;



public class ReikaNuclearHelper {

	/** In eV */
	public static final double URANIUM_FISSION_ENERGY = 200*1000000D;
	public static final double AVOGADRO = 6.02*ReikaMathLibrary.doubpow(10, 23);

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
		return ReikaMathLibrary.doWithChance(getDecayChanceFromHalflife(iso.getMCHalfLife()));
	}

}

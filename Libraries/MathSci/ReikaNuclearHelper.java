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
	public static final double ELECTRON_CHARGE = 1.602/ReikaMathLibrary.doubpow(10, 19);
	public static final double ELECTRON_MASS = 9.11/ReikaMathLibrary.doubpow(10, 31);
	public static final double NEUTRON_MASS = 1.674/ReikaMathLibrary.doubpow(10, 27);
	public static final double PROTON_MASS = 1.672/ReikaMathLibrary.doubpow(10, 27);

	public static final double NEUTRON_ESCAPE_SPEED = 2752;

	public static double getEnergyJ(double E) {
		return E*ELECTRON_CHARGE;
	}

	public static double getUraniumFissionNeutronE() {
		return 0.5*NEUTRON_ESCAPE_SPEED*NEUTRON_ESCAPE_SPEED*NEUTRON_MASS*AVOGADRO;
	}

}

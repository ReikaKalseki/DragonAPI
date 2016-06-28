/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.power;

public class ReikaEUHelper {

	private static final double WATTS_PER_EU_default = 2080D;
	private static final double WATTS_PER_EU_legacy = 22512D;

	public static double getWattsPerEU() {
		return ReikaRFHelper.getWattsPerRF()*4D;
	}

	public static int getIC2TierFromEUVoltage(double voltage) {
		if (voltage >= 8192) { //"Ultra"
			return 5;
		}
		if (voltage >= 2048) { //EV
			return 4;
		}
		else if (voltage >= 512) { //HV
			return 3;
		}
		else if (voltage >= 33) { //MV, and yes, 33 not 32
			return 2;
		}
		else if (voltage >= 6) { //LV
			return 1;
		}
		return 0; //"Micro"
	}

	public static int getIC2TierFromPower(double power) {
		return getIC2TierFromEUVoltage(power/getWattsPerEU());
	}

	public static enum Voltage {
		MICRO("mV", 0, 5),
		LOW("LV", 6, 32),
		MEDIUM("MV", 33, 511),
		HIGH("HV", 512, 2047),
		EXTREME("EV", 2048, 8191),
		ULTRA("UV", 8192, Integer.MAX_VALUE);

		public final int minVoltage;
		public final int maxVoltage;
		public final String abbrev;

		public static final Voltage[] voltages = values();

		private Voltage(String s, int min, int max) {
			minVoltage = min;
			maxVoltage = max;
			abbrev = s;
		}

		@Override
		public String toString() {
			return abbrev+" ("+minVoltage+" - "+maxVoltage+" EU/t)";
		}
	}

}

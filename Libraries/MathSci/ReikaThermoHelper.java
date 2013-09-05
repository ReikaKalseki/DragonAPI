package Reika.DragonAPI.Libraries.MathSci;

public class ReikaThermoHelper {

	public static final double WATER_HEAT = 4.18;
	public static final double GRAPHITE_HEAT = 0.71;

	/** Heat energy in one block */
	public static final double WATER_BLOCK_HEAT = 4.18*1000000D;

	/** E = mc delta T */
	public static double getTemperatureIncrease(double C, double M, double E) {
		return E/M/C;
	}
}

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ReikaMathCacher {

	private static final HashMap<Long, Double> log2Cache = new HashMap();
	private static final HashMap<List<Double>, Double> powerCache = new HashMap();
	private static final HashMap<Integer, Double> sinCache = new HashMap();
	private static final HashMap<Integer, Double> cosCache = new HashMap();
	private static final HashMap<Integer, Double> tanCache = new HashMap();
	private static final HashMap<Double, Double> asinCache = new HashMap();
	private static final HashMap<Double, Double> acosCache = new HashMap();
	private static final HashMap<List<Double>, Double> atan2Cache = new HashMap();

	public static void initalize() {
		for (long i = 1; i <= Integer.MAX_VALUE; i *= 2L) {
			log2Cache.put(i, ReikaMathLibrary.logbase(i, 2));
		}

		for (int i = 0; i < 360; i++) {
			double rad = Math.toRadians(i);
			sinCache.put(i, Math.sin(rad));
			cosCache.put(i, Math.cos(rad));
			tanCache.put(i, Math.tan(rad));
		}

		for (double d = -1; d <= 1; d += 0.03125) {
			asinCache.put(d, Math.asin(d));
			acosCache.put(d, Math.acos(d));
		}
		/*
		for (int i = -30; i <= 30; i++) {
			for (int k = -30; k <= 30; k++) {
				atan2Cache.put(Arrays.asList((double)i,(double)k), Math.atan2(i, k));
			}
		}*/
	}

	public static double log2(long val) {
		Double ret = log2Cache.get(val);
		return ret != null ? ret.doubleValue() : ReikaMathLibrary.logbase2(val);
	}

	public static double atan2(double d1, double d2) {
		List<Double> key = Arrays.asList(d1, d2);
		Double ang = atan2Cache.get(key);
		if (ang == null) {
			ang = Math.atan2(d1, d2);
			atan2Cache.put(key, ang);
		}
		return ang.doubleValue();
	}

	public static double pow(double base, double power) {
		List<Double> key = Arrays.asList(base, power);
		Double pow = powerCache.get(key);
		if (pow == null) {
			pow = Math.pow(base, power);
			powerCache.put(key, pow);
		}
		return pow.doubleValue();
	}

	public static double sin(int deg) {
		return sinCache.get(deg);
	}

	public static double cos(int deg) {
		return cosCache.get(deg);
	}

	public static double tan(int deg) {
		return tanCache.get(deg);
	}

}

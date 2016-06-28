/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries.java;

import reika.dragonapi.DragonAPICore;
import reika.dragonapi.instantiable.data.WeightedRandom;
import reika.dragonapi.instantiable.data.immutable.DecimalPosition;
import reika.dragonapi.libraries.mathsci.ReikaPhysicsHelper;

public class ReikaRandomHelper extends DragonAPICore {

	/** Like rand.getInt(), but will not crash for n <= 0 */
	public static int getSafeRandomInt(int val) {
		return val > 1 ? rand.nextInt(val) : 0;
	}

	/** Gets a random double value within base +/- range. */
	public static double getRandomPlusMinus(double base, double range) {
		double add = -range+rand.nextDouble()*range*2;
		return (base+add);
	}

	/** Gets a random integer value within base +/- range. */
	public static int getRandomPlusMinus(int base, int range) {
		int add = -range+rand.nextInt(range*2+1);
		return base+add;
	}

	/** Returns true with a percentage probability. Args: chance (out of 1 or a %) */
	public static boolean doWithChance(double num) {
		if (num >= 100)
			return true;
		if (num > 1)
			num /= 100D;
		if (num >= 1)
			return true;
		if (num <= 0)
			return false;

		if (num < 10e-15) { //to help precision
			return rand.nextDouble()*10e12 < num*10e12;
		}

		return rand.nextDouble() < num;
	}

	public static short getRandomShort(int max) {
		int lim = max > 0 ? Math.min(max, Short.MAX_VALUE+1) : Short.MAX_VALUE+1;
		return (short)rand.nextInt(lim);
	}

	public static short getRandomShort() {
		return getRandomShort(Short.MAX_VALUE+1);
	}

	public static byte getRandomByte(int max) {
		int lim = max > 0 ? Math.min(max, Byte.MAX_VALUE+1) : Byte.MAX_VALUE+1;
		return (byte)rand.nextInt(lim);
	}

	public static byte getRandomByte() {
		return getRandomByte(Byte.MAX_VALUE+1);
	}

	/** Returns a random number less than {@code n} such that the probability of a given value {@code val} is equal to <br> {@code (val+1)/n!}. */
	public static int getLinearRandom(int n) {
		WeightedRandom<Integer> r = new WeightedRandom();
		for (int i = 0; i < n; i++) {
			r.addEntry(i, i+1);
		}
		return r.getRandomEntry();
	}

	/** Returns a random number less than {@code n} such that the probability of a given value {@code val} is equal to <br> {@code (n-val+1)/n!}. */
	public static int getInverseLinearRandom(int n) {
		WeightedRandom<Integer> r = new WeightedRandom();
		for (int i = 0; i < n; i++) {
			r.addEntry(n-i-1, i+1);
		}
		return r.getRandomEntry();
	}

	public static String generateRandomString(int len) {
		byte[] b = new byte[len];
		for (int i = 0; i < len; i++) {
			b[i] = (byte)(32+rand.nextInt(95));
		}
		return new String(b);
	}

	public static DecimalPosition getRandomSphericalPosition(double x, double y, double z, int r) {
		double dr = rand.nextDouble()*r;
		double[] d = ReikaPhysicsHelper.polarToCartesian(dr, rand.nextInt(360), rand.nextInt(360));
		return new DecimalPosition(x+d[0], y+d[1], z+d[2]);
	}

	public static int getRandomBetween(int min, int max) {
		return min+rand.nextInt(1+max-min);
	}

	public static double getRandomBetween(double min, double max) {
		return min+rand.nextDouble()*(max-min);
	}

}

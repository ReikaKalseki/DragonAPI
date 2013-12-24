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

import net.minecraft.util.MathHelper;
import Reika.DragonAPI.DragonAPICore;

public final class ReikaMathLibrary extends DragonAPICore {

	public static final double e = Math.E;				// s/e
	public static final double pi = Math.PI;			// s/e

	/** Returns the pythagorean sum of the three inputs. Used mainly for vector magnitudes.
	 * Args: x,y,z */
	public static double py3d(double dx, double dy, double dz) {
		double val;
		val = dx*dx+dy*dy+dz*dz;
		return MathHelper.sqrt_double(val);
	}

	/** Returns true if the input is within a percentage of its size of another value.
	 * Args: Input, target, percent tolerance */
	public static boolean approxp(double input, double target, double percent) {
		double low = input - input*percent/100;
		double hi = input + input*percent/100;
		if ((target >= low) && (target <= hi))
			return true;
		else
			return false;
	}

	/** Returns true if the input is within [target-range,target+range]. Args: input, target, range */
	public static boolean approxr(double input, double target, double range) {
		double low = input - range;
		double hi = input + range;
		if ((target >= low) && (target <= hi))
			return true;
		else
			return false;
	}

	/** Returns the value of a double raised to an integer power. Args: Base, power */
	public static double intpow(double base, int pow) {
		double val = 1.0D;
		if (pow == 0)
			return val;
		if (pow > 0) {
			for (int i = 0; i < pow; i++) {
				val *= base;
			}
		}
		else {
			pow = -pow;
			for (int i = 0; i < pow; i++) {
				val /= base;
			}
		}
		return val;
	}

	/** Returns the value of an integer raised to a POSITIVE integer power. Args: Base, power */
	public static int intpow2(int base, int pow) {
		int val = 1;
		for (int i = 0; i < pow; i++) {
			val *= base;
		}
		return val;
	}

	/** Returns the value of a double raised to an decimal power. Args: Base, power */
	public static double doubpow(double base, double pow) {
		double val = 1.0D;
		return Math.pow(base, pow);
	}

	/** Returns a random integer between two specified bounds. Args: Low bound, hi bound */
	public static int randinrange(int low, int hi) {
		int val = low;
		// += Random.nextInt(hi-low);
		return val;
	}

	/** Calculates the magnitude of the difference between two values. Args: a, b */
	public static double leftover(double a, double b) {
		double val;
		if (a > b)
			val = a - b;
		else
			val = b - a;
		return val;
	}

	/** Returns the logarithm of a specified base. Args: input, logbase */
	public static double logbase(double inp, double base) {
		double val = Math.log(inp);
		return val/(Math.log(base));
	}

	/** Returns the abs-max, abs-min, signed max, or signed min of the arguments,
	 * as specified. Args: a, b, operation. Operations: "min", "absmin", "max",
	 * "absmax". All other inputs will result in the method returning -987654321 */
	public static int extrema(int a, int b, String control) {
		if ("min".equals(control)) {
			if (a > b)
				return b;
			else
				return a;
		}
		if ("absmin".equals(control)) {
			if (a < 0)
				a *= -1;
			if (b < 0)
				b *= -1;
			if (a > b)
				return b;
			else
				return a;
		}
		if ("max".equals(control)) {
			if (a > b)
				return a;
			else
				return b;
		}
		if ("absmax".equals(control)) {
			if (a < 0)
				a *= -1;
			if (b < 0)
				b *= -1;
			if (a > b)
				return a;
			else
				return b;
		}
		return -987654321; // Seriously doubt this will happen
	}

	/** A double-IO version of extrema. */
	public static double extremad(double a, double b, String control) {
		if (control == "min") {
			if (a > b)
				return b;
			else
				return a;
		}
		if (control == "absmin") {
			if (a < 0)
				a *= -1;
			if (b < 0)
				b *= -1;
			if (a > b)
				return b;
			else
				return a;
		}
		if (control == "max") {
			if (a > b)
				return a;
			else
				return b;
		}
		if (control == "absmax") {
			if (a < 0)
				a *= -1;
			if (b < 0)
				b *= -1;
			if (a > b)
				return a;
			else
				return b;
		}
		return -987654321; // Seriously doubt this will happen
	}

	/** Returns the nearest higher power of 2. Args: input */
	public static int ceil2exp(int val) {
		val--;
		val = (val >> 1) | val;
		val = (val >> 2) | val;
		val = (val >> 4) | val;
		val = (val >> 8) | val;
		val = (val >> 16) | val;
		val++;
		return val;
	}

	/** Returns whether the two numbers are the same sign.
	 * Will return true if both are zero. Args: Input 1, Input 2*/
	public static boolean isSameSign(double val1, double val2) {
		if (val1 == 0 || val2 == 0)
			return true;
		if ((1000*val1)/val2 > 0)
			return true;
		return false;
	}

	/** Splits a number of items into an array; index 0 is number of stacks, index 1 is leftover
	 * Args: Number items, MaxStack size */
	public static int[] splitStacks(int number, int size) {
		int[] stacks = new int[2];
		if (number == 0) {
			stacks[0] = 0;
			stacks[1] = 0;
			return stacks;
		}
		while (number >= size) {
			stacks[0]++;
			number -= size;
		}
		stacks[1] = number;
		return stacks;
	}

	/** Returns the next multiple of a higher than b. Args: a, b */
	public static int nextMultiple(int a, int b) {
		while (b%a != 0) {
			b++;
		}
		return b;
	}

	/** Returns true if a number is a power of another Args: Number, Base */
	public static boolean isPowerOf(int num, int base) {
		return (logbase(num, base) == (int)logbase(num, base));
	}

	/** Returns a multiplier (<1) based on how close the value is to the peak value of a
	 * power distribution. (y = ax^n). Args: Peak x, peak y, required x, falloff factor, power */
	public static double powerFalloff(double peakx, double peaky, double pos, double factor, double power) {
		double distance = pos-peakx;
		if (distance < 0)
			distance = -distance;
		distance = ReikaMathLibrary.doubpow(distance, power);
		double reduction = factor*distance;
		return (peaky-reduction);
	}

	/** Returns a multiplier (<1) based on how close the value is to the peak value of an
	 * exponential distribution (y = a*n^x). Args: Peak x, peak y, required x, falloff factor, base */
	public static double expFalloff(double peakx, double peaky, double pos, double factor, double base) {
		double distance = pos-peakx;
		if (distance < 0)
			distance = -distance;
		double reduction = factor*ReikaMathLibrary.doubpow(base, distance);
		return (peaky-reduction);
	}

	/** Returns true if the value is not inside the bounds (inclusive). Args: Low Bound, Upper Bound, Value */
	public static boolean isValueOutsideBounds(int low, int hi, int val) {
		if (val >= low && val <= hi)
			return false;
		return true;
	}

	/** Returns true if the value is inside the bounds (not inclusive). Args: Low Bound, Upper Bound, Value */
	public static boolean isValueInsideBounds(int low, int hi, int val) {
		return (val < hi && val > low);
	}

	/** Returns true if the value is inside the bounds (inclusive). Args: Low Bound, Upper Bound, Value */
	public static boolean isValueInsideBoundsIncl(int low, int hi, int val) {
		return (val <= hi && val >= low);
	}

	public static boolean isValueInsideBoundsIncl(double low, double hi, double val) {
		return (val <= hi && val >= low);
	}

	/** Returns a double expressed in scientific notation (ret[0] x 10^ret[1]). Args: Value */
	public static double[] getScientificNotation(double val) {
		int pow = 0;
		while (val >= 1000) {
			pow += 3;
			val/= 1000D;
		}
		return new double[]{val, pow};
	}

	/** Returns a double's value when reduced until it is less than a thousand.
	 * Equivalent to getScientificNotation[0]. Args: Value */
	public static double getThousandBase(double val) {
		boolean neg = val < 0;
		val = Math.abs(val);
		while (val >= 1000) {
			val /= 1000D;
		}
		while (val < 1 && val > 0) {
			val *= 1000D;
		}
		return neg ? -val : val;
	}

	/** Returns the factorial of a positive integer. Take care with this, given
	 * how rapidly that function's output rises. */
	public static int factorial(int val) {
		int base = 1;
		for (int i = val; i > 0; i--) {
			base *= i;
		}
		return base;
	}

	/** Simple test to see if two number ranges overlap. Args: range 1 min/max; range 2 min/max */
	public static boolean doRangesOverLap(int min1, int max1, int min2, int max2) {
		return max2 >= min1 && min2 <= max1;
	}

	/** Returns true if and only if n and only n of the args are true. */
	public static boolean nBoolsAreTrue(int number, boolean... args) {
		int count = 0;
		for (int i = 0; i < args.length; i++) {
			if (args[i])
				count++;
		}
		return count == number;
	}

	/** Returns true if n or more of the args are true. */
	public static boolean nPlusBoolsAreTrue(int number, boolean... args) {
		int count = 0;
		for (int i = 0; i < args.length; i++) {
			if (args[i])
				count++;
		}
		return count >= number;
	}

	/** Returns +1 or -1 depending on the sign of the argument. */
	public static int sign(int val) {
		return val/Math.abs(val);
	}

	/** Returns +1 or -1 depending on the sign of the argument. */
	public static double sign(double val) {
		return val/Math.abs(val);
	}
}

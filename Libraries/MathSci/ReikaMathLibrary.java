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

import java.awt.Polygon;

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
		return Math.sqrt(val);
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

	/** Returns true if the input is within a percentage of its size of another value.
	 * Args: Input, target, percent tolerance */
	public static boolean approxpAbs(double input, double target, double percent) {
		return approxp(Math.abs(input), Math.abs(target), percent);
	}

	/** Returns true if the input is within [target-range,target+range]. Args: input, target, range */
	public static boolean approxrAbs(double input, double target, double range) {
		return approxr(Math.abs(input), Math.abs(target), range);
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

	/** Returns the long value of an integer raised to a POSITIVE integer power. Args: Base, power */
	public static long longpow(int base, int pow) {
		long val = 1;
		for (int i = 0; i < pow; i++) {
			val *= base;
		}
		return val;
	}

	/** Returns the value of a double raised to an decimal power. Args: Base, power */
	public static double doubpow(double base, double pow) {
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

	public static int logbase2(long inp) {
		return inp > 0 ? 63-Long.numberOfLeadingZeros(inp) : 0;
	}

	public static double logbase(long inp, int base) {
		//if (base == 2 && isPowerOfTwo((int)inp))
		//	return logbase2(inp);
		return logbase((double)inp, base);
	}

	/** Returns the logarithm of a specified base. Args: input, logbase */
	public static double logbase(double inp, double base) {
		return Math.log(inp)/(Math.log(base));
	}

	public static boolean isPositiveInteger(double num) {
		return num > 0 && (int)num == num;
	}

	public static boolean isInteger(double num) {
		return MathHelper.floor_double(num) == num;
	}

	public static boolean isPowerOfTwo(long num) {
		return num > 0 && (num & (num-1)) == 0; //alternate: num > 0 && (num & -num) == num
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
		if (val <= 0)
			return 0;
		val--;
		val = (val >> 1) | val;
		val = (val >> 2) | val;
		val = (val >> 4) | val;
		val = (val >> 8) | val;
		val = (val >> 16) | val;
		val++;
		return val;
	}

	/** Returns the nearest higher power of 2 or "sort of" power of 2 (=1.5*prev power of 2, so eg 192 between 128 and 256) */
	public static int ceilPseudo2Exp(int val) {
		int pow = ceil2exp(val);
		int prev = intpow2(2, logbase2(pow)-1);
		int mid = prev*3/2;
		if (prev >= val)
			return prev;
		else if (mid >= val)
			return mid;
		else
			return pow;
	}

	/** Returns whether the two numbers are the same sign.
	 * Will return true if both are zero. Args: Input 1, Input 2*/
	public static boolean isSameSign(double val1, double val2) {
		return Math.signum(val1) == Math.signum(val2);
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
		return logbase(num, base) == (int)logbase(num, base);
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
		return !(val >= low && val <= hi);
	}

	/** Returns true if the value is inside the bounds (not inclusive). Args: Low Bound, Upper Bound, Value */
	public static boolean isValueInsideBounds(int low, int hi, int val) {
		return val < hi && val > low;
	}

	/** Returns true if the value is inside the bounds (inclusive). Args: Low Bound, Upper Bound, Value */
	public static boolean isValueInsideBoundsIncl(int low, int hi, int val) {
		return val <= hi && val >= low;
	}

	public static boolean isValueInsideBounds(double low, double hi, double val) {
		return val < hi && val > low;
	}

	public static boolean isValueInsideBoundsIncl(double low, double hi, double val) {
		return val <= hi && val >= low;
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
		if (Math.abs(val) == Double.POSITIVE_INFINITY)
			return val;
		if (val == Double.NaN)
			return val;
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

	public static int roundDownToX(int multiple, int val) {
		int ret = val - val%multiple;
		if (val < 0)
			ret -= multiple;
		return ret;
	}

	public static int roundUpToX(int multiple, int val) {
		return roundDownToX(multiple, val)+multiple;
	}

	public static int roundToNearestX(int multiple, int val) {
		return ((val+multiple/2)/multiple)*multiple;
	}

	public static double[] splitNumberByDigits(long num, int base) {
		int len = 1+(int)logbase(num, base);
		double[] arr = new double[len];
		for (int i = 0; i < len; i++) {
			long val = num%base;
			arr[i] = val/(double)base;
			num /= base;
		}
		return arr;
	}

	public static int multiMin(int... vals) {
		int min = vals[0];
		for (int i = 1; i < vals.length; i++) {
			min = Math.min(min, vals[i]);
		}
		return min;
	}

	public static int multiMax(int... vals) {
		int max = vals[0];
		for (int i = 1; i < vals.length; i++) {
			max = Math.max(max, vals[i]);
		}
		return max;
	}

	public static float getDecimalPart(float f) {
		return f-(int)f;
	}

	public static double getDecimalPart(double d) {
		return d-(int)d;
	}

	public static int addAndRollover(int a, int b, int min, int max) {
		int sum = a+b;
		int over = sum-max;
		int under = min-sum;
		int range = max-min;
		while (over > 0) {
			sum = Math.min(max, min+over);
			over -= range;
		}
		while (under > 0) {
			sum = Math.max(min, max-under);
			under -= range;
		}
		return sum;
	}

	public static boolean isPointInsideEllipse(double x, double y, double z, double ra, double rb, double rc) {
		return (ra > 0 ? ((x*x)/(ra*ra)) : 0) + (rb > 0 ? ((y*y)/(rb*rb)) : 0) + (rc > 0 ? ((z*z)/(rc*rc)) : 0) <= 1;
	}

	public static boolean isPointInsidePowerEllipse(double x, double y, double z, double rx, double ry, double rz, double pow) {
		x = Math.abs(x);
		y = Math.abs(y);
		z = Math.abs(z);
		return (rx > 0 ? (Math.pow(x, pow)/Math.pow(rx, pow)) : 0) + (ry > 0 ? (Math.pow(y, pow)/Math.pow(ry, pow)) : 0) + (rz > 0 ? (Math.pow(z, pow)/Math.pow(rz, pow)) : 0) <= 1;
	}

	public static double linterpolate(double x, double x1, double x2, double y1, double y2) {
		return y1+(x-x1)/(x2-x1)*(y2-y1);
	}

	public static int bitRound(int val, int bits) {
		return (val >> bits) << bits;
	}

	public static double cosInterpolation(double min, double max, double val) {
		if (!isValueInsideBoundsIncl(min, max, val))
			return 0;
		double size = (max-min)/2D;
		double mid = min+size;
		if (val == mid) {
			return 1;
		}
		else {
			return 0.5+0.5*Math.cos(Math.toRadians((val-mid)/size*180));
		}
	}

	public static double cosInterpolation(double min, double max, double val, double y1, double y2) {
		return y1+(y2-y1)*cosInterpolation(min, max, val);
	}

	public static int toggleBit(int num, int bit) {
		return num ^ (1 << bit);
	}

	public static int getNBitflags(int length) {
		return intpow2(2, length)-1;
	}

	public static boolean isPerfectSquare(int val) {
		double sqrt = Math.sqrt(val);
		return sqrt == (int)sqrt;
	}

	/** Assumes val ranges from [-1 to +1] */
	public static double normalizeToBounds(double val, double min, double max) {
		return normalizeToBounds(val, min, max, -1, 1);
	}

	public static double normalizeToBounds(double val, double min, double max, double low, double high) {
		return min+((max-min)*(val-low)/(high-low));
	}

	public static float roundToDecimalPlaces(float f, int i) {
		float pow = (float)Math.pow(10, i);
		return Math.round(f*pow)/pow;
	}

	public static double roundToNearestFraction(double val, double frac) {
		double fac = 1D/frac;
		return Math.round(val*fac)/fac;
	}

	public static int getWithinBoundsElse(int val, int min, int max, int fall) {
		return isValueInsideBoundsIncl(min, max, val) ? val : fall;
	}

	public static int cycleBitsLeft(int num, int n) {
		n = n&31;
		return (num << n) | (num >> (32-n));
	}

	public static long cycleBitsLeft(long num, int n) {
		n = n&63;
		return (num << n) | (num >> (64-n));
	}

	public static int cycleBitsRight(int num, int n) {
		n = n&31;
		return (num >> n) | (num << (32-n));
	}

	public static long cycleBitsRight(long num, int n) {
		n = n&63;
		return (num >> n) | (num << (64-n));
	}

	public static boolean isPointInsidePolygon(int x, int z, int n, int r) {
		if (x*x+z*z > r*r)
			return false;
		double da = 360D/(2*n);
		Polygon p = new Polygon();
		for (int i = 0; i < n; i++) {
			double a = Math.toRadians(da+i*360D/n);
			double dx = r*Math.cos(a);
			double dz = r*Math.sin(a);
			p.addPoint((int)Math.round(dx), (int)Math.round(dz));
		}
		return p.contains(x, z);
	}

	public static double getUnequalAverage(double a, double b, double bias) {
		return (1-bias)*a+bias*b;
	}

	public static int clipLeadingHexBits(int val) {
		while (val > 0 && (val&0xF) == 0)
			val = val >> 4;
		return val;
	}

	public static double ellipticalInterpolation(double x, double x1, double x2, double y1, double y2) {
		return (y2-y1)*Math.sqrt(Math.pow(x2-x1, 2)-Math.pow(x-x1, 2))/(x2-x1);
	}

	public static double powerInterpolation(double x, double x1, double x2, double y1, double y2, double power) {
		return (y2-y1)*Math.pow(Math.pow(x2-x1, power)-Math.pow(x-x1, power), 1D/power)/(x2-x1);
	}

	public static long cantorCombine(long... vals) {
		long ret = cantorCombine(vals[0], vals[1]);
		for (int i = 2; i < vals.length; i++) {
			ret = cantorCombine(ret, vals[i]);
		}
		return ret;
	}

	public static long cantorCombine(long a, long b) {
		long k1 = a*2;
		long k2 = b*2;
		if (a < 0)
			k1 = a*-2-1;
		if (b < 0)
			k2 = b*-2-1;
		return (long)(0.5*(k1 + k2)*(k1 + k2 + 1) + k2);
	}
}

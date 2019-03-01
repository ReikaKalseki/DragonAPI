/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkCoordIntPair;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public final class ReikaArrayHelper extends DragonAPICore {

	public static boolean containsTrue(boolean[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i])
				return true;
		}
		return false;
	}

	//TODO Condense functions into accept-all-primitive designs
	/** Returns the sum of all values in an array. Args: Array */
	public static int sumArray(int[] arr) {
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum;
	}

	/** Returns the sum of all values in an array. Args: Array */
	public static double sumArray(double[] arr) {
		double sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum;
	}

	/** Returns the sum of all values in an array. Args: Array */
	public static float sumArray(float[] arr) {
		float sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum;
	}

	/** Returns the product of all values in an array. Args: Array */
	public static int productArray(int[] arr) {
		int product = 1;
		for (int i = 0; i < arr.length; i++) {
			product *= arr[i];
		}
		return product;
	}

	/** Returns the product of all values in an array. Args: Array */
	public static double productArray(double[] arr) {
		double product = 1;
		for (int i = 0; i < arr.length; i++) {
			product *= arr[i];
		}
		return product;
	}

	/** Returns the product of all values in an array. Args: Array */
	public static float productArray(float[] arr) {
		float product = 1;
		for (int i = 0; i < arr.length; i++) {
			product *= arr[i];
		}
		return product;
	}

	/** Fills an array with the specified value and returns the array.
	 * Args: array, value */
	public static double[] fillArray(double[] arr, double val) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = val;
		return arr;
	}

	/** Fills an array with the specified value and returns the array.
	 * Args: array, value */
	public static int[] fillArray(int[] arr, int val) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = val;
		return arr;
	}

	/** Fills an array with the specified value and returns the array.
	 * Args: array, value */
	public static boolean[] fillArray(boolean[] arr, boolean val) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = val;
		return arr;
	}

	/** Fills an array with the specified value and returns the array.
	 * Args: array, value */
	public static String[] fillArray(String[] arr, String val) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = val;
		return arr;
	}

	/** Fills an array with the specified value and returns the array.
	 * Args: array, value */
	public static ItemStack[] fillArray(ItemStack[] arr, ItemStack val) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = val;
		return arr;
	}

	/** Fills a matrix with the specified value and returns the array.
	 * Args: Array, value */
	public static int[][] fillMatrix(int[][] mat, int val) {
		for (int i = 0; i < mat.length; i++)
			mat[i] = ReikaArrayHelper.fillArray(mat[i], val);
		return mat;
	}

	/** Fills a matrix with the specified value and returns the array.
	 * Args: Array, value */
	public static boolean[][] fillMatrix(boolean[][] mat, boolean val) {
		for (int i = 0; i < mat.length; i++)
			mat[i] = ReikaArrayHelper.fillArray(mat[i], val);
		return mat;
	}

	/** Fills a matrix with the specified value and returns the array.
	 * Args: Array, value */
	public static double[][] fillMatrix(double[][] mat, double val) {
		for (int i = 0; i < mat.length; i++)
			mat[i] = ReikaArrayHelper.fillArray(mat[i], val);
		return mat;
	}

	/** Fills a matrix with the specified value and returns the array.
	 * Args: Array, value */
	public static String[][] fillMatrix(String[][] mat, String val) {
		for (int i = 0; i < mat.length; i++)
			mat[i] = ReikaArrayHelper.fillArray(mat[i], val);
		return mat;
	}

	/** Fills a matrix with the specified value and returns the array.
	 * Args: Array, value */
	public static ItemStack[][] fillMatrix(ItemStack[][] mat, ItemStack val) {
		for (int i = 0; i < mat.length; i++)
			mat[i] = ReikaArrayHelper.fillArray(mat[i], val);
		return mat;
	}

	/** Rotates a square matrix 90 degrees clockwise and returns it. Args: Matrix */
	public static int[][] rotateMatrix(int[][] mat) {
		int[][] temp = mat; //Ensures size match
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				temp[i][j] = mat[mat.length-j-1][i];
			}
		}
		return temp;
	}

	/** Rotates a square matrix 90 degrees counterclockwise and returns it. Args: Matrix */
	public static int[][] rotateMatrixM90(int[][] mat) {
		int[][] temp = transposeMatrix(mat);
		temp = reverseColumns(temp);
		return temp;
	}

	public static int[][] reverseColumns(int[][] mat) {
		int[][] temp = mat;
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				temp[i][mat.length-1-j] = mat[i][j];
			}
		}
		return temp;
	}

	/** Rotates a square matrix 90 degrees clockwise and returns it. Args: Matrix */
	public static boolean[][] rotateMatrix(boolean[][] mat) {
		boolean[][] temp = mat; //Ensures size match
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				temp[i][j] = mat[mat.length-j-1][i];
			}
		}
		return temp;
	}

	/** Transposes a 2D matrix and returns it. Args: Matrix */
	public static int[][] transposeMatrix(int[][] mat) {
		int[][] arr = mat;
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				arr[i][j] = mat[j][i];
			}
		}
		return arr;
	}

	/** Transposes a 2D matrix and returns it. Args: Matrix */
	public static boolean[][] transposeMatrix(boolean[][] mat) {
		boolean[][] arr = mat;
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				arr[i][j] = mat[j][i];
			}
		}
		return arr;
	}

	/** Rotates a square matrix 90 degrees clockwise and returns it. Args: Matrix */
	public static double[][] rotateMatrix(double[][] mat) {
		double[][] temp = mat; //Ensures size match
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				temp[i][j] = mat[mat.length-j-1][i];
			}
		}
		return temp;
	}

	/** Transposes a 2D matrix and returns it. Args: Matrix */
	public static double[][] transposeMatrix(double[][] mat) {
		double[][] arr = mat;
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				arr[i][j] = mat[j][i];
			}
		}
		return arr;
	}

	/** Rotates a square matrix 90 degrees clockwise and returns it. Args: Matrix */
	public static String[][] rotateMatrix(String[][] mat) {
		String[][] temp = mat; //Ensures size match
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				temp[i][j] = mat[mat.length-j-1][i];
			}
		}
		return temp;
	}

	/** Transposes a 2D matrix and returns it. Args: Matrix */
	public static String[][] transposeMatrix(String[][] mat) {
		String[][] arr = mat;
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				arr[i][j] = mat[j][i];
			}
		}
		return arr;
	}

	/** Rotates a square matrix 90 degrees clockwise and returns it. Args: Matrix */
	public static ItemStack[][] rotateMatrix(ItemStack[][] mat) {
		ItemStack[][] temp = mat; //Ensures size match
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				temp[i][j] = mat[mat.length-j-1][i];
			}
		}
		return temp;
	}

	/** Transposes a 2D matrix and returns it. Args: Matrix */
	public static ItemStack[][] transposeMatrix(ItemStack[][] mat) {
		ItemStack[][] arr = mat;
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				arr[i][j] = mat[j][i];
			}
		}
		return arr;
	}

	/** Returns true if all nonzero values in the array are equal. Args: Array */
	//TODO Make scale for all array sizes
	public static boolean allNonZerosEqual(long[] powers) {
		Arrays.sort(powers);
		if (powers[0] != 0) {
			if (powers[1] != 0)
				if (powers[0] != powers[1])
					return false;
		}
		if (powers[0] != 0) {
			if (powers[2] != 0)
				if (powers[0] != powers[2])
					return false;
		}
		if (powers[0] != 0) {
			if (powers[3] != 0)
				if (powers[0] != powers[3])
					return false;
		}
		if (powers[0] != 0) {
			if (powers[4] != 0)
				if (powers[0] != powers[4])
					return false;
		}
		if (powers[0] != 0) {
			if (powers[5] != 0)
				if (powers[0] != powers[5])
					return false;
		}
		if (powers[1] != 0) {
			if (powers[2] != 0)
				if (powers[1] != powers[2])
					return false;
		}
		if (powers[1] != 0) {
			if (powers[3] != 0)
				if (powers[1] != powers[3])
					return false;
		}
		if (powers[1] != 0) {
			if (powers[4] != 0)
				if (powers[1] != powers[4])
					return false;
		}
		if (powers[1] != 0) {
			if (powers[5] != 0)
				if (powers[1] != powers[5])
					return false;
		}
		if (powers[2] != 0) {
			if (powers[3] != 0)
				if (powers[2] != powers[3])
					return false;
		}
		if (powers[2] != 0) {
			if (powers[4] != 0)
				if (powers[2] != powers[4])
					return false;
		}
		if (powers[2] != 0) {
			if (powers[5] != 0)
				if (powers[2] != powers[5])
					return false;
		}
		if (powers[3] != 0) {
			if (powers[4] != 0)
				if (powers[3] != powers[4])
					return false;
		}
		if (powers[3] != 0) {
			if (powers[5] != 0)
				if (powers[3] != powers[5])
					return false;
		}
		if (powers[4] != 0) {
			if (powers[5] != 0)
				if (powers[4] != powers[5])
					return false;
		}
		return true;
	}

	/** Returns true if all values in the array are equal. Args: Array */
	public static boolean allEqual(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[0] != arr[i])
				return false;
		}
		return true;
	}

	public static void shuffleArray(int[] a) {
		int mid = a.length / 2;
		for (int i = mid; i < a.length; i++) {
			int lo = rand.nextInt(mid);
			int buffer = a[lo];
			a[lo] = a[i];
			a[i] = buffer;
		}
	}

	public static void shuffleArray(double[] a) {
		int mid = a.length / 2;
		for (int i = mid; i < a.length; i++) {
			int lo = rand.nextInt(mid);
			double buffer = a[lo];
			a[lo] = a[i];
			a[i] = buffer;
		}
	}

	public static void shuffleArray(Object[] a) {
		int mid = a.length / 2;
		for (int i = mid; i < a.length; i++) {
			int lo = rand.nextInt(mid);
			Object buffer = a[lo];
			a[lo] = a[i];
			a[i] = buffer;
		}
	}

	public static long sumArray(long[] arr) {
		long sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum;
	}

	public static boolean contains(int[] arr, int val) {
		for (int i = 0; i < arr.length; i++) {
			if (val == arr[i])
				return true;
		}
		return false;
	}

	public static boolean contains(Object[] arr, Object val) {
		for (int i = 0; i < arr.length; i++) {
			if (val.equals(arr[i]))
				return true;
		}
		return false;
	}

	public static int[] getLinearArray(int size) {
		int[] n = new int[size];
		for (int i = 0; i < n.length; i++)
			n[i] = i;
		return n;
	}

	public static int[] getLinearArrayExceptFor(int size, int... vals) {
		int[] n = new int[size-vals.length];
		for (int i = 0; i < n.length; i++) {
			while (contains(vals, i)) {
				i++;
			}
			n[i] = i;
		}
		return n;
	}

	public static int[] intListToArray(List<Integer> li) {
		int[] a = new int[li.size()];
		for (int i = 0; i < a.length; i++) {
			a[i] = li.get(i);
		}
		return a;
	}

	public static int[] getArrayOf(int val, int length) {
		int[] data = new int[length];
		for (int i = 0; i < length; i++) {
			data[i] = val;
		}
		return data;
	}

	public static boolean isAllTrue(boolean[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (!arr[i])
				return false;
		}
		return true;
	}

	public static boolean arrayContains(String[] arr, String sg, boolean ignoreCase) {
		for (int i = 0; i < arr.length; i++) {
			if (ignoreCase) {
				if (sg.equalsIgnoreCase(arr[i]))
					return true;
			}
			else {
				if (sg.equals(arr[i]))
					return true;
			}
		}
		return false;
	}

	public static int booleanToBitflags(boolean[] flags) {
		if (flags.length > 31)
			throw new IllegalArgumentException("You cannot store more than 31 bits on an int!");
		int n = 0;
		for (int i = 0; i < flags.length; i++) {
			if (flags[i])
				n += (1 << i);
		}
		return n;
	}

	public static boolean[] booleanFromBitflags(int bitflags, int len) {
		if (len > 31)
			throw new IllegalArgumentException("You cannot store more than 31 bits on an int!");
		boolean[] arr = new boolean[len];
		for (int i = 0; i < len; i++) {
			int n = (1 << i);
			boolean flag = (bitflags & n) != 0;
			arr[i] = flag;
		}
		return arr;
	}

	public static byte booleanToByteBitflags(boolean[] flags) {
		if (flags.length > 8)
			throw new IllegalArgumentException("You cannot store more than 8 bits on a byte!");
		byte n = 0;
		for (int i = 0; i < flags.length; i++) {
			if (flags[i])
				n += (1 << i);
		}
		return n;
	}

	public static boolean[] booleanFromByteBitflags(byte bitflags, int len) {
		if (len > 8)
			throw new IllegalArgumentException("You cannot store more than 8 bits on a byte!");
		boolean[] arr = new boolean[len];
		for (int i = 0; i < len; i++) {
			int n = (1 << i);
			boolean flag = (bitflags & n) != 0;
			arr[i] = flag;
		}
		return arr;
	}

	public static int getIndexOfLargest(int[] arr) {
		int idx = 0;
		for (int i = 1; i < arr.length; i++) {
			int n = arr[i];
			if (n > arr[idx])
				idx = i;
		}
		return idx;
	}

	public static boolean[] getTrueArray(int n) {
		boolean[] arr = new boolean[n];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = true;
		}
		return arr;
	}

	public static void cycleArray(int[] arr, int newVal) {
		for (int i = arr.length-1; i > 0; i--) {
			arr[i] = arr[i-1];
		}
		arr[0] = newVal;
	}

	public static void cycleArray(double[] arr, double newVal) {
		for (int i = arr.length-1; i > 0; i--) {
			arr[i] = arr[i-1];
		}
		arr[0] = newVal;
	}

	public static void cycleArray(float[] arr, float newVal) {
		for (int i = arr.length-1; i > 0; i--) {
			arr[i] = arr[i-1];
		}
		arr[0] = newVal;
	}

	public static void cycleArray(boolean[] arr, boolean newVal) {
		for (int i = arr.length-1; i > 0; i--) {
			arr[i] = arr[i-1];
		}
		arr[0] = newVal;
	}

	public static <A> void cycleArray(A[] arr, A newVal) {
		for (int i = arr.length-1; i > 0; i--) {
			arr[i] = arr[i-1];
		}
		arr[0] = newVal;
	}

	public static <A> void cycleArrayReverse(A[] arr, A newVal) {
		for (int i = 0; i < arr.length-1; i++) {
			arr[i] = arr[i+1];
		}
		arr[arr.length-1] = newVal;
	}

	public static <A> A[] getArrayOf(A val, int length) {
		A[] arr = (A[])Array.newInstance(val.getClass(), length);
		for (int i = 0; i < length; i++) {
			arr[i] = val;
		}
		return arr;
	}

	public static double[] averageArrays(double[] a1, double[] a2) {
		if (a1.length != a2.length)
			throw new MisuseException("You cannot average arrays of different lengths!");
		double[] ret = new double[a1.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (a1[i]+a2[i])/2D;
		}
		return ret;
	}

	public static double[] onesComplementArray(double[] arr) {
		double[] ret = new double[arr.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = 1-arr[i];
		}
		return ret;
	}

	public static boolean isSquare(Object[][] arr) {
		int l = arr.length;
		for (int i = 0; i < l; i++) {
			if (arr[i].length != l)
				return false;
		}
		return true;
	}

	public static double[][] splitSquareArray(double[] arr) {
		if (!ReikaMathLibrary.isPerfectSquare(arr.length))
			throw new MisuseException("You can only split square arrays!");
		int d = (int)Math.sqrt(arr.length);
		double[][] ret = new double[d][d];
		for (int i = 0; i < d; i++) {
			for (int k = 0; k < d; k++) {
				int idx = i*d+k;
				double val = arr[idx];
				ret[i][k] = val;
			}
		}
		return ret;
	}

	public static double getMinValue(double[] arr) {
		double val = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < val)
				val = arr[i];
		}
		return val;
	}

	public static double getMaxValue(double[] arr) {
		double val = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > val)
				val = arr[i];
		}
		return val;
	}

	public static int getMaxValue(int[] arr) {
		int val = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > val)
				val = arr[i];
		}
		return val;
	}

	public static Coordinate[] chunkCoordsToBlockCoords(ChunkCoordIntPair[] pos) {
		Coordinate[] ret = new Coordinate[pos.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = new Coordinate(pos[i].chunkXPos << 4, 0, pos[i].chunkZPos << 4);
		}
		return ret;
	}
}

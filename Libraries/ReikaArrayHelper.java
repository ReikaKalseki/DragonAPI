/*******************************************************************************
 * @author Reika
 * 
 * This code is the property of and owned and copyrighted by Reika.
 * Unless given explicit written permission - electronic writing is acceptable - no user may
 * copy, edit, or redistribute this source code nor any derivative works.
 * Failure to comply with these restrictions is a violation of
 * copyright law and will be dealt with accordingly.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.DragonAPICore;

public final class ReikaArrayHelper extends DragonAPICore {

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
	public static boolean allNonZerosEqual(int[] arr) {
		Arrays.sort(arr);
    	if (arr[0] != 0) {
    		if (arr[1] != 0)
    			if (arr[0] != arr[1])
    				return false;
    	}
    	if (arr[0] != 0) {
    		if (arr[2] != 0)
    			if (arr[0] != arr[2])
    				return false;
    	}
    	if (arr[0] != 0) {
    		if (arr[3] != 0)
    			if (arr[0] != arr[3])
    				return false;
    	}
    	if (arr[0] != 0) {
    		if (arr[4] != 0)
    			if (arr[0] != arr[4])
    				return false;
    	}
    	if (arr[0] != 0) {
    		if (arr[5] != 0)
    			if (arr[0] != arr[5])
    				return false;
    	}
    	if (arr[1] != 0) {
    		if (arr[2] != 0)
    			if (arr[1] != arr[2])
    				return false;
    	}
    	if (arr[1] != 0) {
    		if (arr[3] != 0)
    			if (arr[1] != arr[3])
    				return false;
    	}
    	if (arr[1] != 0) {
    		if (arr[4] != 0)
    			if (arr[1] != arr[4])
    				return false;
    	}
    	if (arr[1] != 0) {
    		if (arr[5] != 0)
    			if (arr[1] != arr[5])
    				return false;
    	}
    	if (arr[2] != 0) {
    		if (arr[3] != 0)
    			if (arr[2] != arr[3])
    				return false;
    	}
    	if (arr[2] != 0) {
    		if (arr[4] != 0)
    			if (arr[2] != arr[4])
    				return false;
    	}
    	if (arr[2] != 0) {
    		if (arr[5] != 0)
    			if (arr[2] != arr[5])
    				return false;
    	}
    	if (arr[3] != 0) {
    		if (arr[4] != 0)
    			if (arr[3] != arr[4])
    				return false;
    	}
    	if (arr[3] != 0) {
    		if (arr[5] != 0)
    			if (arr[3] != arr[5])
    				return false;
    	}
    	if (arr[4] != 0) {
    		if (arr[5] != 0)
    			if (arr[4] != arr[5])
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
}

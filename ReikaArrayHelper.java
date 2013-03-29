package Reika.DragonAPI;

import net.minecraft.item.ItemStack;

public class ReikaArrayHelper {
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
}

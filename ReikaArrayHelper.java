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
}

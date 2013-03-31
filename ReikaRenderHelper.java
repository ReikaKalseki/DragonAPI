package Reika.DragonAPI;

import net.minecraft.world.World;


public class ReikaRenderHelper {
	
	/** Converts an RGB array into a color multiplier. Args: RGB[], bit */
	public static float RGBtoColorMultiplier(int[] RGB, int bit) {
		float color = 1F;
		if (bit < 0 || bit > 2)
			return 1F;
		color = RGB[bit]/255F;		
		return color;
	}
	
	/** Converts a hex color code to a color multiplier. Args: Hex, bit */
	public static float HextoColorMultiplier(int hex, int bit) {
		float color = 1F;
		int[] RGB = ReikaGuiAPI.HexToRGB(hex);
		if (bit < 0 || bit > 2)
			return 1F;
		color = RGB[bit]/255F;		
		return color;
	}
	
	/** Converts a biome to a color multiplier (for use in things like leaf textures).
	 * Args: World, x, z, material (grass, water, etc), bit */
	public static float biomeToColorMultiplier(World world, int x, int z, String mat, int bit) {
		int[] color = ReikaWorldHelper.biomeToRGB(world, x, z, mat);
		float mult = RGBtoColorMultiplier(color, bit);
		return mult;
	}
	
}

package Reika.DragonAPI;

import net.minecraft.world.World;


public class ReikaRenderHelper {
	
	public static float RGBtoColorMultiplier(int[] RGB, int bit) {
		float color = 1F;
		if (bit < 0 || bit > 2)
			return 1F;
		color = RGB[bit]/255F;		
		return color;
	}
	
	public static float HextoColorMultiplier(int hex, int bit) {
		float color = 1F;
		int[] RGB = ReikaGuiAPI.HexToRGB(hex);
		if (bit < 0 || bit > 2)
			return 1F;
		color = RGB[bit]/255F;		
		return color;
	}
	
	public static float biomeToColorMultiplier(World world, int x, int z, String mat, int bit) {
		int[] color = ReikaWorldHelper.biomeToRGB(world, x, z, mat);
		float mult = RGBtoColorMultiplier(color, bit);
		return mult;
	}
	
}

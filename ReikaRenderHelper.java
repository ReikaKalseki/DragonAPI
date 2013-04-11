package Reika.DragonAPI;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.ModLoader;
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
	
	/** Renders a flat circle in the world. Args: radius, center x,y,z, RGB*/
	public static void renderCircle(double r, double x, double y, double z, int[] color) {
        ModLoader.getMinecraftInstance().entityRenderer.disableLightmap(1);
    	GL11.glDisable(GL11.GL_LIGHTING);
    	GL11.glEnable(GL11.GL_BLEND);
    	GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    	GL11.glColor4f(1F, 1F, 1F, 1F);
		Tessellator var5 = new Tessellator();
		var5.startDrawing(GL11.GL_LINE_LOOP);
    	var5.setColorRGBA(color[0], color[1], color[2], 255);
    	for (int i = 0; i < 360; i++) {
    		var5.addVertex(x+r*Math.cos(ReikaPhysicsHelper.degToRad(i)), y, z+r*Math.sin(ReikaPhysicsHelper.degToRad(i)));
    	}
    	var5.draw();
    	ModLoader.getMinecraftInstance().entityRenderer.enableLightmap(1);
    	GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    	GL11.glEnable(GL11.GL_LIGHTING);
    	GL11.glEnable(GL11.GL_CULL_FACE);
    	GL11.glDisable(GL11.GL_BLEND); 
    	GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	/** Renders a vertical-plane circle in the world. Args: radius, center x,y,z, RGB, phi */
	public static void renderVCircle(double r, double x, double y, double z, int[] color, double phi) {
        ModLoader.getMinecraftInstance().entityRenderer.disableLightmap(1);
    	GL11.glDisable(GL11.GL_LIGHTING);
    	GL11.glEnable(GL11.GL_BLEND);
    	GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    	GL11.glColor4f(1F, 1F, 1F, 1F);
		Tessellator var5 = new Tessellator();
    	var5.setColorRGBA(color[0], color[1], color[2], 255);
    	for (int k = 0; k < 360; k += 15) {
        	var5.startDrawing(GL11.GL_LINE_LOOP);
        	var5.setColorRGBA(color[0], color[1], color[2], 255);
	    	for (int i = 0; i < 360; i++) {
	    		int sign = 1;
	    		double h = r*Math.cos(ReikaPhysicsHelper.degToRad(i));
	    		if (i >= 180)
	    			sign = -1;
	    		var5.addVertex(x-Math.sin(phi)*(sign)*(Math.sqrt(r*r-h*h)), y+r*Math.cos(ReikaPhysicsHelper.degToRad(i)), z+r*Math.sin(ReikaPhysicsHelper.degToRad(i))*Math.cos(phi));
	    	}
	    	var5.draw();
    	}
    	ModLoader.getMinecraftInstance().entityRenderer.enableLightmap(1);
    	GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    	GL11.glEnable(GL11.GL_LIGHTING);
    	GL11.glEnable(GL11.GL_CULL_FACE);
    	GL11.glDisable(GL11.GL_BLEND); 
    	GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
}

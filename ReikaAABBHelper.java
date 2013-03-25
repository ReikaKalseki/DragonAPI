package Reika.DragonAPI;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.ModLoader;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class ReikaAABBHelper {

	/** Renders an AABB bounding box in the world. Very useful for debug purposes, or as a user-friendliness feature.
	 * Args: World, AABB, Render par2,4,6, x,y,z of machine, root alpha value */
	public static void renderAABB(World world, AxisAlignedBB box, double par2, double par4, double par6, int x, int y, int z, int a) {
		int[] color = {255, 255, 255, a};
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4 + 2.0F, (float)par6 + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glPopMatrix(); 
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    	GL11.glDisable(GL11.GL_LIGHTING);
    	GL11.glEnable(GL11.GL_BLEND);
    	GL11.glDisable(GL11.GL_TEXTURE_2D);
    	if (color[3] > 255)
    		color[3] = 255;
    	boolean filled = false;
    	Tessellator var5 = new Tessellator();
		double xdiff = box.minX-x;
		double ydiff = box.minY-y;
		double zdiff = box.minZ-z;
		double xdiff2 = box.maxX-x;
		double ydiff2 = box.maxY-y;
		double zdiff2 = box.maxZ-z;
		
		double px = par2+xdiff;
		double py = par4+ydiff;
		double pz = par6+zdiff;
		double px2 = par2+xdiff2;
		double py2 = par4+ydiff2;
		double pz2 = par6+zdiff2;
    	if (filled)
    	{
	    	var5.startDrawing(GL11.GL_QUADS);
	    	//var5.setBrightness(255);
	    	var5.setColorRGBA(color[0], color[1], color[2], (int)(color[3]*0.375F));
			
	    	var5.addVertex(px, py, pz);
	    	var5.addVertex(px2, py, pz);
	    	var5.addVertex(px2, py, pz2);
	    	var5.addVertex(px, py, pz2);
	    	
	    	var5.addVertex(px2, py, pz);
	    	var5.addVertex(px2, py2, pz);
	    	var5.addVertex(px2, py2, pz2);
	    	var5.addVertex(px2, py, pz2);
	    	
	    	var5.addVertex(px, py2, pz);
	    	var5.addVertex(px, py, pz);
	    	var5.addVertex(px, py, pz2);
	    	var5.addVertex(px, py2, pz2);
	    	
	    	var5.addVertex(px, py2, pz2);
	    	var5.addVertex(px, py, pz2);
	    	var5.addVertex(px2, py, pz2);
	    	var5.addVertex(px2, py2, pz2);
	    	
	    	var5.addVertex(px, py, pz);
	    	var5.addVertex(px, py2, pz);
	    	var5.addVertex(px2, py2, pz);
	    	var5.addVertex(px2, py, pz);
	    	
	    	var5.addVertex(px2, py2, pz);
	    	var5.addVertex(px, py2, pz);
	    	var5.addVertex(px, py2, pz2);
	    	var5.addVertex(px2, py2, pz2);
	    	var5.draw();
    	}
	    	
	    var5.startDrawing(GL11.GL_LINE_LOOP);
	    var5.setColorRGBA(color[0], color[1], color[2], color[3]);
	    var5.addVertex(px2, py2, pz);
	    var5.addVertex(px, py2, pz);
	    var5.addVertex(px, py2, pz2);
	    var5.addVertex(px2, py2, pz2);
	    var5.draw();
	    var5.startDrawing(GL11.GL_LINE_LOOP);
	    var5.setColorRGBA(color[0], color[1], color[2], color[3]);
	    var5.addVertex(px2, py, pz);
	    var5.addVertex(px, py, pz);
	    var5.addVertex(px, py, pz2);
	    var5.addVertex(px2, py, pz2);
	    var5.draw();
	    var5.startDrawing(GL11.GL_LINE_LOOP);
	    var5.setColorRGBA(color[0], color[1], color[2], color[3]);
	    var5.addVertex(px, py, pz);
	   	var5.addVertex(px, py2, pz);
	   	var5.draw();
	    var5.startDrawing(GL11.GL_LINE_LOOP);
	    var5.setColorRGBA(color[0], color[1], color[2], color[3]);
	   	var5.addVertex(px2, py, pz);
	   	var5.addVertex(px2, py2, pz);
	   	var5.draw();
	    var5.startDrawing(GL11.GL_LINE_LOOP);
	    var5.setColorRGBA(color[0], color[1], color[2], color[3]);
	    var5.addVertex(px2, py, pz2);
	    var5.addVertex(px2, py2, pz2);
	    var5.draw();
	    var5.startDrawing(GL11.GL_LINE_LOOP);
	    var5.setColorRGBA(color[0], color[1], color[2], color[3]);
	    var5.addVertex(px, py, pz2);
	    var5.addVertex(px, py2, pz2);
	    var5.draw();
	    	
    	GL11.glEnable(GL11.GL_LIGHTING);
    	GL11.glEnable(GL11.GL_CULL_FACE);
    	GL11.glDisable(GL11.GL_BLEND); 
    	GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
}

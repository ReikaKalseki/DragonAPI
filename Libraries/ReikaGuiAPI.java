/*******************************************************************************
 * @author Reika
 * 
 * This code is the property of and owned and copyrighted by Reika.
 * This code is provided under a modified visible-source license that is as follows:
 * 
 * Any and all users are permitted to use the source for educational purposes, or to create other mods that call
 * parts of this code and use DragonAPI as a dependency.
 * 
 * Unless given explicit written permission - electronic writing is acceptable - no user may redistribute this
 * source code nor any derivative works. These pre-approved works must prominently contain this copyright notice.
 * 
 * Additionally, no attempt may be made to achieve monetary gain from this code by anyone except the original author.
 * In the case of pre-approved derivative works, any monetary gains made will be shared between the original author
 * and the other developer(s), proportional to the ratio of derived to original code.
 * 
 * Finally, any and all displays, duplicates or derivatives of this code must be prominently marked as such, and must
 * contain attribution to the original author, including a link to the original source. Any attempts to claim credit
 * for this code will be treated as intentional theft.
 * 
 * Due to the Mojang and Minecraft Mod Terms of Service and Licensing Restrictions, compiled versions of this code
 * must be provided for free. However, with the exception of pre-approved derivative works, only the original author
 * may distribute compiled binary versions of this code.
 * 
 * Failure to comply with these restrictions is a violation of copyright law and will be dealt with accordingly.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ReikaGuiAPI extends GuiScreen {
	private int xSize;
	private int ySize;
	protected FontRenderer fontRenderer;

	public static ReikaGuiAPI instance = new ReikaGuiAPI();

	private ReikaGuiAPI() {}

    /**
     * Renders the specified text to the screen, center-aligned.
     */
    public void drawCenteredStringNoShadow(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5)
    {
        par1FontRenderer.drawString(par2Str, par3 - par1FontRenderer.getStringWidth(par2Str) / 2, par4, par5);
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRectInvert(int x, int y, int u, int v, int width, int height, int ySize, int scale)
    {
    	y += ySize/2;
    	y -= scale;
    	v -= scale;
    	height = scale;

        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = new Tessellator();
		if (var9.isDrawing)
			var9.draw();
        var9.startDrawingQuads();
        var9.addVertexWithUV(x + 0, y + height, zLevel, (u + 0) * var7, (v + height) * var8);
        var9.addVertexWithUV(x + width, y + height, zLevel, (u + width) * var7, (v + height) * var8);
        var9.addVertexWithUV(x + width, y + 0, zLevel, (u + width) * var7, (v + 0) * var8);
        var9.addVertexWithUV(x + 0, y + 0, zLevel, (u + 0) * var7, (v + 0) * var8);
        var9.draw();
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color. Modified and simplified from the original
     * in that it automatically handles alpha channel (makes completely transparent full opaque) and changes 4-point method
     * to x-y-width-height. Args: x, y-topleft, width, height, color, alpha on/off
     */
    public void drawRect(int par1, int par2, int par3, int par4, int par5, boolean par6)
    {
        int var5;
        if (!par6) {
        	int color = par5;
        	par5 /= 1000000; // make alpha-only
        	par5 *= 1000000; // pad back to alpha bitspace
        	par5 = 0xff000000+(color-par5); //subtract original color alpha, then add FF
        }

        if (par1 < par3)
        {
            var5 = par1;
            par1 = par3;
            par3 = var5;
        }

        if (par2 < par4)
        {
            var5 = par2;
            par2 = par4;
            par4 = var5;
        }
        par3 += par1;
        par4 += par2;

        float var10 = (par5 >> 24 & 255) / 255.0F;
        float var6 = (par5 >> 16 & 255) / 255.0F;
        float var7 = (par5 >> 8 & 255) / 255.0F;
        float var8 = (par5 & 255) / 255.0F;
        Tessellator var9 = new Tessellator();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(var6, var7, var8, var10);
		if (var9.isDrawing)
			var9.draw();
        var9.startDrawingQuads();
        var9.addVertex(par1, par4, 0.0D);
        var9.addVertex(par3, par4, 0.0D);
        var9.addVertex(par3, par2, 0.0D);
        var9.addVertex(par1, par2, 0.0D);
        var9.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /** Draws a dotted line between two points. Args: start x,y, end x,y, thickness, color */
    public void dottedLine(int x, int y, int x2, int y2, int t, int color) {
    	if (x == x2 && y == y2)
    		return;
    	if (x != x2 && y == y2) {
    		for (int i = x; i < x2-t; i++) {
    			this.drawRect(i, y, i+t, y, color);
    		}
    	}
    	if (y != y2 && x == x2) {
    		for (int i = y; i < y2-t; i++) {
    			this.drawRect(x, i, x, i+t, color);
    		}
    	}
    	if (x != x2 && y != y2) {
    		int xdiff = x2-x;
    		int ydiff = y2-y;
    		double slope = (double)ydiff/(double)xdiff;
    		while (x < x2-t) {
    			this.drawRect(x, y, x+t, y+t, color);
    			x += xdiff;
    			y += xdiff*slope;
    		}
    	}
    }

    /** Draws a dashed line between two points. Args: start x,y, end x,y, thickness, color */
    public void dashedLine(int x, int y, int x2, int y2, int t, int color) {

    }

    /** Draws a line between two points. Args: Start x,y, end x,y, color */
    public void drawLine(int x, int y, int x2, int y2, int color) {
    	boolean light = GL11.glIsEnabled(GL11.GL_LIGHTING);
    	boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
    	boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
    	boolean tex = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);

    	float alpha = Color.decode(String.valueOf(color)).getAlpha();
    	float red = Color.decode(String.valueOf(color)).getRed();
    	float green = Color.decode(String.valueOf(color)).getGreen();
    	float blue = Color.decode(String.valueOf(color)).getBlue();
    	GL11.glDisable(GL11.GL_LIGHTING);
    	GL11.glDisable(GL11.GL_DEPTH_TEST);
    	GL11.glEnable(GL11.GL_BLEND);
    	GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glBegin(GL11.GL_LINE_LOOP);
    	GL11.glColor4f(red/255F, green/255F, blue/255F, alpha/255F);
    	GL11.glVertex2i(x, y);
    	GL11.glVertex2i(x2, y2);
    	GL11.glEnd();
    	if (light)
    		GL11.glEnable(GL11.GL_LIGHTING);
    	if (depth)
    		GL11.glEnable(GL11.GL_DEPTH_TEST);
    	if (blend)
    		GL11.glDisable(GL11.GL_BLEND);
    	if (tex)
    		GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void drawCircle(int x, int y, int radius, int color) {
    	boolean light = GL11.glIsEnabled(GL11.GL_LIGHTING);
    	boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
    	boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
    	boolean tex = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
    	float alpha = Color.decode(String.valueOf(color)).getAlpha();
    	float red = Color.decode(String.valueOf(color)).getRed();
    	float green = Color.decode(String.valueOf(color)).getGreen();
    	float blue = Color.decode(String.valueOf(color)).getBlue();
    	GL11.glDisable(GL11.GL_LIGHTING);
    	GL11.glDisable(GL11.GL_DEPTH_TEST);
    	GL11.glEnable(GL11.GL_BLEND);
    	GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glBegin(GL11.GL_LINE_LOOP);
      	GL11.glColor4f(red/255F, green/255F, blue/255F, alpha/255F);
    	for (int i = 0; i < 360; i++) {
    		GL11.glVertex2i(x+(int)(radius*Math.cos(ReikaPhysicsHelper.degToRad(i))), y+(int)(radius*Math.sin(ReikaPhysicsHelper.degToRad(i))));
    	}
    	GL11.glEnd();
    	if (light)
    		GL11.glEnable(GL11.GL_LIGHTING);
    	if (depth)
    		GL11.glEnable(GL11.GL_DEPTH_TEST);
    	if (blend)
    		GL11.glDisable(GL11.GL_BLEND);
    	if (tex)
    		GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Draws a "fill bar" (rectangle from bottom up).
     * Args: left x, top y, width, bottom y, color, height, maxheight, alpha on/off
     */
    public void fillBar(int par1, int par2, int par3, int par4, int par5, int par6, int par7, boolean par8)
    {
        int var5;
        if (!par8) {
        	int color = par5;
        	par5 /= 1000000; // make alpha-only
        	par5 *= 1000000; // pad back to alpha bitspace
        	par5 = 0xff000000+(color-par5); //subtract original color alpha, then add FF
        }

        par2 += (par7-par6);
        par3 += par1;

        if (par1 < par3)
        {
            var5 = par1;
            par1 = par3;
            par3 = var5;
        }

        if (par2 < par4)
        {
            var5 = par2;
            par2 = par4;
            par4 = var5;
        }

        float var10 = (par5 >> 24 & 255) / 255.0F;
        float var6 = (par5 >> 16 & 255) / 255.0F;
        float var7 = (par5 >> 8 & 255) / 255.0F;
        float var8 = (par5 & 255) / 255.0F;
        Tessellator var9 = new Tessellator();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(var6, var7, var8, var10);
		if (var9.isDrawing)
			var9.draw();
        var9.startDrawingQuads();
        var9.addVertex(par1, par4, 0.0D);
        var9.addVertex(par3, par4, 0.0D);
        var9.addVertex(par3, par2, 0.0D);
        var9.addVertex(par1, par2, 0.0D);
        var9.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static int RGBtoHex(int R, int G, int B) {
    	int color = (B | G << 8 | R << 16);
    	color += 0xff000000;
    	return color;
    }

    public static int RGBtoHex(int GS) {
    	if (GS == -1) //technical
    		return 0xffD47EFF;
    	int color = (GS | GS << 8 | GS << 16);
    	color += 0xff000000;
    	return color;
    }

    public static int[] HexToRGB (int hex) {
    	int[] color = new int[3];
    	color[0] = Color.decode(String.valueOf(hex)).getRed();
    	color[1] = Color.decode(String.valueOf(hex)).getGreen();
    	color[2] = Color.decode(String.valueOf(hex)).getBlue();
    	return color;
    }

    public static void renderFraction(FontRenderer fr, String num, String den, int x, int y, int color, boolean shadow, boolean center) {

    }

    public static void renderRoot(FontRenderer fr, String num, String root, int x, int y, int color, boolean shadow, boolean center) {

    }

    public static void renderPower(FontRenderer fr, String base, String pow, int x, int y, int color, boolean shadow, boolean center) {

    }

    /** Draw a crafting recipe in the GUI. Args: x in, y in; icon indexes of: top-left, top, top-right, left,
     * center, right, bottom-left, bottom, bottom right; x out, y out; icon index of output item, output number,
     * shapeless t/f, icon textures.
     * Input icon indexes names MUST be a size-9 array, and textures must be a size-10 array! */
    public void drawRecipe(FontRenderer f, int x, int y, int[] in, int x2, int y2, int out, int amount, boolean shapeless, String[] tex) {
    	Minecraft mc = Minecraft.getMinecraft();
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2 - 8;
        int rowsout, colsout;
    	int[] rowsin = new int[9];
    	int[] colsin = new int[9];
    	for (int ij = 0; ij < 9; ij++) {
    		rowsin[ij] = in[ij]/16;
    		colsin[ij] = in[ij]-16*rowsin[ij];
    	}
    	rowsout = out/16;
    	colsout = out-16*rowsout;
    	//ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("DFD324");

    	for (int ii = 0; ii < 3; ii++) {
    		for (int jj = 0; jj < 3; jj++) {
    			//ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("34342");
    			if (in[ii*3+jj] != -1) {
    		    	mc.renderEngine.bindTexture(tex[ii*3+jj]);
    				this.drawTexturedModalRect(x+j+18*jj, y+k+18*ii, 16*colsin[ii*3+jj], 16*rowsin[ii*3+jj], 16, 16);
    				//ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("DFD");
    			}
    		}
    	}
    	mc.renderEngine.bindTexture(tex[9]);
    	this.drawTexturedModalRect(x2+4+j, y2+4+k, colsout*16, rowsout*16, 16, 16);
    	int xdp = 0;
    	if (amount < 10)
    		xdp = 3;
    	if (amount > 1)
    		this.drawString(f, String.format("%3d", amount), x2+j+6+xdp, y2+k+16, 0xffffff);
    	if (shapeless)
    		f.drawString("Shapeless", x2+j-35, y2+k+27, 0x000000);
    }

    /** Draw a smelting recipe in the GUI. Args: x in, y in, input icon index, x out, y out, output icon index, amount */
    public void drawSmelting(FontRenderer f, int x, int y, int in, int x2, int y2, int out, int amount) {
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2 - 8;
    	int rowsin, rowsout, colsin, colsout;
    	rowsin = in/16;
    	colsin = in-16*rowsin;
    	rowsout = out/16;
    	colsout = out-16*rowsout;
    	if (in != -1)
    		this.drawTexturedModalRect(x+j, y+k, colsin*16, rowsin*16, 16, 16);
    	if (out != -1)
    		this.drawTexturedModalRect(x2+4+j, y2+4+k, colsout*16, rowsout*16, 16, 16);
    	if (amount > 1)
    		this.drawString(f, String.format("%3d", amount), x2+j+6, y2+k+16, 0xffffff);
    }

    /** Draw a compactor recipe in the GUI. Args: x in, y in, input icon index, x out, y out, output icon index, amount. */
    public void drawCompressor(FontRenderer f, int x, int y, int in, int x2, int y2, int out, int amount) {
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2 - 8;
    	int rowsin, rowsout, colsin, colsout;
    	rowsin = in/16;
    	colsin = in-16*rowsin;
    	rowsout = out/16;
    	colsout = out-16*rowsout;
    	if (in != -1) {
    		for (int ii = 0; ii < 4; ii++)
    			this.drawTexturedModalRect(x+j, y+k+ii*18, colsin*16, rowsin*16, 16, 16);
    	}
    	if (out != -1)
    		this.drawTexturedModalRect(x2+j, y2+k, colsout*16, rowsout*16, 16, 16);
    	if (amount > 1)
    		this.drawString(f, String.format("%3d", amount), x2+j+6, y2+k+16, 0xffffff);
    }

    /** Draw an extractor recipe in the GUI. Args: x in, y in; icon indexes of top row;
     * x out, y out; icon index of bottom row.
     * Icon indexes MUST be size-4 arrays! */
    public void drawExtractor(FontRenderer f, int x, int y, int[] in, int x2, int y2, int[] out, int amount) {
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2 - 8;
        int[] rowsout = new int[4];
        int[] colsout = new int[4];
    	int[] rowsin = new int[4];
    	int[] colsin = new int[4];
    	for (int ij = 0; ij < 4; ij++) {
    		rowsin[ij] = in[ij]/16;
    		colsin[ij] = in[ij]-16*rowsin[ij];
    	}
    	for (int ij = 0; ij < 4; ij++) {
    		rowsout[ij] = out[ij]/16;
    		colsout[ij] = out[ij]-16*rowsout[ij];
    	}
    	for (int ij = 0; ij < 4; ij++)
    		this.drawTexturedModalRect(x+j+36*ij, y+k, colsin[ij]*16, rowsin[ij]*16, 16, 16);
    	for (int ij = 0; ij < 4; ij++)
    		this.drawTexturedModalRect(x2+j+36*ij, y2+k, colsout[ij]*16, rowsout[ij]*16, 16, 16);

    	if (amount > 1)
    		this.drawString(f, String.format("%3d", amount), x2+j+6, y2+k+16, 0xffffff);

    }

    /** Draw a fermenter recipe in the GUI. Args: x,y of top input slot, icon indices of input slots,
     * x,y out, icon index of output, amount made. Icon index array must be size-3! */
    public void drawFermenter(FontRenderer f, int x, int y, int[] in, int x2, int y2, int out, int amount) {
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2 - 8;
    	int rowsout = out/16;
    	int colsout = out-16*rowsout;
    	int[] rowsin = new int[3];
    	int[] colsin = new int[3];
    	for (int ij = 0; ij < 3; ij++) {
    		rowsin[ij] = in[ij]/16;
    		colsin[ij] = in[ij]-16*rowsin[ij];
    	}
    	for (int ij = 0; ij < 3; ij++)
    		this.drawTexturedModalRect(x+j, y+18*ij+k, colsin[ij]*16, rowsin[ij]*16, 16, 16);
    	if (out != -1)
    		this.drawTexturedModalRect(x2+4+j, y2+4+k, colsout*16, rowsout*16, 16, 16);
    	if (amount > 1)
    		this.drawString(f, String.format("%3d", amount), x2+j+9, y2+k+16, 0xffffff);

    }
}

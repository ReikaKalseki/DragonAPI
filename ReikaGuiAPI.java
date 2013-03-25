package Reika.DragonAPI;

import java.awt.Color;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class ReikaGuiAPI extends GuiScreen {
	private int xSize;
	private int ySize;
	protected FontRenderer fontRenderer;
	
	public static ReikaGuiAPI instance = new ReikaGuiAPI();
	
	public ReikaGuiAPI(int width2, int height2) {
		this.width = width2;
		this.height = height2;
	}
	
	public ReikaGuiAPI() {
		
	}
	
	public ReikaGuiAPI(int xSize2, int ySize2, int width2, int height2) {
		this.xSize = xSize2;
		this.ySize = ySize2;
		this.width = width2;
		this.height = height2;
	}
	
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
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(x + 0), (double)(y + height), (double)this.zLevel, (double)((float)(u + 0) * var7), (double)((float)(v + height) * var8));
        var9.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel, (double)((float)(u + width) * var7), (double)((float)(v + height) * var8));
        var9.addVertexWithUV((double)(x + width), (double)(y + 0), (double)this.zLevel, (double)((float)(u + width) * var7), (double)((float)(v + 0) * var8));
        var9.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, (double)((float)(u + 0) * var7), (double)((float)(v + 0) * var8));
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

        float var10 = (float)(par5 >> 24 & 255) / 255.0F;
        float var6 = (float)(par5 >> 16 & 255) / 255.0F;
        float var7 = (float)(par5 >> 8 & 255) / 255.0F;
        float var8 = (float)(par5 & 255) / 255.0F;
        Tessellator var9 = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(var6, var7, var8, var10);
        var9.startDrawingQuads();
        var9.addVertex((double)par1, (double)par4, 0.0D);
        var9.addVertex((double)par3, (double)par4, 0.0D);
        var9.addVertex((double)par3, (double)par2, 0.0D);
        var9.addVertex((double)par1, (double)par2, 0.0D);
        var9.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
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

        float var10 = (float)(par5 >> 24 & 255) / 255.0F;
        float var6 = (float)(par5 >> 16 & 255) / 255.0F;
        float var7 = (float)(par5 >> 8 & 255) / 255.0F;
        float var8 = (float)(par5 & 255) / 255.0F;
        Tessellator var9 = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(var6, var7, var8, var10);
        var9.startDrawingQuads();
        var9.addVertex((double)par1, (double)par4, 0.0D);
        var9.addVertex((double)par3, (double)par4, 0.0D);
        var9.addVertex((double)par3, (double)par2, 0.0D);
        var9.addVertex((double)par1, (double)par2, 0.0D);
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
    
    /** Draw a crafting recipe in the GUI. Args: x in, y in; icon indexes of: top-left, top, top-right, left,
     * center, right, bottom-left, bottom, bottom right; x out, y out; icon index of output item. 
     * Input icon indexes MUST be a size-9 array! */
    public void drawRecipe(int x, int y, int[] in, int x2, int y2, int out, int amount, boolean shapeless) {
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
    				drawTexturedModalRect(x+j+18*jj, y+k+18*ii, 16*colsin[ii*3+jj], 16*rowsin[ii*3+jj], 16, 16);
    				//ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("DFD");
    			}
    		}
    	}
    	drawTexturedModalRect(x2+4+j, y2+4+k, colsout*16, rowsout*16, 16, 16);
    	if (amount > 1)
    		drawString(fontRenderer, String.format("%3d", amount), x2+j+6, y2+k+16, 0xffffff);
    	if (shapeless)
    		fontRenderer.drawString("Shapeless", x2+j-35, y2+k+27, 0x000000);
    }
    
    /** Draw a smelting recipe in the GUI. Args: x in, y in, input icon index, x out, y out, output icon index. */
    public void drawSmelting(int x, int y, int in, int x2, int y2, int out, int amount) {
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2 - 8;
    	int rowsin, rowsout, colsin, colsout;
    	rowsin = in/16;
    	colsin = in-16*rowsin;
    	rowsout = out/16;
    	colsout = out-16*rowsout;
    	if (in != -1)
    		drawTexturedModalRect(x+j, y+k, colsin*16, rowsin*16, 16, 16);
    	if (out != -1)
    		drawTexturedModalRect(x2+4+j, y2+4+k, colsout*16, rowsout*16, 16, 16);
    	if (amount > 1)
    		drawString(fontRenderer, String.format("%3d", amount), x2+j+6, y2+k+16, 0xffffff);
    }
    
    /** Draw a compactor recipe in the GUI. Args: x in, y in, input icon index, x out, y out, output icon index. */
    public void drawCompressor(int x, int y, int in, int x2, int y2, int out, int amount) {
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2 - 8;
    	int rowsin, rowsout, colsin, colsout;
    	rowsin = in/16;
    	colsin = in-16*rowsin;
    	rowsout = out/16;
    	colsout = out-16*rowsout;
    	if (in != -1) {
    		for (int ii = 0; ii < 4; ii++)
    			drawTexturedModalRect(x+j, y+k+ii*18, colsin*16, rowsin*16, 16, 16);
    	}
    	if (out != -1)
    		drawTexturedModalRect(x2+j, y2+k, colsout*16, rowsout*16, 16, 16);
    	if (amount > 1)
    		drawString(fontRenderer, String.format("%3d", amount), x2+j+6, y2+k+16, 0xffffff);
    }
    
    /** Draw an extractor recipe in the GUI. Args: x in, y in; icon indexes of top row;
     * x out, y out; icon index of bottom row. 
     * Icon indexes MUST be size-4 arrays! */
    public void drawExtractor(int x, int y, int[] in, int x2, int y2, int[] out, int amount) {
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
    	//ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("DFD324");
    	for (int ij = 0; ij < 4; ij++)
    		drawTexturedModalRect(x+j+36*ij, y+k, colsin[ij]*16, rowsin[ij]*16, 16, 16);
    	for (int ij = 0; ij < 4; ij++)
    		drawTexturedModalRect(x2+j+36*ij, y2+k, colsout[ij]*16, rowsout[ij]*16, 16, 16);
    	
    	if (amount > 1)
    		drawString(fontRenderer, String.format("%3d", amount), x2+j+6, y2+k+16, 0xffffff);

    }
    
    public static void writeItemStack(World world, ItemStack is) {
    	if (ModLoader.getMinecraftInstance().thePlayer == null || world == null)
    		return;
    	if (world.isRemote)
    		return;
    	String msg;
    	if (is == null)
    		msg = "Null Stack!";
    	else
    		msg = String.format("%d, %d, %d", is.itemID, is.stackSize, is.getItemDamage());
    	ModLoader.getMinecraftInstance().thePlayer.addChatMessage(msg);
    }
    
    public static void writeCoords(World world, int x, int y, int z) {
    	if (ModLoader.getMinecraftInstance().thePlayer == null || world == null)
    		return;
    	if (world.isRemote)
    		return;
    	String msg;
    	msg = String.format("%d, %d, %d", x, y, z);
    	ModLoader.getMinecraftInstance().thePlayer.addChatMessage(msg);
    }
    
    public static void writeBlockAtCoords(World world, int x, int y, int z) {
    	if (ModLoader.getMinecraftInstance().thePlayer == null || world == null)
    		return;
    	if (world.isRemote)
    		return;
    	String msg;
    	int id = world.getBlockId(x, y, z);
    	int meta = world.getBlockMetadata(x, y, z);
    	msg = String.format("%d:%d @ %d, %d, %d", id, meta, x, y, z);
    	ModLoader.getMinecraftInstance().thePlayer.addChatMessage(msg);
    }
    
    public static void writeInt(int num) {
    	if (ModLoader.getMinecraftInstance().thePlayer != null)
    		ModLoader.getMinecraftInstance().thePlayer.addChatMessage(String.format("%d", num));
    }
}

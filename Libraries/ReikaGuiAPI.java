/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Exception.MisuseException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ReikaGuiAPI extends GuiScreen {
	private int xSize;
	private int ySize;

	public static ReikaGuiAPI instance = new ReikaGuiAPI();

	private ReikaGuiAPI() {
		mc = Minecraft.getMinecraft();
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
	public void drawTexturedModalRectInvert(int x, int y, int u, int v, int w, int h, int scale)
	{
		y += ySize/2;
		y -= scale;
		v -= scale;
		h = scale;

		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		Tessellator var9 = new Tessellator();
		if (var9.isDrawing)
			var9.draw();
		var9.startDrawingQuads();
		var9.addVertexWithUV(x + 0, y + h, zLevel, (u + 0) * var7, (v + h) * var8);
		var9.addVertexWithUV(x + w, y + h, zLevel, (u + w) * var7, (v + h) * var8);
		var9.addVertexWithUV(x + w, y + 0, zLevel, (u + w) * var7, (v + 0) * var8);
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

	public static void renderFraction(FontRenderer fr, String num, String den, int x, int y, int color, boolean shadow, boolean center) {

	}

	public static void renderRoot(FontRenderer fr, String num, String root, int x, int y, int color, boolean shadow, boolean center) {

	}

	public static void renderPower(FontRenderer fr, String base, String pow, int x, int y, int color, boolean shadow, boolean center) {

	}

	public void drawCustomRecipeList(RenderItem render, FontRenderer f, List<IRecipe> lr, int x, int y, int x2, int y2) {
		if (lr.size() <= 0) {
			//ReikaJavaLibrary.pConsole("No recipes found for "+out);
			return;
		}
		//ReikaJavaLibrary.pConsole(lr.get(0).getRecipeOutput().toString());
		ItemStack[] in = new ItemStack[9];
		int k = ((int)(System.nanoTime()/2000000000))%lr.size();
		//ReikaJavaLibrary.pConsole(k);
		IRecipe ire = lr.get(k);
		ItemStack isout = ire.getRecipeOutput();
		ReikaRecipeHelper.copyRecipeToItemStackArray(in, ire);
		//ReikaJavaLibrary.pConsole(Arrays.toString(in)+" to "+isout);
		boolean noshape = false;
		if (ire instanceof ShapelessRecipes)
			noshape = true;
		if (ire instanceof ShapelessOreRecipe)
			noshape = true;
		this.drawRecipe(render, f, x, y, in, x2, y2, isout, noshape);
	}

	public void drawCustomRecipes(RenderItem render, FontRenderer f, List<ItemStack> out, List<IRecipe> ir, int x, int y, int x2, int y2) {
		List<IRecipe> lr = new ArrayList<IRecipe>();
		for (int i = 0; i < out.size(); i++) {
			lr.addAll(ReikaRecipeHelper.getAllRecipesByOutput(ir, out.get(i)));
		}
		if (lr.size() <= 0) {
			//ReikaJavaLibrary.pConsole("No recipes found for "+out);
			return;
		}
		//ReikaJavaLibrary.pConsole(lr.get(13).getRecipeOutput());
		ItemStack[] in = new ItemStack[9];
		IRecipe ire = lr.get(((int)(System.nanoTime()/2000000000))%lr.size());
		ItemStack isout = ire.getRecipeOutput();
		ReikaRecipeHelper.copyRecipeToItemStackArray(in, ire);
		boolean noshape = false;
		if (ire instanceof ShapelessRecipes)
			noshape = true;
		if (ire instanceof ShapelessOreRecipe)
			noshape = true;
		this.drawRecipe(render, f, x, y, in, x2, y2, isout, noshape);
	}

	/** Draw a crafting recipe in the GUI. Args: x in, y in; items of: top-left, top, top-right, left,
	 * center, right, bottom-left, bottom, bottom right; x out, y out; output item, shapeless t/f.
	 * Input items MUST be a size-9 array! */
	private void drawRecipe(RenderItem render, FontRenderer f, int x, int y, ItemStack[] in, int x2, int y2, ItemStack out, boolean shapeless) {
		if (in.length != 9)
			throw new MisuseException("DrawRecipe() requires 9 input items!");
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2 - 8;
		//ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("DFD324");

		for (int ii = 0; ii < 3; ii++) {
			for (int jj = 0; jj < 3; jj++) {
				//ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("34342");
				if (in[ii*3+jj] != null) {
					this.drawItemStack(render, f, in[ii*3+jj], x+j+18*jj, y+k+18*ii);
					//ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("DFD");
				}
			}
		}
		if (out != null)
			this.drawItemStack(render, f, out, x2+4+j, y2+4+k); /*
		if (out != null && out.getItemDamage() > 0) {
			int dx = 0;
			if (out.getItemDamage() < 10)
				dx = 6;
			if (out.getItemDamage() >= 100)
				dx = -6;
			f.drawStringWithShadow(String.format("%d", out.getItemDamage()), x2+12+j+dx, y2+2+k, 0xffffff);
		}*/
		if (shapeless)
			f.drawString("Shapeless", x2+j-35, y2+k+27, 0x000000);
	}

	/** Draw a smelting recipe in the GUI. Args: output item, x in, y in, x out, y out */
	public void drawSmelting(RenderItem render, FontRenderer f, ItemStack out, int x, int y, int x2, int y2) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2 - 8;

		ItemStack in = ReikaRecipeHelper.getFurnaceInput(out);

		if (in != null)
			this.drawItemStack(render, f, in, x+j, y+k);
		if (out != null)
			this.drawItemStack(render, f, out, x2+4+j, y2+4+k);
	}

	/** Draw a compactor recipe in the GUI. Args: x in, y in, input itemstack, x out, y out, output itemstack */
	public void drawCompressor(RenderItem render, FontRenderer f, int x, int y, ItemStack in, int x2, int y2, ItemStack out) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2 - 8;

		if (in != null) {
			for (int ii = 0; ii < 4; ii++)
				this.drawItemStack(render, f, in, x+j, y+k+ii*18);
		}
		if (out != null)
			this.drawItemStack(render, f, out, x2+j, y2+k);
	}

	/** Draw an extractor recipe in the GUI. Args: x in, y in; items of top row;
	 * x out, y out; items of bottom row.
	 * Items MUST be size-4 arrays! */
	public void drawExtractor(RenderItem render, FontRenderer f, int x, int y, ItemStack[] in, int x2, int y2, ItemStack[] out) {
		if (in.length != 4)
			throw new MisuseException("DrawExtractor() requires 4 input items!");
		if (out.length != 4)
			throw new MisuseException("DrawExtactor() requires 4 output items!");
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2 - 8;

		for (int ij = 0; ij < 4; ij++)
			this.drawItemStack(render, f, in[ij], x+j+36*ij, y+k);
		for (int ij = 0; ij < 4; ij++)
			this.drawItemStack(render, f, out[ij], x2+j+36*ij, y2+k);
	}

	/** Draw a fermenter recipe in the GUI. Args: x,y of top input slot, items of input slots,
	 * x,y out, item output. Item array must be size-3! */
	public void drawFermenter(RenderItem render, FontRenderer f, int x, int y, ItemStack[] in, int x2, int y2, ItemStack out) {
		if (in.length != 3)
			throw new MisuseException("DrawFermenter() requires 3 input items!");
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2 - 8;

		for (int ij = 0; ij < 3; ij++)
			this.drawItemStack(render, f, in[ij], x+j, y+18*ij+k);
		if (out != null)
			this.drawItemStack(render, f, out, x2+4+j, y2+4+k);
	}

	public void drawItemStack(RenderItem renderer, FontRenderer fr, ItemStack is, int x, int y) {
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		zLevel = 200.0F;
		renderer.zLevel = 200.0F;
		FontRenderer font = null;
		if (is != null)
			font = is.getItem().getFontRenderer(is);
		if (font == null)
			font = fr;

		//ReikaJavaLibrary.pConsole(is.getItem().getLocalizedName(is)+" @ "+x+", "+y);

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		short short1 = 240;
		short short2 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		renderer.renderItemAndEffectIntoGUI(font, mc.renderEngine, is, x, y);
		renderer.renderItemOverlayIntoGUI(font, mc.renderEngine, is, x, y, null);
		zLevel = 0.0F;
		renderer.zLevel = 0.0F;
		GL11.glPopMatrix();

		RenderHelper.disableStandardItemLighting();
	}
}

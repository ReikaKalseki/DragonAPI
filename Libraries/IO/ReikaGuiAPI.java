/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Interfaces.WrappedRecipe;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ReikaGuiAPI extends GuiScreen {
	private int xSize;
	private int ySize;

	public static final ReikaGuiAPI instance = new ReikaGuiAPI();

	private ReikaGuiAPI() {
		mc = Minecraft.getMinecraft();
	}

	public int getScreenXInset() {
		return (width - xSize) / 2;
	}

	public int getScreenYInset() {
		return (height - ySize) / 2 - 8;
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
		Tessellator var9 = Tessellator.instance;
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
		Tessellator var9 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		BlendMode.DEFAULT.apply();
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
		GL11.glColor4f(1, 1, 1, 1);
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
		Tessellator var9 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		BlendMode.DEFAULT.apply();
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

	public void renderFraction(FontRenderer fr, String num, String den, int x, int y, int color, boolean shadow, boolean center) {

	}

	public void renderRoot(FontRenderer fr, String num, String root, int x, int y, int color, boolean shadow, boolean center) {

	}

	public void renderPower(FontRenderer fr, String base, String pow, int x, int y, int color, boolean shadow, boolean center) {

	}

	public void drawCustomRecipeList(RenderItem render, FontRenderer f, List<IRecipe> lr, int x, int y, int x2, int y2) {
		if (lr == null || lr.size() <= 0) {
			//ReikaJavaLibrary.pConsole("No recipes found for "+out);
			return;
		}
		//ReikaJavaLibrary.pConsole(lr.get(0).getRecipeOutput().toString());
		ItemStack[] in = new ItemStack[9];
		int k = ((int)(System.nanoTime()/2000000000))%lr.size();
		//ReikaJavaLibrary.pConsole(k);
		Object ir = lr.get(k);
		IRecipe ire = ir instanceof WrappedRecipe ? ((WrappedRecipe)ir).getRecipe() : (IRecipe)ir;
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
		ArrayList<IRecipe> lr = new ArrayList<IRecipe>();
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
		try {
			ReikaRecipeHelper.copyRecipeToItemStackArray(in, ire);
		}
		catch (NullPointerException e) { //temporary fix...
			e.printStackTrace();
			return;
		}
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
		int j = this.getScreenXInset();
		int k = this.getScreenYInset();
		for (int ii = 0; ii < 3; ii++) {
			for (int jj = 0; jj < 3; jj++) {
				if (in[ii*3+jj] != null) {
					in[ii*3+jj].stackSize = 1;
					this.drawItemStackWithTooltip(render, f, in[ii*3+jj], x+j+18*jj, y+k+18*ii);
				}
			}
		}
		if (out != null)
			this.drawItemStackWithTooltip(render, f, out, x2+4+j, y2+4+k);
		if (shapeless)
			f.drawString("Shapeless", x2+j-35, y2+k+27, 0x000000);
	}

	/** Draw a smelting recipe in the GUI. Args: output item, x in, y in, x out, y out */
	public void drawSmelting(RenderItem render, FontRenderer f, ItemStack out, int x, int y, int x2, int y2) {
		int j = this.getScreenXInset();
		int k = this.getScreenYInset();

		ItemStack in = ReikaRecipeHelper.getFurnaceInput(out);

		if (in != null)
			this.drawItemStackWithTooltip(render, f, in, x+j, y+k);
		if (out != null)
			this.drawItemStackWithTooltip(render, f, out, x2+4+j, y2+4+k);
	}

	public void drawItemStack(RenderItem renderer, ItemStack is, int x, int y) {
		this.drawItemStack(renderer, Minecraft.getMinecraft().fontRenderer, is, x, y);
	}

	/** Note that this must be called after any and all texture and text rendering, as the lighting conditions are left a bit off */
	public void drawItemStack(RenderItem renderer, FontRenderer fr, ItemStack is, int x, int y) {
		FontRenderer font = null;
		if (is == null)
			return;
		if (is.getItem() == null)
			return;
		if (is != null && is.getItem() != null)
			font = is.getItem().getFontRenderer(is);
		if (font == null)
			font = fr;

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		short short1 = 240;
		short short2 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		renderer.renderItemAndEffectIntoGUI(font, mc.renderEngine, is, x, y);
		renderer.renderItemOverlayIntoGUI(font, mc.renderEngine, is, x, y, null);
	}

	public void drawItemStackWithTooltip(RenderItem renderer, ItemStack is, int x, int y) {
		this.drawItemStackWithTooltip(renderer, Minecraft.getMinecraft().fontRenderer, is, x, y);
	}

	public void drawItemStackWithTooltip(RenderItem renderer, FontRenderer fr, ItemStack is, int x, int y) {
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		this.drawItemStack(renderer, fr, is, x, y);

		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		if (this.isMouseInBox(x, x+16, y, y+16)) {
			String sg = is.getDisplayName();
			boolean right = this.getMouseRealX() < mc.currentScreen.width/2;
			if (right)
				this.drawTooltipAt(fr, sg, this.getMouseRealX()+fr.getStringWidth(sg)+12, this.getMouseRealY());
			else
				this.drawTooltip(fr, sg);
		}
		GL11.glTranslatef(0.0F, 0.0F, -64.0F);
	}

	public void drawTooltip(FontRenderer f, String s) {
		int mx = this.getMouseRealX()-f.getStringWidth(s)*0;
		int my = this.getMouseRealY();
		this.drawTooltipAt(f, s, mx, my);
	}

	public void drawTooltipAt(FontRenderer f, String s, int mx, int my) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		int k = f.getStringWidth(s);
		int j2 = mx + 12;
		int k2 = my - 12;
		int i1 = 8;

		if (j2 + k > width)
			j2 -= 28 + k;

		if (k2 + i1 + 6 > height)
			;//k2 = height - i1 - 6;

		zLevel = 300.0F;
		itemRender.zLevel = 300.0F;
		int j1 = -267386864;
		this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
		this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
		this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
		this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
		this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
		int k1 = 1347420415;
		int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
		this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
		this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
		this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
		this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		f.drawStringWithShadow(s, j2, k2, 0xffffffff);
		GL11.glPopAttrib();
	}

	public float getMouseScreenY() {
		return 1-(Mouse.getY()/(float)Minecraft.getMinecraft().displayHeight);
	}

	public float getMouseScreenX() {
		return (Mouse.getX()/(float)Minecraft.getMinecraft().displayWidth);
	}

	public int getMouseRealX() {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int w = sr.getScaledWidth();
		int x = Mouse.getX() * w / mc.displayWidth;
		return x;
	}

	public int getMouseRealY() {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int h = sr.getScaledHeight();
		int y = h - Mouse.getY() * h / mc.displayHeight - 1;
		return y;
	}

	public boolean isMouseInBox(int minx, int maxx, int miny, int maxy) {
		int x = this.getMouseRealX();
		int y = this.getMouseRealY();
		return x >= minx && x <= maxx && y >= miny && y <= maxy;
	}

	public void renderStatic(int minx, int miny, int maxx, int maxy) {
		for (int i = minx; i <= maxx; i++) {
			for (int k = miny; k <= maxy; k++) {
				int br = ReikaRandomHelper.getRandomPlusMinus(127, 127);
				int color = ReikaColorAPI.GStoHex(br);
				this.drawRect(i, k, i+1, k+1, 0xff000000 | color);
			}
		}
	}

	public void drawTexturedModalRectWithDepth(int x, int y, int u, int v, int w, int h, int o) {
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, o);
		this.drawTexturedModalRect(x, y, u, v, w, h);
		GL11.glPopMatrix();
	}
}

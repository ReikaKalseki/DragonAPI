/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.IO.DelegateFontRenderer;
import Reika.DragonAPI.Instantiable.Data.Maps.RectangleMap;
import Reika.DragonAPI.Instantiable.Data.Maps.RegionMap;
import Reika.DragonAPI.Interfaces.WrappedRecipe;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Objects.LineType;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ReikaGuiAPI extends GuiScreen {

	private int xSize;
	private int ySize;

	public static int NEI_DEPTH = 0;

	public static final ReikaGuiAPI instance = new ReikaGuiAPI();

	private final RectangleMap<String> tooltips = new RectangleMap();
	private final RegionMap<ItemStack> items = new RegionMap();
	private final boolean cacheRenders = ModList.NEI.isLoaded();

	public static final RenderItem itemRenderer = new RenderItem();

	private ReikaGuiAPI() {
		mc = Minecraft.getMinecraft();
		MinecraftForge.EVENT_BUS.register(this);
	}

	public int getScreenXInset() {
		return (width - xSize) / 2;
	}

	public int getScreenYInset() {
		return (height - ySize) / 2 - 8;
	}

	@SubscribeEvent
	public void preDrawScreen(DrawScreenEvent.Pre evt) {
		if (cacheRenders) {
			tooltips.clear();
			items.clear();
		}
	}

	/**
	 * Renders the specified text to the screen, center-aligned.
	 */
	public void drawCenteredStringNoShadow(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5)
	{
		par1FontRenderer.drawString(par2Str, par3 - par1FontRenderer.getStringWidth(par2Str) / 2, par4, par5);
	}

	@Override
	public void drawGradientRect(int x1, int y1, int x2, int y2, int c1, int c2) {
		super.drawGradientRect(x1, y1, x2, y2, c1, c2);
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
			par5 = par5 | 0xff000000;
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

	/** Draws a line between two points. Args: Start x,y, end x,y, color */
	public void drawLine(double x, double y, double x2, double y2, int color) {
		this.drawLine(x, y, x2, y2, color, LineType.SOLID);
	}

	public void drawLine(double x, double y, double x2, double y2, int color, LineType type) {
		if (type != LineType.THIN && GL11.glGetFloat(GL11.GL_LINE_WIDTH) < 1.5F)
			GL11.glLineWidth(1.5F);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		int alpha = ReikaColorAPI.getAlpha(color);
		if (alpha == 0)
			alpha = 255;
		int red = ReikaColorAPI.getRed(color);
		int green = ReikaColorAPI.getGreen(color);
		int blue = ReikaColorAPI.getBlue(color);
		GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		if (alpha == 255)
			GL11.glDisable(GL11.GL_BLEND);
		else
			GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if (type != LineType.SOLID) {
			type.setMode(2);
		}
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4f(red/255F, green/255F, blue/255F, alpha/255F);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x2, y2);
		GL11.glEnd();
		GL11.glPopAttrib();
	}

	public void drawCircle(double x, double y, double radius, int color) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		int alpha = ReikaColorAPI.getAlpha(color);
		if (alpha == 0)
			alpha = 255;
		int red = ReikaColorAPI.getRed(color);
		int green = ReikaColorAPI.getGreen(color);
		int blue = ReikaColorAPI.getBlue(color);
		GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glColor4f(red/255F, green/255F, blue/255F, alpha/255F);
		for (int i = 0; i < 360; i++) {
			GL11.glVertex2d(x+radius*Math.cos(Math.toRadians(i)), y+radius*Math.sin(Math.toRadians(i)));
		}
		GL11.glEnd();
		GL11.glPopAttrib();
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
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
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
		GL11.glPopAttrib();
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
		int k = ((int)(System.nanoTime()/2000000000))%lr.size();
		//ReikaJavaLibrary.pConsole(k);
		Object ir = lr.get(k);
		IRecipe ire = ir instanceof WrappedRecipe ? ((WrappedRecipe)ir).getRecipe() : (IRecipe)ir;
		ItemStack isout = ire.getRecipeOutput();
		ItemStack[] in = ReikaRecipeHelper.getPermutedRecipeArray(ire);
		if (in == null)
			return;
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
		for (ItemStack is : out) {
			lr.addAll(ReikaRecipeHelper.getAllRecipesByOutput(ir, is));
		}
		if (lr.size() <= 0) {
			//ReikaJavaLibrary.pConsole("No recipes found for "+out);
			return;
		}
		//ReikaJavaLibrary.pConsole(lr.get(13).getRecipeOutput());
		IRecipe ire = lr.get(((int)(System.nanoTime()/2000000000))%lr.size());
		ItemStack isout = ire.getRecipeOutput();
		ItemStack[] in = ReikaRecipeHelper.getPermutedRecipeArray(ire);
		if (in == null)
			return;
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
		this.drawItemStack(renderer, fr, is, x, y, false);
	}

	public void drawItemStack(RenderItem renderer, FontRenderer fr, ItemStack is, int x, int y, boolean forceStackSize) {
		FontRenderer font = null;
		if (is == null)
			return;
		if (is.getItem() == null)
			return;
		if (is != null && is.getItem() != null)
			font = is.getItem().getFontRenderer(is);
		if (font == null)
			font = fr;
		/*
		if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			List li = ReikaItemHelper.getAllPermutations(is.getItem());
			if (!li.isEmpty()) {
				int tick = (int)((System.currentTimeMillis()/1000)%li.size());
				is = li.get(tick);
			}
		}
		 */
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
		renderer.renderItemOverlayIntoGUI(font, mc.renderEngine, is, x, y, forceStackSize ? String.valueOf(is.stackSize) : null);

		if (cacheRenders)
			items.addRegionByWH(x, y, 16, 16, is.copy());
	}

	/*
	public void drawItemStackWithTooltip(RenderItem renderer, ItemStack is, int x, int y) {
		this.drawItemStackWithTooltip(renderer, Minecraft.getMinecraft().fontRenderer, is, x, y);
	}
	 */

	public void drawItemStackWithTooltip(RenderItem renderer, FontRenderer fr, ItemStack is, int x, int y) {
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		FontRenderer f2 = Minecraft.getMinecraft().fontRenderer;//is.getItem().getFontRenderer(is);
		if (f2 != null)
			fr = f2;
		if (is.getItemDamage() == 32767) {
			is = is.copy();
			is.setItemDamage(0);
		}
		this.drawItemStack(renderer, fr, is, x, y);

		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		if (this.isMouseInBox(x, x+16, y, y+16)) {
			String sg = is.getDisplayName();
			if (sg == null) {
				sg = is.toString()+"{"+is.stackTagCompound+"}";
			}
			boolean right = this.getMouseRealX() < mc.currentScreen.width/2;
			if (right)
				this.drawTooltipAt(fr, sg, this.getMouseRealX()+fr.getStringWidth(sg)+12, this.getMouseRealY());
			else
				this.drawTooltip(fr, sg);
		}
		GL11.glTranslatef(0.0F, 0.0F, -64.0F);
	}

	public void drawMultilineTooltip(List<String> li, int x, int y, int spacing, boolean center) {
		GL11.glTranslatef(0.0F, 0.0F, 64.0F);
		int dy = y;
		for (String s : li) {
			int dx = center ? x+mc.fontRenderer.getStringWidth(s)/2 : x;
			this.drawTooltipAt(mc.fontRenderer, s, dx, dy);
			dy += spacing;
		}
		GL11.glTranslatef(0.0F, 0.0F, -64.0F);
	}

	public void drawMultilineTooltip(ItemStack is, int x, int y) {
		if (this.isMouseInBox(x, x+16, y, y+16)) {
			List<String> li = new ArrayList();
			li.add(is.getDisplayName());
			is.getItem().addInformation(is, Minecraft.getMinecraft().thePlayer, li, true);
			this.drawMultilineTooltip(li, x, y, 17, false);
		}
	}

	public void drawTooltip(FontRenderer f, String s) {
		int x = this.getMouseRealX()-f.getStringWidth(s)*0;
		int y = this.getMouseRealY();
		this.drawTooltipAt(f, s, x, y);
	}

	public void drawTooltip(FontRenderer f, String s, int dx, int dy) {
		int x = this.getMouseRealX()-f.getStringWidth(s)*0;
		int y = this.getMouseRealY();
		this.drawTooltipAt(f, s, x+dx, y+dy);
	}

	public void drawTooltipAt(FontRenderer f, String s, int mx, int my) {
		if (s == null)
			s = "[null]";
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		//GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_LIGHTING);
		int k = f.getStringWidth(DelegateFontRenderer.stripFlags(s));
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
		GL11.glPopMatrix();

		if (cacheRenders)
			tooltips.addItem(s, mx, my+8, f.getStringWidth(s)+24, f.FONT_HEIGHT+8);
	}

	public void drawSplitTooltipAt(FontRenderer f, List<String> li, int mx, int my) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		int k = -1;
		for (String s : li) {
			k = Math.max(k, f.getStringWidth(DelegateFontRenderer.stripFlags(s)));
		}
		int j2 = mx + 12;
		int k2 = my - 12;
		int i1 = 8*li.size()+(2*li.size()-1)-1;

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

		for (int i = 0; i < li.size(); i++) {
			String s = li.get(i);
			f.drawStringWithShadow(s, j2, k2+i*10, 0xffffffff);
			if (cacheRenders)
				tooltips.addItem(s, mx, my+8+i*10, f.getStringWidth(s)+24, f.FONT_HEIGHT+8);
		}

		GL11.glPopAttrib();
	}

	public Map<String, Rectangle> getTooltips() {
		return tooltips.view();
	}

	public Map<Rectangle, ItemStack> getRenderedItems() {
		return items.view();
	}

	/** This function is computationally expensive! */
	public ItemStack getItemRenderAt(int x, int y) {
		return items.getRegion(x, y);
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
				int br = ReikaRandomHelper.getRandomBetween(0, 255);
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

	public void drawRectFrame(int minx, int miny, int w, int h, int color) {
		this.drawRectFrame(minx, miny, w, h, color, LineType.SOLID);
	}

	public void drawRectFrame(int minx, int miny, int w, int h, int color, LineType type) {
		int maxx = minx+w;
		int maxy = miny+h;
		this.drawLine(minx, miny, maxx, miny, color, type);
		this.drawLine(minx, maxy, maxx, maxy, color, type);
		this.drawLine(minx, miny, minx, maxy, color, type);
		this.drawLine(maxx, miny, maxx, maxy, color, type);
	}

	public void drawVanillaHealthBar(EntityPlayer ep, ScaledResolution sr, int rowHeight) {
		this.drawVanillaHealthBar(ep, sr, rowHeight, null);
	}

	public void drawVanillaHealthBar(EntityPlayer ep, ScaledResolution sr, int rowHeight, HashMap<Integer, Integer> colors) {
		GL11.glEnable(GL11.GL_BLEND);
		if (colors != null && !colors.isEmpty())
			ReikaTextureHelper.bindFinalTexture(DragonAPICore.class, "Resources/gui.png");
		boolean highlight = ep.hurtResistantTime/3 % 2 == 1;

		if (ep.hurtResistantTime < 10) {
			highlight = false;
		}

		IAttributeInstance attrMaxHealth = ep.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		int health = MathHelper.ceiling_float_int(ep.getHealth());
		int healthLast = MathHelper.ceiling_float_int(ep.prevHealth);
		float healthMax = (float)attrMaxHealth.getAttributeValue();
		float absorb = ep.getAbsorptionAmount();

		int healthRows = MathHelper.ceiling_float_int((healthMax+absorb)/2.0F/10.0F);

		int left = sr.getScaledWidth()/2-91;
		int top = sr.getScaledHeight()-GuiIngameForge.left_height;
		GuiIngameForge.left_height += (healthRows*rowHeight);
		if (rowHeight != 10)
			GuiIngameForge.left_height += 10-rowHeight;

		int regen = -1; //regen "bounced" heart
		if (ep.isPotionActive(Potion.regeneration)) {
			regen = (int)(System.currentTimeMillis()/50) % 25;
		}

		int upper =  9*(ep.worldObj.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
		int back = (highlight ? 25 : 16);
		int margin = 16;
		if (ep.isPotionActive(Potion.poison))
			margin += 36;
		else if (ep.isPotionActive(Potion.wither))
			margin += 72;
		float absorbRemaining = absorb;

		for (int i = MathHelper.ceiling_float_int((healthMax+absorb)/2.0F)-1; i >= 0; i--) {
			int row = MathHelper.ceiling_float_int((i+1)/10.0F)-1;
			int x = left+i % 10*8;
			int y = top-row*rowHeight;

			if (health <= 4)
				y += ReikaRandomHelper.getRandomPlusMinus(2, 1);
			if (i == regen)
				y -= 2;

			this.drawTexturedModalRect(x, y, back, upper, 9, 9);

			if (highlight) {
				if (i*2+1 < healthLast)
					this.drawTexturedModalRect(x, y, margin+54, upper, 9, 9); //draw full heart
				else if (i*2+1 == healthLast)
					this.drawTexturedModalRect(x, y, margin+63, upper, 9, 9); //draw half heart
			}

			if (absorbRemaining > 0) {
				if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
					this.drawTexturedModalRect(x, y, margin+153, upper, 9, 9); //draw full heart
				else
					this.drawTexturedModalRect(x, y, margin+144, upper, 9, 9); //draw half heart
				absorbRemaining -= 2.0F;
			}
			else {
				if (colors != null) {
					Integer color = colors.get(i);
					if (color != null) {
						float red = ReikaColorAPI.getRed(color.intValue())/255F;
						float green = ReikaColorAPI.getGreen(color.intValue())/255F;
						float blue = ReikaColorAPI.getBlue(color.intValue())/255F;
						GL11.glColor3f(red, green, blue);
					}
				}
				if (i*2+1 < health) //heart is filled
					this.drawTexturedModalRect(x, y, margin+36, upper, 9, 9); //draw full heart
				else if (i*2+1 == health)
					this.drawTexturedModalRect(x, y, margin+45, upper, 9, 9); //draw half heart
				GL11.glColor3f(1, 1, 1);
			}
		}

		ReikaTextureHelper.bindHUDTexture();
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void setZLevel(float z) {
		zLevel = z;
	}

	public float getZLevel() {
		return zLevel;
	}

}

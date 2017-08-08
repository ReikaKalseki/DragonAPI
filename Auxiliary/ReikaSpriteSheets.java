/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Interfaces.Item.AnimatedSpritesheet;
import Reika.DragonAPI.Interfaces.Item.BlendedColor;
import Reika.DragonAPI.Interfaces.Item.GradientBlend;
import Reika.DragonAPI.Interfaces.Item.MultiLayerItemSprite;
import Reika.DragonAPI.Interfaces.Item.SpriteRenderCallback;
import Reika.DragonAPI.Interfaces.Item.ToolSprite;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public final class ReikaSpriteSheets {

	private static int zLevel = 0;

	private ReikaSpriteSheets() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

	private static final RenderItem itemRender = new RenderItem();

	/** Call this from a registered ItemRenderer class that implements IItemRenderer to actually render the Items.
	 * It will automatically compensate for being used for inventory/entity/held items.
	 * Args: Texture root class, Texture path, Sprite Index, ItemRenderType, ItemStack, Data */
	public static void renderItem(Class root, String tex, int idx, ItemRenderType type, ItemStack is, Object... data) {
		if (is == null)
			return;
		Item item = is.getItem();
		if (item == null)
			return;
		if (item instanceof SpriteRenderCallback) {
			GL11.glPushMatrix();
			SpriteRenderCallback spr = (SpriteRenderCallback)item;
			if (spr.doPreGLTransforms(is, type)) {
				if (type == ItemRenderType.INVENTORY)
					prepareInvRender();
				else if (type == ItemRenderType.EQUIPPED)
					prepareHeldRender3rdP();
				else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
					prepareHeldRender();
			}
			boolean res = spr.onRender(itemRender, is, type);
			GL11.glPopMatrix();
			if (res)
				return;
		}
		int[] indices = new int[]{idx};
		if (item instanceof MultiLayerItemSprite) {
			MultiLayerItemSprite m = (MultiLayerItemSprite)item;
			indices = m.getIndices(is);
		}
		GL11.glPushMatrix();
		for (int i = 0; i < indices.length; i++) {
			GL11.glPushMatrix();
			double z = 0.001*i;
			int index = indices[i];
			int row = index/16;
			int col = index-row*16;
			if (item instanceof AnimatedSpritesheet) {
				AnimatedSpritesheet a = (AnimatedSpritesheet)item;
				if (a.useAnimatedRender(is)) {
					int offset = (int)((System.currentTimeMillis()/32/a.getFrameSpeed()+a.getFrameOffset(is))%a.getFrameCount());
					col = a.getColumn(is);
					row = a.getBaseRow(is);
					if (a.verticalFrames())
						row += offset;
					else
						col += offset;
					tex = a.getTexture(is);
				}
			}
			ReikaTextureHelper.bindTexture(root, tex);

			initGL(type);

			Tessellator v5 = Tessellator.instance;
			if (type == type.INVENTORY) {
				if (v5.isDrawing)
					v5.draw();
				boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_BLEND);
				int dst = GL11.glGetInteger(GL11.GL_BLEND_DST);
				int src = GL11.glGetInteger(GL11.GL_BLEND_SRC);
				GL11.glPushMatrix();
				prepareInvRender();
				v5.startDrawingQuads();
				int c1 = 0xffffff;
				int c2 = 0xffffff;
				int c3 = 0xffffff;
				int c4 = 0xffffff;
				if (item instanceof GradientBlend) {
					GL11.glShadeModel(GL11.GL_SMOOTH);
					GradientBlend g = (GradientBlend)item;
					c1 = g.getColorOne(is);
					c2 = g.getColorTwo(is);
					c3 = g.getColorThree(is);
					c4 = g.getColorFour(is);
				}
				else if (item instanceof BlendedColor) {
					int c = ((BlendedColor)item).getColor(is);
					c1 = c2 = c3 = c4 = c;
				}
				v5.setColorOpaque_I(c1);
				v5.addVertexWithUV(0, 0, z, 0.0625F*col, 0.0625F+0.0625F*row);
				v5.setColorOpaque_I(c2);
				v5.addVertexWithUV(1, 0, z, 0.0625F+0.0625F*col, 0.0625F+0.0625F*row);
				v5.setColorOpaque_I(c3);
				v5.addVertexWithUV(1, 1, z, 0.0625F+0.0625F*col, 0.0625F*row);
				v5.setColorOpaque_I(c4);
				v5.addVertexWithUV(0, 1, z, 0.0625F*col, 0.0625F*row);
				v5.draw();
				GL11.glPopMatrix();
				GL11.glBlendFunc(src, dst);
				if (!blend)
					GL11.glDisable(GL11.GL_BLEND);
				GL11.glShadeModel(GL11.GL_FLAT);
			}
			if (type == type.EQUIPPED || type == type.EQUIPPED_FIRST_PERSON || type == type.ENTITY) {
				if (type == type.EQUIPPED && (item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemShears || item instanceof ToolSprite)) {
					prepareHeldToolRender();
				}
				else if (type == type.EQUIPPED_FIRST_PERSON) {
					prepareHeldRender();
				}
				else if (type == type.EQUIPPED) {
					prepareHeldRender3rdP();
				}
				else { //Entity
					double sc = 0.6;
					//GL11.glScaled(sc, sc, sc);
					GL11.glRotatef(90, 0, 1, 0);
					GL11.glTranslated(-0.5, 0, 0);
					GL11.glTranslated(0, -0.375, 0);
					//GL11.glTranslated(0, 0, 0.125);
				}
				float thick = 0.0625F;
				if (Minecraft.getMinecraft().gameSettings.fancyGraphics || type == type.EQUIPPED_FIRST_PERSON || type == type.EQUIPPED)
					ItemRenderer.renderItemIn2D(v5, 0.0625F+0.0625F*col, 0.0625F*row, 0.0625F*col, 0.0625F+0.0625F*row, 16, 16, thick);
				else {
					if (type == type.ENTITY) {
						GL11.glRotatef(180.0F - RenderManager.instance.playerViewY-90, 0.0F, 1.0F, 0.0F);
						GL11.glRotatef(-RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
					}
					int c1 = 0xffffff;
					int c2 = 0xffffff;
					int c3 = 0xffffff;
					int c4 = 0xffffff;
					if (item instanceof GradientBlend) {
						GL11.glShadeModel(GL11.GL_SMOOTH);
						GradientBlend g = (GradientBlend)item;
						c1 = g.getColorOne(is);
						c2 = g.getColorTwo(is);
						c3 = g.getColorThree(is);
						c4 = g.getColorFour(is);
					}
					else if (item instanceof BlendedColor) {
						int c = ((BlendedColor)item).getColor(is);
						c1 = c2 = c3 = c4 = c;
					}
					GL11.glColor4f(1, 1, 1, 1);
					v5.startDrawingQuads();
					float u = col/16F;
					float v = row/16F;
					v5.setColorOpaque(255, 255, 255);
					v5.setColorOpaque_I(c1);
					v5.addVertexWithUV(0, 0, 0, u, v+0.0625);
					v5.setColorOpaque_I(c2);
					v5.addVertexWithUV(1, 0, 0, u+0.0625, v+0.0625);
					v5.setColorOpaque_I(c3);
					v5.addVertexWithUV(1, 1, 0, u+0.0625, v);
					v5.setColorOpaque_I(c4);
					v5.addVertexWithUV(0, 1, 0, u, v);
					v5.draw();
				}
			}
			GL11.glPopMatrix();
		}
		renderEffect(type, is);

		GL11.glEnable(GL11.GL_LIGHTING);
		ReikaTextureHelper.bindItemTexture();
		GL11.glPopMatrix();
	}

	private static void prepareHeldToolRender() {
		GL11.glTranslated(0.1, 0.15, 0);
		float r = 135;
		GL11.glRotated(r, 0, 1, 0);
		GL11.glRotated(-100, 0, 0, 1);
		double d = -2;
		GL11.glTranslated(d, d, 0);
		double s = 2.5;
		GL11.glScaled(s, s, s);
	}

	private static void prepareHeldRender3rdP() {
		GL11.glRotated(90, 1, 0, 0);
		GL11.glRotated(135, 0, 0, 1);
		double d = 1.5;
		GL11.glScaled(d, d, d);
		GL11.glTranslated(0, -1, 0);
		GL11.glTranslated(-0.2, 0, -0.4);
		GL11.glRotated(-20, 0, 1, 0);
		GL11.glRotated(-30, 1, 0, 0);
	}

	private static void prepareHeldRender() {
		GL11.glTranslatef(0, 1.25F, 0.3125F);
		GL11.glRotatef(60, 0, 1, 0);
		GL11.glRotatef(65, 0, 0, 1);
		GL11.glTranslatef(-0.625F, 0F, 0);
		GL11.glScalef(1.5F, 1.5F, 1.5F);
		GL11.glRotatef(-90, 0, 0, 1);
		GL11.glTranslatef(-1, 0, 0);
		GL11.glTranslatef(0.5F, 0, 0.25F);
		GL11.glRotatef(-10, 0, 1, 0);
		GL11.glTranslatef(-0.125F, -0.125F, 0F);
		/*
		double r = -45;
		double r2 = 0;
		double r3 = 30;
		double s = 2.25;
		double d = 0;
		GL11.glRotated(r, 0, 1, 0);
		GL11.glRotated(r2, 1, 0, 0);
		GL11.glRotated(r3, 0, 0, 1);
		GL11.glScaled(s, s, s);
		GL11.glTranslated(d, d, 0);
		 */
	}

	private static void prepareInvRender() {
		BlendMode.DEFAULT.apply();
		double r = 45;
		double r2 = -30;
		double s = 1.6;
		double d = -0.5;
		GL11.glRotated(r, 0, 1, 0);
		GL11.glRotated(r2, 1, 0, 0);
		GL11.glScaled(s, s, s);
		GL11.glTranslated(d, d, 0);
	}

	private static void initGL(ItemRenderType type) {
		if (type == ItemRenderType.INVENTORY) {
			GL11.glDisable(GL11.GL_LIGHTING);
		}
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glPopMatrix();
	}

	public static void renderEffect(ItemRenderType ir, ItemStack is) {
		GL11.glPushMatrix();
		int pass = MinecraftForgeClient.getRenderPass();
		Tessellator tessellator = Tessellator.instance;

		int par4 = 0;
		int par5 = 0;

		if (is.hasEffect(pass))
		{
			if (ir == ItemRenderType.INVENTORY) {
				renderEffect(Minecraft.getMinecraft().renderEngine, 5, 5);
			}
			else if (ir == ItemRenderType.ENTITY || ir == ItemRenderType.EQUIPPED || ir == ItemRenderType.EQUIPPED_FIRST_PERSON) {
				if (is.hasEffect(pass))
				{
					float f12 = 0.0625F;

					GL11.glDepthFunc(GL11.GL_EQUAL);
					GL11.glDisable(GL11.GL_LIGHTING);
					ReikaTextureHelper.bindEnchantmentTexture();
					GL11.glEnable(GL11.GL_BLEND);
					BlendMode.OVERLAYDARK.apply();
					float f13 = 0.76F;
					GL11.glColor4f(0.5F * f13, 0.25F * f13, 0.8F * f13, 1.0F);
					GL11.glMatrixMode(GL11.GL_TEXTURE);
					GL11.glPushMatrix();
					float f14 = 0.125F;
					GL11.glScalef(f14, f14, f14);
					float f15 = Minecraft.getSystemTime() % 3000L / 3000.0F * 8.0F;
					GL11.glTranslatef(f15, 0.0F, 0.0F);
					GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
					ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f12);
					GL11.glPopMatrix();
					GL11.glPushMatrix();
					GL11.glScalef(f14, f14, f14);
					f15 = Minecraft.getSystemTime() % 4873L / 4873.0F * 8.0F;
					GL11.glTranslatef(-f15, 0.0F, 0.0F);
					GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
					ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f12);
					GL11.glPopMatrix();
					GL11.glMatrixMode(GL11.GL_MODELVIEW);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glDepthFunc(GL11.GL_LEQUAL);
				}
			}
		}
		GL11.glPopMatrix();
	}

	private static void renderEffect(TextureManager manager, int x, int y)
	{
		GL11.glDepthFunc(GL11.GL_GREATER);
		GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		ReikaTextureHelper.bindEnchantmentTexture();
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.OVERLAYDARK.apply();
		GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);

		GL11.glRotated(45, 0, 1, 0);
		GL11.glRotated(-45, 1, 0, 0);

		double d = -0.8125;
		double s = 1.75;

		float du = 0.25F;

		float u = Minecraft.getSystemTime()%12000/3000.0F * 2F;

		GL11.glTranslated(d, d, 0);
		Tessellator v5 = Tessellator.instance;

		v5.startDrawingQuads();
		v5.addVertexWithUV(0, 0, -1, du*u, 0);
		v5.addVertexWithUV(s, 0, -1, du*u+du*1, 0);
		v5.addVertexWithUV(s, s, -1, du*u+du*1, du*1);
		v5.addVertexWithUV(0, s, -1, du*u, du*1);
		v5.draw();

		double r = 90;
		u *= -1.5F;

		GL11.glRotated(r, 0, 0, 1);
		GL11.glTranslated(0, -s, 0);

		v5.startDrawingQuads();
		v5.addVertexWithUV(0, 0, -2, du*u, 0);
		v5.addVertexWithUV(s, 0, -2, du*u+du*1, 0);
		v5.addVertexWithUV(s, s, -2, du*u+du*1, du*1);
		v5.addVertexWithUV(0, s, -2, du*u, du*1);
		v5.draw();

		GL11.glTranslated(0, s, 0);
		GL11.glRotated(-r, 0, 0, 1);

		GL11.glTranslated(-d, -d, 0);

		GL11.glRotated(-45, 1, 0, 0);
		GL11.glRotated(45, 0, 1, 0);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		BlendMode.DEFAULT.apply();
	}

}

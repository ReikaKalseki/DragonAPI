/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper.RenderDistance;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class LODModelPart extends ModelRenderer {

	private double renderDistanceSqr = -1;
	private double lastDistance;
	private int lastTileX;
	private int lastTileY;
	private int lastTileZ;
	private double lastPlayerX;
	private double lastPlayerY;
	private double lastPlayerZ;

	private int textureX;
	private int textureZ;

	public LODModelPart(ModelBase baseModel, int textureX, int textureZ) {
		super(baseModel, textureX, textureZ);
	}

	@Override
	public final ModelRenderer setTextureOffset(int x, int z) {
		super.setTextureOffset(x, z);
		textureX = x;
		textureZ = z;
		return this;
	}

	@Override
	public final ModelRenderer setTextureSize(int w, int h) {
		super.setTextureSize(w, h);
		for (MovableBox b : ((List<MovableBox>)cubeList)) {
			b.textureWidth = w;
			b.textureHeight = h;
		}
		return this;
	}

	@Override
	public final ModelRenderer addBox(float par1, float par2, float par3, int par4, int par5, int par6) {
		cubeList.add(new MovableBox(this, par1, par2, par3, par4, par5, par6));
		float size = this.calculateVolume();
		renderDistanceSqr = this.calculateRenderDistance(size);
		return this;
	}

	public final void addBox(LODModelPart model) {
		for (MovableBox b : ((List<MovableBox>)model.cubeList)) {
			MovableBox b2 = b.move(model, model.rotationPointX, model.rotationPointY, model.rotationPointZ);
			ReikaJavaLibrary.pConsole(b+" moves to "+b2);
			cubeList.add(b2);
		}
		float size = this.calculateVolume();
		renderDistanceSqr = this.calculateRenderDistance(size);
	}

	@Override
	public final void setRotationPoint(float x, float y, float z) {
		super.setRotationPoint(x, y, z);
	}

	protected final MovableBox getBox(int idx) {
		return (MovableBox)cubeList.get(idx);
	}

	private final float calculateVolume() {
		MovableBox box = this.getBox(cubeList.size()-1);
		float x = box.posX1-box.posX2;
		float y = box.posY1-box.posY2;
		float z = box.posZ1-box.posZ2;
		return Math.abs(x*y*z);
	}

	private double calculateRenderDistance(float size) {
		int d = 0;
		if (size > 1024) {
			d = 16384;
		}
		else if (size > 512) {
			d = 4096;
		}
		else if (size > 128) {
			d = 2048;
		}
		else if (size > 32) {
			d = 1024;
		}
		else if (size > 8) {
			d = 256;
		}
		else if (size > 4) {
			d = 128;
		}
		else if (size > 0){
			d = 96;
		}
		else {
			d = 0;
		}
		return Math.max(renderDistanceSqr, d);
	}

	public final boolean shouldRender(double dist_squared) {
		return GuiScreen.isCtrlKeyDown() || renderDistanceSqr*this.getDistanceMultiplier() >= dist_squared;
	}

	private double getDistanceMultiplier() {
		RenderDistance r = ReikaRenderHelper.getRenderDistance();
		switch (r) {
			case FAR:
				return 2;
			case NORMAL:
				return 1;
			case SHORT:
				return 0.75;
			case TINY:
				return 0.4;
			default:
				return 1;
		}
	}

	public final void render(TileEntity te, float pixelSize) {
		if (te.hasWorldObj())
			this.calcAndCacheRenderDistance(te);

		if (!te.hasWorldObj() || MinecraftForgeClient.getRenderPass() == -1 || this.shouldRender(lastDistance)) {
			super.render(pixelSize);
		}
	}

	private void calcAndCacheRenderDistance(TileEntity te) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (lastTileX == te.xCoord && lastTileY == te.yCoord && lastTileZ == te.zCoord && lastPlayerX == ep.posX && lastPlayerY == ep.posY && lastPlayerZ == ep.posZ) {

		}
		else {
			lastTileX = te.xCoord;
			lastTileY = te.yCoord;
			lastTileZ = te.zCoord;
			lastPlayerX = ep.posX;
			lastPlayerY = ep.posY;
			lastPlayerZ = ep.posZ;
			lastDistance = ep.getDistanceSq(lastTileX+0.5, lastTileY+0.5, lastTileZ+0.5);
		}
	}

	private static class MovableBox extends ModelBox {

		private final int textureX;
		private final int textureZ;
		private float textureWidth;
		private float textureHeight;

		private final float originX;
		private final float originY;
		private final float originZ;

		private final int sizeX;
		private final int sizeY;
		private final int sizeZ;

		private final float rotationX;
		private final float rotationY;
		private final float rotationZ;

		private final float rotationOriginX;
		private final float rotationOriginY;
		private final float rotationOriginZ;

		private MovableBox(LODModelPart model, float pX, float pY, float pZ, int sX, int sY, int sZ) {
			this(model, model.textureX, model.textureZ, model.textureWidth, model.textureHeight, pX, pY, pZ, sX, sY, sZ);
		}

		private MovableBox(LODModelPart model, int tX, int tZ, float tW, float tH, float pX, float pY, float pZ, int sX, int sY, int sZ) {
			super(model, tX, tZ, pX, pY, pZ, sX, sY, sZ, 0);

			textureX = tX;
			textureZ = tZ;
			textureWidth = tW;
			textureHeight = tH;
			originX = pX;
			originY = pY;
			originZ = pZ;
			sizeX = sX;
			sizeY = sY;
			sizeZ = sZ;

			rotationX = model.rotateAngleX;
			rotationY = model.rotateAngleY;
			rotationZ = model.rotateAngleZ;

			rotationOriginX = model.rotationPointX;
			rotationOriginY = model.rotationPointY;
			rotationOriginZ = model.rotationPointZ;
		}

		private MovableBox move(LODModelPart p, float x, float y, float z) {
			return new MovableBox(p, textureX, textureZ, textureWidth, textureHeight, originX+x, originY+y, originZ+z, sizeX, sizeY, sizeZ);
		}

		@Override
		public String toString() {
			return originX+", "+originY+", "+originZ+" > "+sizeX+"x"+sizeY+"x"+sizeZ+" tex "+textureX+","+textureZ+" > "+textureWidth+"x"+textureHeight;
		}

		@Override
		public void render(Tessellator v5, float f5) {
			if (rotationX != 0 || rotationY != 0 || rotationZ != 0) {
				GL11.glPushMatrix();
				GL11.glTranslatef(rotationOriginX * f5, rotationOriginY * f5, rotationOriginZ * f5);

				if (rotationZ != 0)
					GL11.glRotatef(rotationZ * (180F / (float)Math.PI), 0F, 0F, 1F);
				if (rotationY != 0)
					GL11.glRotatef(rotationY * (180F / (float)Math.PI), 0F, 1F, 0F);
				if (rotationX != 0)
					GL11.glRotatef(rotationX * (180F / (float)Math.PI), 1F, 0F, 0F);

				GL11.glTranslatef(-rotationOriginX * f5, -rotationOriginY * f5, -rotationOriginZ * f5);
				super.render(v5, f5);
				GL11.glPopMatrix();
			}
			else {
				super.render(v5, f5);
			}
		}

	}

}

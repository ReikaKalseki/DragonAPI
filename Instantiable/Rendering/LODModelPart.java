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

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper.RenderDistance;
import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LODModelPart extends ModelRenderer {

	private static final String JVM_FLAG = "-DragonAPI_CompileLODModels";
	public static boolean allowCompiling = ReikaJVMParser.isArgumentPresent(JVM_FLAG);

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

	private int displayList = -1;

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
		if (allowCompiling) {
			for (MovableBox b : ((List<MovableBox>)cubeList)) {
				b.textureWidth = w;
				b.textureHeight = h;
			}
		}
		return this;
	}

	@Override
	public final void setRotationPoint(float x, float y, float z) {
		super.setRotationPoint(x, y, z);
		if (allowCompiling) {
			for (MovableBox b : ((List<MovableBox>)cubeList)) {
				b.offsetX = offsetX+x;
				b.offsetY = offsetY+y;
				b.offsetZ = offsetZ+z;
			}
		}
	}

	@Override
	public final ModelRenderer addBox(float par1, float par2, float par3, int par4, int par5, int par6) {
		this.addBox(par1, par2, par3, par4, par5, par6, 0);
		return this;
	}

	@Override
	public final void addBox(float par1, float par2, float par3, int par4, int par5, int par6, float par7) {
		if (allowCompiling) {
			cubeList.add(new MovableBox(this, par1, par2, par3, par4, par5, par6));
		}
		else {
			if (cubeList.size() > 0) {
				throw new MisuseException("You cannot have multiple pieces per model unless model compiling is enabled (jvm arg '"+JVM_FLAG+"')!");
			}
			else {
				super.addBox(par1, par2, par3, par4, par5, par6);
			}
		}
		float size = this.calculateVolume();
		renderDistanceSqr = this.calculateRenderDistance(size);
	}

	public final void addBox(LODModelPart model) {
		if (!allowCompiling) {
			throw new MisuseException("You cannot have multiple pieces per model unless model compiling is enabled (jvm arg '"+JVM_FLAG+"')!");
		}
		for (MovableBox b : ((List<MovableBox>)model.cubeList)) {
			MovableBox b2 = b.move(model, model.rotationPointX, model.rotationPointY, model.rotationPointZ);
			/*
			if (b.rotationX != 0 || b.rotationY != 0 || b.rotationZ != 0) {
				if (b2.rotationX == 0 && b2.rotationY == 0 && b2.rotationZ == 0) {
					ReikaJavaLibrary.pConsole("Undoing rotation!!");
				}
			}
			 */
			//ReikaJavaLibrary.pConsole(b+" moves to "+b2+" rot "+b2.rotationX+", "+b2.rotationY+", "+b2.rotationZ, (model.rotateAngleX != 0 || model.rotateAngleY != 0 || model.rotateAngleZ != 0));
			cubeList.add(b2);
		}
		float size = this.calculateVolume();
		renderDistanceSqr = this.calculateRenderDistance(size);
	}

	@Override
	public final ModelRenderer addBox(String s, float par1, float par2, float par3, int par4, int par5, int par6) {
		throw new MisuseException("Invalid box system for LODModelPart "+this+"!");
	}

	@Override
	public final void addChild(ModelRenderer model) {
		throw new MisuseException("Children not permitted for LODModelPart "+this+"!");
	}

	protected final ModelBox getBox(int idx) {
		return (ModelBox)cubeList.get(idx);
	}

	private final float calculateVolume() {
		ModelBox box = this.getBox(cubeList.size()-1);
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
			//this.render(pixelSize);
		}
	}

	@Override
	public final void render(float pixelSize) {
		if (allowCompiling) {
			if (GuiScreen.isCtrlKeyDown()) {
				Tessellator v5 = Tessellator.instance;
				for (MovableBox b : ((List<MovableBox>)cubeList)) {
					b.render(v5, pixelSize);
				}
			}
			else {
				if (displayList == -1 || Keyboard.isKeyDown(Keyboard.KEY_GRAVE)) {
					this.compileDisplayList(pixelSize);
				}
				GL11.glCallList(displayList);
			}
		}
		else {
			super.render(pixelSize);
		}
	}

	@SideOnly(Side.CLIENT)
	private void compileDisplayList(float pixelSize)  {
		if (!allowCompiling) {
			throw new MisuseException("You may not use GL lists with non-compilable models!");
		}
		displayList = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		Tessellator v5 = Tessellator.instance;

		for (MovableBox b : ((List<MovableBox>)cubeList)) {
			b.render(v5, pixelSize);
		}

		GL11.glEndList();
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

		private float offsetX;
		private float offsetY;
		private float offsetZ;

		private MovableBox(LODModelPart model, float pX, float pY, float pZ, int sX, int sY, int sZ) {
			this(model, model.textureX, model.textureZ, model.textureWidth, model.textureHeight, pX, pY, pZ, sX, sY, sZ);
		}

		private MovableBox(LODModelPart model, int tX, int tZ, float tW, float tH, float pX, float pY, float pZ, int sX, int sY, int sZ) {
			super(model, tX, tZ, pX, pY, pZ, sX, sY, sZ, 0);

			if (!allowCompiling) {
				throw new MisuseException("You cannot have dynamic model boxes unless model compiling is enabled (jvm arg '"+JVM_FLAG+"')!");
			}

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

			offsetX = model.offsetX;
			offsetY = model.offsetY;
			offsetZ = model.offsetZ;
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
			//GL11.glPushMatrix();
			//GL11.glTranslated(offsetX*f5, offsetY*f5, offsetZ*f5);
			//v5.addTranslation(offsetX*f5, offsetY*f5, offsetZ*f5);
			//ReikaJavaLibrary.pConsole(this+" > "+rotationX+" / "+rotationY+" / "+rotationZ);
			if (rotationX != 0 || rotationY != 0 || rotationZ != 0) {
				GL11.glPushMatrix();
				//ReikaJavaLibrary.pConsole(this+" > "+rotationX+" / "+rotationY+" / "+rotationZ);
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
			//GL11.glPopMatrix();
			//v5.addTranslation(-offsetX*f5, -offsetY*f5, -offsetZ*f5);
		}

	}

}

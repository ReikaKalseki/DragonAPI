/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.input.Keyboard;

public class LODModelPart extends ModelRenderer {

	private float size = -1;

	public LODModelPart(ModelBase baseModel, int textureX, int textureZ) {
		super(baseModel, textureX, textureZ);
	}

	@Override
	public final ModelRenderer addBox(String par1Str, float par2, float par3, float par4, int par5, int par6, int par7)
	{
		if (cubeList.isEmpty()) {
			super.addBox(par1Str, par2, par3, par4, par5, par6, par7);
			size = this.calculateVolume();
		}
		else {
			throw new UnsupportedOperationException("You may only have one box per model piece!");
		}
		return this;
	}

	@Override
	public final ModelRenderer addBox(float par1, float par2, float par3, int par4, int par5, int par6)
	{
		if (cubeList.isEmpty()) {
			super.addBox(par1, par2, par3, par4, par5, par6);
			size = this.calculateVolume();
		}
		else {
			throw new UnsupportedOperationException("You may only have one box per model piece!");
		}
		return this;
	}

	/**
	 * Creates a textured box. Args: originX, originY, originZ, width, height, depth, scaleFactor.
	 */
	@Override
	public final void addBox(float par1, float par2, float par3, int par4, int par5, int par6, float par7)
	{
		if (cubeList.isEmpty()) {
			super.addBox(par1, par2, par3, par4, par5, par6, par7);
			size = this.calculateVolume();
		}
		else {
			throw new UnsupportedOperationException("You may only have one box per model piece!");
		}
	}

	protected ModelBox getBox() {
		return (ModelBox)cubeList.get(0);
	}

	private float calculateVolume() {
		ModelBox box = this.getBox();
		float x = box.posX1-box.posX2;
		float y = box.posY1-box.posY2;
		float z = box.posZ1-box.posZ2;
		return Math.abs(x*y*z);
	}


	public float getVolume() {
		return size;
	}

	public boolean shouldRender(double dist_squared) {
		if (Keyboard.isKeyDown(Keyboard.KEY_TAB))
			return true;
		if (size <= 0)
			return false;
		if (dist_squared < 96) {
			return true;
		}
		if (dist_squared < 128) {
			return this.getVolume() > 4;
		}
		else if (dist_squared < 256) {
			return this.getVolume() > 8;
		}
		else if (dist_squared < 1024) {
			return this.getVolume() > 32;
		}
		else if (dist_squared < 2048) {
			return this.getVolume() > 128;
		}
		else if (dist_squared < 4096) {
			return this.getVolume() > 512;
		}
		else {
			return this.getVolume() > 1024;
		}
	}

	public void render(TileEntity te, float pixelSize)
	{
		double rx = RenderManager.renderPosX;
		double ry = RenderManager.renderPosY;
		double rz = RenderManager.renderPosZ;
		double dx = rx-te.xCoord;
		double dy = ry-te.yCoord;
		double dz = rz-te.zCoord;
		double d = dx*dx+dy*dy+dz*dz;

		if (this.shouldRender(d) || !te.hasWorldObj())
			super.render(pixelSize);
	}

}

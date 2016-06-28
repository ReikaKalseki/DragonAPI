/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.input.Keyboard;
import reika.dragonapi.libraries.io.ReikaRenderHelper;
import reika.dragonapi.libraries.io.ReikaRenderHelper.RenderDistance;

public class LODModelPart extends ModelRenderer {

	private double renderDistanceSqr = -1;
	private double lastDistance;
	private long lastTime;
	private TileEntity lastLocation;

	public LODModelPart(ModelBase baseModel, int textureX, int textureZ) {
		super(baseModel, textureX, textureZ);
	}

	@Override
	public final ModelRenderer addBox(String par1Str, float par2, float par3, float par4, int par5, int par6, int par7)
	{
		if (cubeList.isEmpty()) {
			super.addBox(par1Str, par2, par3, par4, par5, par6, par7);
			float size = this.calculateVolume();
			renderDistanceSqr = this.calculateRenderDistance(size);
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
			float size = this.calculateVolume();
			renderDistanceSqr = this.calculateRenderDistance(size);
		}
		else {
			throw new UnsupportedOperationException("You may only have one box per model piece!");
		}
		return this;
	}

	@Override
	public final void addBox(float par1, float par2, float par3, int par4, int par5, int par6, float par7)
	{
		if (cubeList.isEmpty()) {
			super.addBox(par1, par2, par3, par4, par5, par6, par7);
			float size = this.calculateVolume();
			renderDistanceSqr = this.calculateRenderDistance(size);
		}
		else {
			throw new UnsupportedOperationException("You may only have one box per model piece!");
		}
	}

	protected final ModelBox getBox() {
		return (ModelBox)cubeList.get(0);
	}

	public final float calculateVolume() {
		ModelBox box = this.getBox();
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
		return d;
	}

	public final boolean shouldRender(double dist_squared) {
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || renderDistanceSqr*this.getDistanceMultiplier() >= dist_squared;
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

	public final void render(TileEntity te, float pixelSize)
	{
		double d = this.calcAndCacheRenderDistance(te);

		if (!te.hasWorldObj() || MinecraftForgeClient.getRenderPass() == -1 || this.shouldRender(d)) {
			super.render(pixelSize);
		}
	}

	private double calcAndCacheRenderDistance(TileEntity te) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		long time = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
		if (te == lastLocation && time == lastTime) {
			return lastDistance;
		}
		else {
			double rx = ep.posX;
			double ry = ep.posY;
			double rz = ep.posZ;
			double dx = rx-te.xCoord-0.5;
			double dy = ry-te.yCoord-0.5;
			double dz = rz-te.zCoord-0.5;
			double d = dx*dx+dy*dy+dz*dz;
			lastLocation = te;
			lastTime = time;
			lastDistance = d;
			return d;
		}
	}

}

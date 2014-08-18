/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityRenderBase extends TileEntitySpecialRenderer {

	protected final ForgeDirection[] dirs = ForgeDirection.values();

	public final boolean isValidMachineRenderpass(TileEntityBase te) {
		if (!te.isInWorld())
			return true;/*
		int b = 0;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int c = te.worldObj.getLightBrightnessForSkyBlocks(te.xCoord+dir.offsetX, te.yCoord+dir.offsetY, te.zCoord+dir.offsetZ, 0);
			b = Math.max(c, b);
		}
		if (te.hasWorldObj()) {
			int j = b % 65536;
			int k = b / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
		}*/
		int pass = MinecraftForgeClient.getRenderPass();
		return pass == 0;//(te.shouldRenderInPass(pass));
	}

	public abstract String getTextureFolder();

	public final void bindTextureByName(String tex) {
		ReikaTextureHelper.bindTexture(this.getModClass(), tex);
	}

	public final void bindImageByName(String img) {
		ReikaTextureHelper.bindTexture(this.getModClass(), this.getTextureFolder()+img);
	}

	protected abstract Class getModClass();

	protected final FontRenderer getFontRenderer() {
		return this.func_147498_b();
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Auxiliary.Trackers.ModLockController;
import Reika.DragonAPI.IO.ReikaImageLoader;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public abstract class TileEntityRenderBase extends TileEntitySpecialRenderer {

	protected final ForgeDirection[] dirs = ForgeDirection.values();

	private final HashMap<String, String> textureOverrides = new HashMap();

	public final boolean isValidMachineRenderPass(TileEntityBase te) {
		if (!te.isInWorld() || StructureRenderer.isRenderingTiles())
			return true;
		if (!ModLockController.instance.verify(this.getOwnerMod()))
			return false;
		/*
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

	protected abstract boolean doRenderModel(TileEntityBase te);

	public abstract String getTextureFolder();

	public final void bindTextureByName(String tex) {
		String over = textureOverrides.get(tex);
		if (over != null) {
			ReikaTextureHelper.bindTexture(this.getModClass(), over);
			return;
		}
		if (this.loadXmasTextures()) {
			String xmas = tex.replace(".png", "")+"_xmas.png";
			BufferedImage ret = ReikaImageLoader.readImage(this.getModClass(), xmas, null);
			String bind = ret != null && ret != ReikaImageLoader.getMissingTex() ? xmas : tex;
			textureOverrides.put(tex, bind);
			this.bindTextureByName(bind);
			return;
		}
		ReikaTextureHelper.bindTexture(this.getModClass(), tex);
	}

	protected boolean loadXmasTextures() {
		return false;
	}

	public final void bindImageByName(String img) {
		ReikaTextureHelper.bindTexture(this.getModClass(), this.getTextureFolder()+img);
	}

	protected abstract DragonAPIMod getOwnerMod();
	protected abstract Class getModClass();

	protected final FontRenderer getFontRenderer() {
		return this.func_147498_b() != null ? this.func_147498_b() : Minecraft.getMinecraft().fontRenderer;
	}

	@Override
	public abstract void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8);

}

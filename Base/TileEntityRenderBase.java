/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public abstract class TileEntityRenderBase extends TileEntitySpecialRenderer {

	protected final ForgeDirection[] dirs = ForgeDirection.values();

	public final boolean isValidMachineRenderpass(TileEntityBase te) {
		if (!te.isInWorld())
			return true;
		int pass = MinecraftForgeClient.getRenderPass();
		return pass == 0;//(te.shouldRenderInPass(pass));
	}

	public abstract String getTextureFolder();

	public final void bindTextureByName(String tex) {
		ReikaTextureHelper.bindTexture(this.getModClass(), tex);
	}

	public final void bindDirectTextureByName(String tex) {
		ReikaTextureHelper.bindDirectTexture(this.getModClass(), tex);
	}

	public final void bindImageByName(String img) {
		ReikaTextureHelper.bindTexture(this.getModClass(), this.getTextureFolder()+img);
	}

	protected abstract Class getModClass();

}

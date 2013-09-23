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

public abstract class TileEntityRenderBase extends TileEntitySpecialRenderer {

	protected final ForgeDirection[] dirs = ForgeDirection.values();

	public final boolean isValidMachineRenderpass(TileEntityBase te) {
		if (!te.isInWorld())
			return true;
		int pass = MinecraftForgeClient.getRenderPass();
		return (te.shouldRenderInPass(pass));
	}

	public abstract String getTextureFolder();

}

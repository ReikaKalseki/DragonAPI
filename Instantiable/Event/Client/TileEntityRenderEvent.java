/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityRenderEvent extends Event {

	public final TileEntity tileEntity;
	public final double renderPosX;
	public final double renderPosY;
	public final double renderPosZ;
	public final float partialTickTime;

	public TileEntityRenderEvent(TileEntity te, double par2, double par4, double par6, float par8) {
		tileEntity = te;
		renderPosX = par2;
		renderPosY = par4;
		renderPosZ = par6;
		partialTickTime = par8;
	}

}

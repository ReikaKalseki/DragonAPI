/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class TileUpdateEvent extends TileEntityEvent {

	private TileUpdateEvent(TileEntity te) {
		super(te);
	}

	public static boolean fire(TileEntity te) {
		if (te.isInvalid() || !te.hasWorldObj() || !te.worldObj.blockExists(te.xCoord, te.yCoord, te.zCoord))
			return true;
		return MinecraftForge.EVENT_BUS.post(new TileUpdateEvent(te));
	}

}

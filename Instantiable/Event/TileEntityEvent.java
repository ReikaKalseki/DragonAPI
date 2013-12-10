/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.Event;

public class TileEntityEvent extends Event {

	public final TileEntity tile;

	public TileEntityEvent(TileEntity te) {
		tile = te;
	}

}

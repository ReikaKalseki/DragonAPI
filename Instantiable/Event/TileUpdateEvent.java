/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class TileUpdateEvent extends TileEntityEvent {

	public TileUpdateEvent(TileEntity te) {
		super(te);

		ReikaJavaLibrary.pConsole("Updating "+te);
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;

public interface TileModel {

	public void renderAll(TileEntity tile, ArrayList li);

}

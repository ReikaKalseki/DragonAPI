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

import net.minecraft.world.World;
import Reika.DragonAPI.Interfaces.Registry.CropHandler;

public abstract class CropHandlerBase extends ModHandlerBase implements CropHandler {

	public void editTileDataForHarvest(World world, int x, int y, int z) {}


}

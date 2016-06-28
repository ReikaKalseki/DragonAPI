/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.base;

import net.minecraft.world.World;
import reika.dragonapi.interfaces.registry.CropHandler;

public abstract class CropHandlerBase extends ModHandlerBase implements CropHandler {

	public void editTileDataForHarvest(World world, int x, int y, int z) {}


}

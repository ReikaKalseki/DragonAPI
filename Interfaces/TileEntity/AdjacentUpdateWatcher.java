/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.TileEntity;

import net.minecraft.block.Block;
import net.minecraft.world.World;


public interface AdjacentUpdateWatcher {

	public void onAdjacentUpdate(World world, int x, int y, int z, Block b);

}

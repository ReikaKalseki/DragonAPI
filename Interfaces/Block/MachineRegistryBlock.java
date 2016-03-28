/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Block;

import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;


public interface MachineRegistryBlock {

	public TileEnum getMachine(IBlockAccess world, int x, int y, int z);

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces.block;

import net.minecraft.world.IBlockAccess;
import reika.dragonapi.interfaces.registry.TileEnum;


public interface MachineRegistryBlock {

	public TileEnum getMachine(IBlockAccess world, int x, int y, int z);

}

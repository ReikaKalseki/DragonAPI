/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces.tileentity;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;


public interface RedstoneTile {

	int getStrongPower(IBlockAccess world, int x, int y, int z, ForgeDirection side);
	int getWeakPower(IBlockAccess world, int x, int y, int z, ForgeDirection side);

}

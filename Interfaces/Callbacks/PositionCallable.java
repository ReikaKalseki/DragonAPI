/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Callbacks;

import net.minecraft.world.IBlockAccess;


public interface PositionCallable<V> {

	public V call(IBlockAccess world, int x, int y, int z);

}

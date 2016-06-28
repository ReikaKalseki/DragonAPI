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

import net.minecraft.world.World;

public interface SemiUnbreakable {

	public boolean isUnbreakable(World world, int x, int y, int z, int meta);

}

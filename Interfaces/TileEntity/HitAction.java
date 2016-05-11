/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.TileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface HitAction {

	public void onHit(World world, int x, int y, int z, EntityPlayer ep);

}

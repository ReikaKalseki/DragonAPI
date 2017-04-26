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

public interface PlayerBreakHook {

	/** Return false to cancel the block break. */
	public boolean breakByPlayer(EntityPlayer ep);

	public boolean isBreakable(EntityPlayer ep);

}

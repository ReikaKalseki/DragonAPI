/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public abstract class ProgressContainer extends CoreContainer {

	public ProgressContainer(EntityPlayer player, TileEntity te) {
		super(player, te);
	}

	@Override
	public abstract void detectAndSendChanges();

	@Override
	public abstract void updateProgressBar(int index, int value);

}

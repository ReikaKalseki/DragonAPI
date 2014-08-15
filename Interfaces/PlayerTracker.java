/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.player.EntityPlayer;

public interface PlayerTracker {

	public void onNewPlayer(EntityPlayer ep);

	/** This MUST be unique! */
	public String getID();
}
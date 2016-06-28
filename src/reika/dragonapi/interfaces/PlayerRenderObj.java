/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import reika.dragonapi.auxiliary.trackers.PlayerSpecificRenderer.PlayerRotationData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface PlayerRenderObj {

	/** Render starts centered on eye position */
	public void render(EntityPlayer ep, float ptick, PlayerRotationData dat);

	/** Lower numbers render first. Use high numbers (>> 0) for transparency */
	public int getRenderPriority();

}

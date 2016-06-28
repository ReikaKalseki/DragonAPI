/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class PlayerOpenGuiEvent extends Event {

	public final EntityPlayer player;
	public final Object mod;
	public final World world;
	public final int posX;
	public final int posY;
	public final int posZ;

	public PlayerOpenGuiEvent(EntityPlayer ep, Object md, World w, int x, int y, int z) {
		player = ep;
		mod = md;
		world = w;
		posX = x;
		posY = y;
		posZ = z;
	}

}

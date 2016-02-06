/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;


public class PlayerInteractEventClient extends PlayerEvent {

	public final Action action;
	public final int x;
	public final int y;
	public final int z;
	public final int face;
	public final World world;

	public PlayerInteractEventClient(EntityPlayer player, Action action, int x, int y, int z, int face, World world) {
		super(player);
		this.action = action;
		this.x = x;
		this.y = y;
		this.z = z;
		this.face = face;
		this.world = world;
	}

}

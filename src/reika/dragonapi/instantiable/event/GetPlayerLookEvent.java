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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;


public class GetPlayerLookEvent extends PlayerEvent {

	public final MovingObjectPosition originalLook;

	public final Vec3 playerVec;
	public final Vec3 auxVec;

	public MovingObjectPosition newLook;

	public GetPlayerLookEvent(EntityPlayer ep, MovingObjectPosition mov, Vec3 v1, Vec3 v2) {
		super(ep);
		originalLook = mov;
		newLook = mov;

		playerVec = v1;
		auxVec = v2;
	}

}

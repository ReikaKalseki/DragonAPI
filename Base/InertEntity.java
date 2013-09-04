/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public abstract class InertEntity extends Entity {

	public InertEntity(World par1World) {
		super(par1World);
		noClip = true;
	}

	/**
	 * Gets called every tick from main Entity class
	 */
	@Override
	public void onEntityUpdate()
	{
		worldObj.theProfiler.startSection("entityBaseTick");

		prevDistanceWalkedModified = distanceWalkedModified;
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		prevRotationPitch = rotationPitch;
		prevRotationYaw = rotationYaw;

		timeInPortal  = 0;

		if (posY < -64.0D)
			this.setDead();

		worldObj.theProfiler.endSection();

		this.moveEntity(motionX, motionY, motionZ);
	}

	@Override
	public boolean isEntityInvulnerable()
	{
		return true;
	}

	@Override
	public void applyEntityCollision(Entity par1Entity) {}

}

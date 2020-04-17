/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.AI;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import Reika.DragonAPI.Instantiable.MotionTracker;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.ComparableAI;

public class AITaskSeekLocation extends EntityAIBase implements ComparableAI
{
	protected final EntityLiving entity;
	private final Coordinate target;
	protected final double speed;

	private MotionTracker motionTracker = new MotionTracker(20, 5); //5s, in 1/4-second steps

	private boolean isStuck;

	public AITaskSeekLocation(EntityLiving e, double sp, Coordinate c)
	{
		entity = e;
		target = c;
		speed = sp;
		this.setMutexBits(0);
	}

	@Override
	public boolean shouldExecute() {
		return true;
	}

	@Override
	public final boolean continueExecuting() {
		return !entity.getNavigator().noPath();
	}

	@Override
	public void updateTask() {
		motionTracker.update(entity);
		boolean wasStuck = isStuck;
		isStuck = false;
		if (motionTracker.getLastMoved() > 40 || motionTracker.getTotalTravelDistanceSince(20) < 1) { //2s with no movement or 5s with < 1 blocks movement
			isStuck = true;
		}
		if (!isStuck) {
			Vec3 vec = entity.getLookVec();
			int idx = MathHelper.floor_double(entity.posX+vec.xCoord);
			int idy = MathHelper.floor_double(entity.posY+vec.yCoord);
			int idz = MathHelper.floor_double(entity.posZ+vec.zCoord);
			Block b = entity.worldObj.getBlock(idx, idy, idz);
			if (b.getMaterial().blocksMovement() && b.getCollisionBoundingBoxFromPool(entity.worldObj, idx, idy, idz) != null) {
				isStuck = true;
			}
		}
		if (isStuck && !wasStuck) {
			//entity.getNavigator().tryMoveToXYZ(entity.posX, entity.posY+4, entity.posZ, speed);
			entity.getNavigator().clearPathEntity();
			entity.setPositionAndUpdate(entity.posX, entity.posY+2, entity.posZ);
		}
		else if (!isStuck && wasStuck) {
			this.startExecuting();
		}
	}

	@Override
	public final void startExecuting() {
		double dx = target.xCoord+0.5;
		double dy = target.yCoord;
		double dz = target.zCoord+0.5;
		dy += target.getDistanceTo(entity)/32D;
		entity.getNavigator().tryMoveToXYZ(dx, dy, dz, speed);
	}

	@Override
	public boolean match(ComparableAI ai) {
		return ai instanceof AITaskSeekLocation && ((AITaskSeekLocation)ai).target.equals(target);
	}
}

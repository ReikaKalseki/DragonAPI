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

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.ComparableAI;

public class AITaskAvoidLocation extends EntityAIBase implements ComparableAI
{
	protected final EntityLiving entity;
	private final Coordinate target;
	protected final double speed;

	public AITaskAvoidLocation(EntityLiving e, double sp, Coordinate c)
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
	public final void startExecuting() {
		double dx = entity.posX-(target.xCoord+0.5-entity.posX);
		double dy = entity.posY-(target.yCoord-entity.posY);
		double dz = entity.posZ-(target.zCoord+0.5-entity.posZ);
		entity.getNavigator().tryMoveToXYZ(dx, dy, dz, speed);
	}

	@Override
	public boolean match(ComparableAI ai) {
		return ai instanceof AITaskAvoidLocation && ((AITaskAvoidLocation)ai).target.equals(target);
	}
}

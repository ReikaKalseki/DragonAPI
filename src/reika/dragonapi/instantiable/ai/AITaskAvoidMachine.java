/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import reika.dragonapi.instantiable.data.immutable.Coordinate;
import reika.dragonapi.interfaces.tileentity.MobRepellent;

public class AITaskAvoidMachine extends AITaskAvoidLocation {

	private final MobRepellent tile;

	public AITaskAvoidMachine(EntityLiving e, double sp, MobRepellent te) {
		super(e, sp, new Coordinate((TileEntity)te));
		tile = te;
	}

	@Override
	public final boolean shouldExecute() {
		return tile.canRepel(entity);
	}

}

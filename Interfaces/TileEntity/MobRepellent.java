/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.TileEntity;

import net.minecraft.entity.EntityLiving;

public interface MobRepellent {

	public boolean canRepel(EntityLiving e);

}

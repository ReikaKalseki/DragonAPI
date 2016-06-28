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

import net.minecraft.entity.Entity;


public interface MotionController {

	public void update(Entity e);
	public double getMotionX(Entity e);
	public double getMotionY(Entity e);
	public double getMotionZ(Entity e);

}

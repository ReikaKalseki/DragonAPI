/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.Entity;


public interface PositionController {

	public void update(Entity e);
	public double getPositionX(Entity e);
	public double getPositionY(Entity e);
	public double getPositionZ(Entity e);

}

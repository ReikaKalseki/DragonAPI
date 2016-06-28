/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data.immutable;

import net.minecraftforge.common.util.ForgeDirection;

public final class ScaledDirection {

	public final int offsetX;
	public final int offsetY;
	public final int offsetZ;

	public final int distance;
	public final ForgeDirection direction;

	public ScaledDirection(ForgeDirection dir, int dist) {
		direction = dir;
		distance = dist;

		offsetX = dir.offsetX*dist;
		offsetY = dir.offsetY*dist;
		offsetZ = dir.offsetZ*dist;
	}

}

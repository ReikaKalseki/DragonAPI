/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Base;

import net.minecraft.world.World;


public abstract class WorldPositionEvent extends PositionEventBase {

	public final World world;

	public WorldPositionEvent(World world, int x, int y, int z) {
		super(world, x, y, z);
		this.world = world;
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;


public class BlockUpdateEvent extends WorldPositionEvent {

	public final boolean renderOnly;

	public BlockUpdateEvent(World world, int x, int y, int z, boolean render) {
		super(world, x, y, z);
		renderOnly = render;
	}

}

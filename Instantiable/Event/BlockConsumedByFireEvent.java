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
import cpw.mods.fml.common.eventhandler.Cancelable;

/** Fired right before a block is burned by fire. Cancel it to stop the consumption. */
@Cancelable
public class BlockConsumedByFireEvent extends PositionEvent {

	public BlockConsumedByFireEvent(World world, int x, int y, int z) {
		super(world, x, y, z);
	}

}

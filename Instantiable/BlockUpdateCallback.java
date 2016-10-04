/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.TimerMap.TimerCallback;


public class BlockUpdateCallback implements TimerCallback {

	public final WorldLocation location;

	public BlockUpdateCallback(World world, int x, int y, int z) {
		this(new WorldLocation(world, x, y, z));
	}

	public BlockUpdateCallback(TileEntity te) {
		this(new WorldLocation(te));
	}

	public BlockUpdateCallback(WorldLocation loc) {
		location = loc;
	}

	@Override
	public void call() {
		location.triggerBlockUpdate(false);
	}

}

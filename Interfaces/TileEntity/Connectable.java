/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.TileEntity;

import net.minecraft.world.World;

import Reika.DragonAPI.Interfaces.Location;

public interface Connectable<L extends Location> extends BreakAction {

	public boolean isEmitting();

	public void reset();

	public void resetOther();

	public L getConnection();

	public boolean hasValidConnection();

	public boolean tryConnect(World world, int x, int y, int z);

}

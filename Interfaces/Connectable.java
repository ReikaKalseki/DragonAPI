/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import net.minecraft.world.World;

public interface Connectable {

	public boolean isEmitting();

	public void reset();

	public void resetOther();

	public boolean setSource(World world, int x, int y, int z);

	public boolean setTarget(World world, int x, int y, int z);

}

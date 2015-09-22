/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Registry;

import net.minecraft.world.World;


public interface OreEnum {

	public int getHarvestLevel();
	public String getHarvestTool();
	public float getXPDropped(World world, int x, int y, int z);
	public boolean dropsSelf(World world, int x, int y, int z);
	public boolean enforceHarvestLevel();

}

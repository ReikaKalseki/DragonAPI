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

import java.util.Random;

import net.minecraft.world.World;

public interface RetroactiveGenerator {

	/** Note that the chunkX and chunkZ are the real x and z coordinates of the chunk origin, not chunk coordinates */
	public void generate(Random rand, World world, int chunkX, int chunkZ);

	/** Note that the chunkX and chunkZ are the real x and z coordinates of the chunk origin, not chunk coordinates */
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ);

	/** It would be a good idea to prefix this with your mod's name; eg ReactorCraft_PitchblendeGen */
	public String getIDString();

}

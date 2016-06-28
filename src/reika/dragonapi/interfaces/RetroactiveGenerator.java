/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces;

import java.util.Random;

import net.minecraft.world.World;
import cpw.mods.fml.common.IWorldGenerator;

public interface RetroactiveGenerator extends IWorldGenerator {

	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ);

	/** It would be a good idea to prefix this with your mod's name; eg ReactorCraft_PitchblendeGen */
	public String getIDString();

}

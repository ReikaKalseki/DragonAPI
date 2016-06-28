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
import reika.dragonapi.interfaces.registry.OreEnum;


public interface OreGenerator {

	public void generateOre(OreEnum ore, Random random, World world, int chunkX, int chunkZ);

}

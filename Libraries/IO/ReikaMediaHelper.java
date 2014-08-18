/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import Reika.DragonAPI.DragonAPICore;

import net.minecraft.world.World;

public final class ReikaMediaHelper extends DragonAPICore {

	public static void playSoundVarying(World world, int x, int y, int z, String name) {
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, name, rand.nextFloat(), rand.nextFloat());
	}

}

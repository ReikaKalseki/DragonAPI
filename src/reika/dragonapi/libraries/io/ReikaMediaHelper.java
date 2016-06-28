/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries.io;

import net.minecraft.world.World;
import reika.dragonapi.DragonAPICore;

public final class ReikaMediaHelper extends DragonAPICore {

	public static void playSoundVarying(World world, int x, int y, int z, String name) {
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, name, rand.nextFloat(), rand.nextFloat());
	}

}

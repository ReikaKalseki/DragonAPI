/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.deepinteract;

import java.lang.reflect.Field;
import java.util.HashSet;

import net.minecraft.block.Block;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.auxiliary.trackers.ReflectiveFailureTracker;
import reika.dragonapi.instantiable.BasicModEntry;
import cpw.mods.fml.common.Loader;

public class TimeTorchHelper {

	private static HashSet<Class> tileBlacklist;
	private static HashSet<Block> blockBlacklist;

	public static void blacklistTileEntity(Class c) {
		if (tileBlacklist != null) {
			tileBlacklist.add(c);
		}
	}

	public static void blacklistBlock(Block b) {
		if (blockBlacklist != null) {
			blockBlacklist.add(b);
		}
	}

	static {
		if (Loader.isModLoaded("Torcherino")) {
			try {
				Class c = null;
				try {
					c = Class.forName("com.sci.torcherino.TorcherinoRegistry");
				}
				catch (ClassNotFoundException e) {
					c = Class.forName("com.sci.torcherino.tile.TileTorcherino");
				}
				Field f = c.getDeclaredField("blacklistedTiles");
				f.setAccessible(true);
				tileBlacklist = (HashSet<Class>)f.get(null);

				f = c.getDeclaredField("blacklistedBlocks");
				f.setAccessible(true);
				blockBlacklist = (HashSet<Block>)f.get(null);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not load Time Torch blacklisting!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(new BasicModEntry("Torcherino"), e);
			}
		}
	}

}

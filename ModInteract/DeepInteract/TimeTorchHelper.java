/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Field;
import java.util.HashSet;

import net.minecraft.block.Block;
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
				Class c = Class.forName("com.sci.torcherino.tile.TileTorcherino");
				Field f = c.getDeclaredField("blacklistedTiles");
				f.setAccessible(true);
				tileBlacklist = (HashSet<Class>)f.get(null);

				f = c.getDeclaredField("blacklistedBlocks");
				f.setAccessible(true);
				blockBlacklist = (HashSet<Block>)f.get(null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

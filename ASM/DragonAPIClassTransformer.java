/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.util.Collection;

import net.minecraft.item.Item;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Auxiliary.CoreModDetection;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry;
import Reika.DragonAPI.Exception.ASMException;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class DragonAPIClassTransformer implements IClassTransformer {

	private static final MultiMap<String, Patcher> classes = new MultiMap(CollectionType.HASHSET).setNullEmpty();
	private static int bukkitFlags;
	private static boolean nullItemPrintout = false;
	private static boolean nullItemCrash = false;
	private static boolean init = false;

	public static boolean doLightUpdate(World world, int x, int y, int z) {
		if (WorldGenInterceptionRegistry.skipLighting)
			return false;

		boolean flag = false;

		if (!world.provider.hasNoSky) {
			flag |= world.updateLightByType(EnumSkyBlock.Sky, x, y, z);
		}
		flag |= world.updateLightByType(EnumSkyBlock.Block, x, y, z);

		return flag;
	}

	public static void updateSetBlockRelight(Chunk c, int x, int y, int z, int flags) {
		if ((flags & 8) == 0 && !WorldGenInterceptionRegistry.skipLighting) {
			c.relightBlock(x, y, z);
		}
	}

	public static boolean updateSetBlockLighting(int x, int y, int z, World world, int flags) {
		if ((flags & 8) == 0 && !WorldGenInterceptionRegistry.skipLighting) {
			return /*CoreModDetection.fastCraftInstalled() ? (boolean)ReikaReflectionHelper.cacheAndInvokeMethod("fastcraft.J", "d", world, x, y, z) : */world.func_147451_t(x, y, z);
		}
		else {
			return false;
		}
	}

	public static void validateItemStack(Item i) {
		if (i == null) {
			if (nullItemCrash || nullItemPrintout) {
				String s = "A mod created an ItemStack of a null item.\n";
				s += "Though somewhat common, this is a very bad practice as such ItemStacks crash almost immediately upon even basic use.\n";
				s += "Check the Stacktrace for the mod code coming before ItemStack.func_150996_a or ItemStack.<init>.\n";
				s += "Notify the developer of that mod.\n";
				s += "Though it is possible that in this case, the mod was not going to do anything with the item, such stacks are commonly\n";
				s += "registered the OreDictionary, dropped as entities, or added to dungeon loot tables, resulting in crashes in other mods.\n";
				s += "As a result, and the fact that a null-item stack is never necessary, it should be avoided in all cases.\n";
				if (nullItemCrash) {
					s += "You can turn this crash off in the DragonAPI configs, but you would likely crash anyways, usually soon afterward.";
					throw new IllegalStateException(s);
				}
				else {
					s += "You can disable this printout with a JVM argument, but doing so is not recommended.";
					ReikaASMHelper.logError(s);
					Thread.dumpStack();
				}
			}
		}
	}

	@Override
	public byte[] transform(String className, String className2, byte[] opcodes) {
		if (!classes.isEmpty()) {
			Collection<Patcher> c = classes.get(className);
			if (c != null) {

				if (!init) {
					ReikaJavaLibrary.initClass(CoreModDetection.class);
					init = true;
				}

				ReikaASMHelper.activeMod = "DragonAPI";
				for (Patcher p : c) {
					ReikaASMHelper.log("Running patcher "+p);
					try {
						opcodes = p.apply(opcodes);
					}
					catch (ASMException e) {
						if (p.isExceptionThrowing())
							throw e;
						else {
							ReikaASMHelper.logError("ASM ERROR IN "+p+":");
							e.printStackTrace();
						}
					}
				}
				classes.remove(className); //for maximizing performance
				ReikaASMHelper.activeMod = null;
			}
		}
		return opcodes;
	}

	static {
		MultiMap<String, Patcher> map = ReikaASMHelper.getPatchers("DragonAPI", "Reika.DragonAPI.ASM.Patchers");
		classes.putAll(map);

		bukkitFlags = BukkitBitflags.calculateFlags();
		nullItemPrintout = !ReikaJVMParser.isArgumentPresent("-DragonAPI_noNullItemPrint");
	}

	public static int getBukkitFlags() {
		return bukkitFlags;
	}

	public static enum BukkitBitflags {
		CAULDRON("kcauldron.KCauldron"),
		THERMOS("thermos.Thermos");

		private final String className;
		public final int flag;

		private static final BukkitBitflags[] list = values();

		private BukkitBitflags(String s) {
			className = s;
			flag = 1 << this.ordinal();
		}

		private boolean test() {
			try {
				return Class.forName(className) != null;
			}
			catch (ClassNotFoundException e) {
				return false;
			}
		}

		private static int calculateFlags() {
			int flags = 0;
			for (int i = 0; i < list.length; i++) {
				BukkitBitflags b = list[i];
				if (b.test()) {
					flags = flags | b.flag;
				}
			}
			return flags;
		}
	}
}

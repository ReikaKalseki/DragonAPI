/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.lang.reflect.Modifier;
import java.util.Collection;

import net.minecraft.item.Item;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenMutated;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.BiomeDictionary;
import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Exception.ASMException;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class DragonAPIClassTransformer implements IClassTransformer {

	private static final MultiMap<String, Patcher> classes = new MultiMap(new HashSetFactory()).setNullEmpty();
	private static int bukkitFlags;
	private static boolean nullItemPrintout = false;
	private static boolean nullItemCrash = false;

	public static boolean updateSetBlockLighting(int x, int y, int z, World world, int flags) {
		if ((flags & 8) == 0) {
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

	public static void registerPermutedBiomesToDictionary() { //Kept here to prevent premature init of ReikaBiomeHelper
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			if (b instanceof BiomeGenMutated) {
				BiomeGenBase parent = ((BiomeGenMutated)b).baseBiome;
				BiomeDictionary.registerBiomeType(b, BiomeDictionary.getTypesForBiome(parent));
			}
		}
	}

	@Override
	public byte[] transform(String className, String className2, byte[] opcodes) {
		if (!classes.isEmpty()) {
			Collection<Patcher> c = classes.get(className);
			if (c != null) {
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
		try {
			Collection<Class> li = ReikaJavaLibrary.getAllClassesFromPackage("Reika.DragonAPI.ASM.Patchers");
			for (Class c : li) {
				if ((c.getModifiers() & Modifier.ABSTRACT) == 0 && Patcher.class.isAssignableFrom(c)) {
					try {
						Patcher p = (Patcher)c.newInstance();
						if (p.isEnabled()) {
							String s = !FMLForgePlugin.RUNTIME_DEOBF ? p.deobfName : p.obfName;
							classes.addValue(s, p);
						}
						else {
							ReikaASMHelper.log("******************************************************************************************");
							ReikaASMHelper.log("WARNING: ASM TRANSFORMER '"+p+"' HAS BEEN DISABLED. THIS CAN BREAK MANY THINGS.");
							ReikaASMHelper.log("IF THIS TRANSFORMER HAS BEEN DISABLED WITHOUT GOOD REASON, TURN IT BACK ON IMMEDIATELY!");
							ReikaASMHelper.log("******************************************************************************************");
						}
					}
					catch (Exception e) {
						throw new RuntimeException("Could not create DragonAPI ASM handler "+c, e);
					}
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Could not find DragonAPI ASM handlers", e);
		}

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

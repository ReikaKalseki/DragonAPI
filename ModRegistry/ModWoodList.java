/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModRegistry;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Auxiliary.APIRegistry;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;

public enum ModWoodList { //look through treecapitator config?

	CANOPY(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", 1, Block.class),
	DARKWOOD(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", 3, Block.class),
	MANGROVE(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", 2, Block.class),
	TWILIGHTOAK(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", 0, Block.class),
	GREATWOOD(APIRegistry.THAUMCRAFT, "thaumcraft.common.Config", "blockMagicalLog", 0, Block.class),
	SILVERWOOD(APIRegistry.THAUMCRAFT, "thaumcraft.common.Config", "blockMagicalLog", 1, Block.class),
	EUCALYPTUS(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", 0, Block.class),
	//BIGREDWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "redwood", 0, Block.class),
	SAKURA(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", 1, Block.class),
	GHOSTWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", 2, Block.class),
	HOPSEED(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", 3, Block.class),
	DARKNATURA(APIRegistry.NATURA, "mods.natura.common.NContent", "darkTree", 0, Block.class),
	BLOODWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "bloodwood", 0, Block.class),
	FUSEWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "darkTree", 1, Block.class),
	TIGERWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", 3, Block.class),
	SILVERBELL(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", 1, Block.class),
	MAPLE(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", 0, Block.class),
	WILLOW(APIRegistry.NATURA, "mods.natura.common.NContent", "willow", 0, Block.class),
	AMARANTH(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", 2, Block.class),
	REDWOOD(APIRegistry.BOP, null, null, 0, Block.class),
	ACACIA(APIRegistry.BOP, null, null, 0, Block.class),
	JACARANDA(APIRegistry.BOP, null, null, 0, Block.class),
	AUTUMN(APIRegistry.BXL, null, null, 0, Block.class),
	FIR(APIRegistry.BXL, null, null, 0, Block.class),
	XLREDWOOD(APIRegistry.BXL, null, null, 0, Block.class),
	RUBBER(APIRegistry.INDUSTRIALCRAFT, "ic2.core.Ic2Items", "rubberWood", 0, ItemStack.class),
	MINERUBBER(APIRegistry.MINEFACTORY, "powercrystals.minefactoryreloaded.MineFactoryReloadedCore", "rubberWoodBlock", 0, Block.class);

	private APIRegistry mod;
	private int blockID;
	private int blockMeta;
	private boolean hasPlanks;

	private String varName;
	private Class containerClass;

	private boolean exists;

	public static final ModWoodList[] woodList = ModWoodList.values();

	private ModWoodList(APIRegistry req, String className, String blockVar, int meta, Class type) {
		try {
			Class cl = Class.forName(className);
			Field f = cl.getField(blockVar);
			int id;
			if (type == ItemStack.class) {
				ItemStack is = (ItemStack)f.get(null);
				id = is.itemID;
			}
			else if (type == Block.class) {
				Block b = (Block)f.get(null);
				id = b.blockID;
			}
			else if (type == Integer.class) {
				id = f.getInt(null);
			}
			else {
				ReikaJavaLibrary.pConsole("Error loading wood "+this);
				ReikaJavaLibrary.pConsole("Invalid variable type for "+f);
				return;
			}
			blockID = id;
			blockMeta = meta;
			ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully loaded wood "+this);
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("Error loading wood "+this);
			e.printStackTrace();
		}
		catch (NoSuchFieldException e) {
			ReikaJavaLibrary.pConsole("Error loading wood "+this);
			e.printStackTrace();
		}
		catch (SecurityException e) {
			ReikaJavaLibrary.pConsole("Error loading wood "+this);
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			ReikaJavaLibrary.pConsole("Error loading wood "+this);
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			ReikaJavaLibrary.pConsole("Error loading wood "+this);
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return this.name()+" from "+mod;
	}

	private void readFromMod() {

	}

	public boolean exists() {
		return exists;
	}

	public ItemStack getItem() {
		return new ItemStack(blockID, 1, blockMeta);
	}

	public Block getBlock() {
		return Block.blocksList[blockID];
	}

	public static boolean isModWood(ItemStack block) {
		return false;
	}

}

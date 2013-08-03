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
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Auxiliary.APIRegistry;
import Reika.DragonAPI.Libraries.ReikaItemHelper;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;

public enum ModWoodList { //look through treecapitator config?

	CANOPY(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", "leaves", 1, Block.class),
	DARKWOOD(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", "leaves", 3, Block.class),
	MANGROVE(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", "leaves", 2, Block.class),
	TWILIGHTOAK(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", "leaves", 0, Block.class),
	GREATWOOD(APIRegistry.THAUMCRAFT, "thaumcraft.common.Config", "blockMagicalLog", "blockMagicalLog", 0, Block.class),
	SILVERWOOD(APIRegistry.THAUMCRAFT, "thaumcraft.common.Config", "blockMagicalLog", "blockMagicalLog", 1, Block.class),
	EUCALYPTUS(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", "floraLeaves", 0, Block.class),
	//BIGREDWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "redwood", 0, Block.class),
	SAKURA(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", "floraLeaves", 1, Block.class),
	GHOSTWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", "floraLeaves", 2, Block.class),
	HOPSEED(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", "floraLeaves", 3, Block.class),
	DARKNATURA(APIRegistry.NATURA, "mods.natura.common.NContent", "darkTree", "darkLeaves", 0, Block.class),
	BLOODWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "bloodwood", "bloodwood", 0, 1, Block.class),
	FUSEWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "darkTree", "darkLeaves", 1, Block.class),
	TIGERWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", "rareLeaves", 3, Block.class),
	SILVERBELL(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", "rareLeaves", 1, Block.class),
	MAPLE(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", "rareLeaves", 0, Block.class),
	WILLOW(APIRegistry.NATURA, "mods.natura.common.NContent", "willow", "willow", 0, 1, Block.class),
	AMARANTH(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", "rareLeaves", 2, Block.class),
	REDWOOD(APIRegistry.BOP, null, null, null, 0, Block.class),
	ACACIA(APIRegistry.BOP, null, null, null, 0, Block.class),
	JACARANDA(APIRegistry.BOP, null, null, null, 0, Block.class),
	AUTUMN(APIRegistry.BXL, null, null, null, 0, Block.class),
	FIR(APIRegistry.BXL, null, null, null, 0, Block.class),
	XLREDWOOD(APIRegistry.BXL, null, null, null, 0, Block.class),
	RUBBER(APIRegistry.INDUSTRIALCRAFT, "ic2.core.Ic2Items", "rubberWood", "rubberLeaves", 0, ItemStack.class),
	MINERUBBER(APIRegistry.MINEFACTORY, "powercrystals.minefactoryreloaded.MineFactoryReloadedCore", "rubberWoodBlock", "rubberLeavesBlock", 0, Block.class);

	private APIRegistry mod;
	private int blockID = -1;
	private int leafID = -1;
	private int blockMeta = -1;
	private int leafMeta = -1;
	private boolean hasPlanks;

	private String varName;
	private Class containerClass;

	private boolean exists;

	public static final ModWoodList[] woodList = ModWoodList.values();

	private ModWoodList(APIRegistry req, String className, String blockVar, String leafVar, int meta, Class type) {
		this(req, className, blockVar, leafVar, meta, meta, type);
	}

	private ModWoodList(APIRegistry req, String className, String blockVar, String leafVar, int meta, int metaleaf, Class type) {
		mod = req;
		if (!mod.conditionsMet())
			return;
		if (className == null || className.isEmpty()) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this+": Empty parent class");
			return;
		}
		if (blockVar == null || blockVar.isEmpty()) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this+": Empty variable name");
			return;
		}
		if (leafVar == null || leafVar.isEmpty()) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading leaves for wood "+this+": Empty variable name");
			return;
		}
		try {
			Class cl = Class.forName(className);
			Field w = cl.getField(blockVar);
			Field l = cl.getField(leafVar);
			int id;
			int idleaf;
			if (type == ItemStack.class) {
				ItemStack wood = (ItemStack)w.get(null);
				ItemStack leaf = (ItemStack)l.get(null);
				id = wood.itemID;
				idleaf = leaf.itemID;
			}
			else if (type == Block.class) {
				Block wood = (Block)w.get(null);
				Block leaf = (Block)l.get(null);
				id = wood.blockID;
				idleaf = leaf.blockID;
			}
			else if (type == Integer.class) {
				id = w.getInt(null);
				idleaf = l.getInt(null);
			}
			else {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this);
				ReikaJavaLibrary.pConsole("DRAGONAPI: Invalid variable type for "+w+" or "+l);
				return;
			}
			blockID = id;
			blockMeta = meta;
			leafID = idleaf;
			leafMeta = metaleaf;
			ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully loaded wood "+this);
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this);
			e.printStackTrace();
		}
		catch (NoSuchFieldException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this);
			e.printStackTrace();
		}
		catch (SecurityException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this);
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this);
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this);
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return this.name()+" from "+mod;
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

	public static ModWoodList getModWood(ItemStack block) {
		for (int i = 0; i < woodList.length; i++) {
			if (ReikaItemHelper.matchStacks(block, woodList[i].getItem()))
				return woodList[i];
		}
		return null;
	}

	public static boolean isModWood(ItemStack block) {
		return getModWood(block) != null;
	}

	public Icon getWoodIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return this.getBlock().getBlockTexture(iba, x, y, z, s);
	}

	public Icon getSideIcon() {
		return this.getBlock().getBlockTextureFromSide(2);
	}

	public EntityFallingSand getFallingBlock(World world, int x, int y, int z) {
		EntityFallingSand e = new EntityFallingSand(world, x+0.5, y+0.5, z+0.5, blockID, blockMeta);
		return e;
	}

	public ItemStack getCorrespondingLeaf() {
		return new ItemStack(leafID, 1, leafMeta);
	}

}

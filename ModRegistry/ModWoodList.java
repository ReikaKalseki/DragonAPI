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
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public enum ModWoodList { //look through treecapitator config?

	CANOPY(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", "leaves", "sapling", new int[]{1,13}, 1, 1, Block.class),
	DARKWOOD(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", "hedge", "sapling", new int[]{3,15}, 1, 3, Block.class),
	MANGROVE(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", "leaves", "sapling", new int[]{2,14}, new int[]{2,10}, 2, Block.class),
	TWILIGHTOAK(APIRegistry.TWILIGHT, "twilightforest.block.TFBlocks", "log", "leaves", "sapling", new int[]{0,12}, 0, 0, Block.class),
	GREATWOOD(APIRegistry.THAUMCRAFT, "thaumcraft.common.Config", "blockMagicalLog", "blockMagicalLeaves", "blockCustomPlant", new int[]{0,4,8}, new int[]{0,8}, 0, Block.class),
	SILVERWOOD(APIRegistry.THAUMCRAFT, "thaumcraft.common.Config", "blockMagicalLog", "blockMagicalLeaves", "blockCustomPlant", new int[]{1,5,9}, new int[]{1,9}, 1, Block.class),
	EUCALYPTUS(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", "floraLeaves", "floraSapling", 0, new int[]{1,9}, 1, Block.class),
	SEQUOIA(APIRegistry.NATURA, "mods.natura.common.NContent", "redwood", "floraLeaves", "floraSapling", new int[]{0,1,2}, new int[]{0,8}, 0, Block.class),
	SAKURA(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", "floraLeavesNoColor", "floraSapling", new int[]{1,5,9}, new int[]{0,8}, 3, Block.class),
	GHOSTWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", "floraLeavesNoColor", "floraSapling", new int[]{2,6,10}, new int[]{1,9}, 4, Block.class),
	HOPSEED(APIRegistry.NATURA, "mods.natura.common.NContent", "tree", "floraLeaves", "floraSapling", 3, new int[]{2,10}, 2, Block.class),
	DARKNATURA(APIRegistry.NATURA, "mods.natura.common.NContent", "darkTree", "darkLeaves", "floraSapling", 0, new int[]{0,1,2,8,9,10}, 6, Block.class),
	BLOODWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "bloodwood", "floraLeavesNoColor", "floraSapling", new int[]{0,1,2,3,4,5,15}, new int[]{2,10}, 5, Block.class),
	FUSEWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "darkTree", "darkLeaves", "floraSapling", 1, new int[]{3,11}, 7, Block.class),
	TIGERWOOD(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", "rareLeaves", "rareSapling", 3, new int[]{3,11}, 3, Block.class),
	SILVERBELL(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", "rareLeaves", "rareSapling", 1, new int[]{1,9}, 1, Block.class),
	MAPLE(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", "rareLeaves", "rareSapling", 0, new int[]{0,8}, 0, Block.class),
	WILLOW(APIRegistry.NATURA, "mods.natura.common.NContent", "willow", "floraLeavesNoColor", "rareSapling", 0, new int[]{3,11}, 4, Block.class),
	AMARANTH(APIRegistry.NATURA, "mods.natura.common.NContent", "rareTree", "rareLeaves", "rareSapling", 2, new int[]{2,10}, 2, Block.class),
	REDWOOD(APIRegistry.BOP, null, null, null, null, 0, Block.class),
	ACACIA(APIRegistry.BOP, null, null, null, null, 0, Block.class),
	JACARANDA(APIRegistry.BOP, null, null, null, null, 0, Block.class),
	AUTUMN(APIRegistry.BXL, null, null, null, null, 0, Block.class),
	FIR(APIRegistry.BXL, null, null, null, null, 0, Block.class),
	XLREDWOOD(APIRegistry.BXL, null, null, null, null, 0, Block.class),
	RUBBER(APIRegistry.INDUSTRIALCRAFT, "ic2.core.Ic2Items", "rubberWood", "rubberLeaves", "rubberSapling", new int[]{1,2,3,4,5}, 0, 0, ItemStack.class),
	MINERUBBER(APIRegistry.MINEFACTORY, "powercrystals.minefactoryreloaded.MineFactoryReloadedCore", "rubberWoodBlock", "rubberLeavesBlock", "rubberSaplingBlock", new int[]{0,1,2,3,4,5}, new int[]{0,8}, 0, Block.class);

	private APIRegistry mod;
	private int blockID = -1;
	private int leafID = -1;
	private int blockMeta[];
	private int leafMeta[];
	private boolean hasPlanks;

	private int saplingID;
	private int saplingMeta;

	private String varName;
	private Class containerClass;

	private boolean exists;

	public static final ModWoodList[] woodList = ModWoodList.values();

	private ModWoodList(APIRegistry req, String className, String blockVar, String leafVar, String saplingVar, int meta, int metaleaf, int metasapling, Class type) {
		this(req, className, blockVar, leafVar, saplingVar, new int[]{meta}, new int[]{metaleaf}, metasapling, type);
	}

	private ModWoodList(APIRegistry req, String className, String blockVar, String leafVar, String saplingVar, int meta, Class type) {
		this(req, className, blockVar, leafVar, saplingVar, new int[]{meta}, new int[]{meta}, meta, type);
	}

	private ModWoodList(APIRegistry req, String className, String blockVar, String leafVar, String saplingVar, int[] meta, int metaleaf, int metasapling, Class type) {
		this(req, className, blockVar, leafVar, saplingVar, meta, new int[]{metaleaf}, metasapling, type);
	}

	private ModWoodList(APIRegistry req, String className, String blockVar, String leafVar, String saplingVar, int meta, int[] metaleaf, int metasapling, Class type) {
		this(req, className, blockVar, leafVar, saplingVar, new int[]{meta}, metaleaf, metasapling, type);
	}

	private ModWoodList(APIRegistry req, String className, String blockVar, String leafVar, String saplingVar, int[] meta, int[] metaleaf, int metasapling, Class type) {
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
			Field s = cl.getField(saplingVar);
			int id;
			int idleaf;
			int idsapling;
			if (type == ItemStack.class) {
				ItemStack wood = (ItemStack)w.get(null);
				ItemStack leaf = (ItemStack)l.get(null);
				ItemStack sapling = (ItemStack)s.get(null);
				id = wood.itemID;
				idleaf = leaf.itemID;
				idsapling = sapling.itemID;
			}
			else if (type == Block.class) {
				Block wood = (Block)w.get(null);
				Block leaf = (Block)l.get(null);
				Block sapling = (Block)s.get(null);
				id = wood.blockID;
				idleaf = leaf.blockID;
				idsapling = sapling.blockID;
			}
			else if (type == Integer.class) {
				id = w.getInt(null);
				idleaf = l.getInt(null);
				idsapling = s.getInt(null);
			}
			else {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this);
				ReikaJavaLibrary.pConsole("DRAGONAPI: Invalid variable type for "+w+" or "+l);
				return;
			}
			blockID = id;
			blockMeta = new int[meta.length];
			System.arraycopy(meta, 0, blockMeta, 0, meta.length);
			leafID = idleaf;
			leafMeta = new int[metaleaf.length];
			System.arraycopy(metaleaf, 0, leafMeta, 0, metaleaf.length);
			saplingID = idsapling;
			saplingMeta = metasapling;
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
		return new ItemStack(blockID, 1, blockMeta[0]);
	}

	public ItemStack getLogItemWithOffset(int i) {
		return new ItemStack(blockID, 1, blockMeta[i]);
	}

	public boolean isLogBlock(ItemStack block) {
		if (blockMeta == null)
			return false;
		if (this == SEQUOIA) {
			return block.itemID == blockID;
		}
		for (int i = 0; i < blockMeta.length; i++) {
			if (ReikaItemHelper.matchStacks(block, this.getLogItemWithOffset(i)))
				return true;
		}
		return false;
	}

	public Block getBlock() {
		return Block.blocksList[blockID];
	}

	public static ModWoodList getModWood(int id, int meta) {
		return getModWood(new ItemStack(id, 1, meta));
	}

	public static ModWoodList getModWood(ItemStack block) {
		for (int i = 0; i < woodList.length; i++) {
			if (woodList[i].isLogBlock(block))
				return woodList[i];
		}
		return null;
	}

	public static ModWoodList getModWoodFromSapling(ItemStack block) {
		for (int i = 0; i < woodList.length; i++) {
			if (ReikaItemHelper.matchStacks(block, woodList[i].getCorrespondingSapling()))
				return woodList[i];
		}
		return null;
	}

	public static ModWoodList getModWoodFromLeaf(ItemStack block) {
		for (int i = 0; i < woodList.length; i++) {
			if (woodList[i].leafMeta != null) {
				for (int k = 0; k < woodList[i].leafMeta.length; k++) {
					if (ReikaItemHelper.matchStacks(block, woodList[i].getCorrespondingDamagedLeaf(k)))
						return woodList[i];
				}
			}
		}
		return null;
	}

	public static ModWoodList getModWoodFromLeaf(int id, int meta) {
		return getModWoodFromLeaf(new ItemStack(id, 1, meta));
	}

	public static boolean isModWood(ItemStack block) {
		return getModWood(block) != null;
	}

	public static boolean isModWood(int id, int meta) {
		return getModWood(id, meta) != null;
	}

	public static boolean isModLeaf(ItemStack block) {
		return getModWoodFromLeaf(block) != null;
	}

	public static boolean isModSapling(ItemStack block) {
		return getModWoodFromSapling(block) != null;
	}

	public Icon getWoodIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return this.getBlock().getBlockTexture(iba, x, y, z, s);
	}

	public Icon getSideIcon() {
		return this.getBlock().getBlockTextureFromSide(2);
	}

	public EntityFallingSand getFallingBlock(World world, int x, int y, int z) {
		EntityFallingSand e = new EntityFallingSand(world, x+0.5, y+0.5, z+0.5, blockID, blockMeta[0]);
		return e;
	}

	public ItemStack getCorrespondingLeaf() {
		return new ItemStack(leafID, 1, leafMeta[0]);
	}

	public ItemStack getCorrespondingDamagedLeaf(int i) {
		return new ItemStack(leafID, 1, leafMeta[i]);
	}

	public ItemStack getCorrespondingSapling() {
		return new ItemStack(saplingID, 1, saplingMeta);
	}

}

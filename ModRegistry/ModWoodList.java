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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public enum ModWoodList {

	CANOPY(ModList.TWILIGHT, "log", "leaves", "sapling", new int[]{1,13}, 1, 1, VarType.BLOCK),
	DARKWOOD(ModList.TWILIGHT, "log", "hedge", "sapling", new int[]{3,15}, 1, 3, VarType.BLOCK),
	MANGROVE(ModList.TWILIGHT, "log", "leaves", "sapling", new int[]{2,14}, new int[]{2,10}, 2, VarType.BLOCK),
	TWILIGHTOAK(ModList.TWILIGHT, "log", "leaves", "sapling", new int[]{0,12}, 0, 0, VarType.BLOCK),
	GREATWOOD(ModList.THAUMCRAFT, "blockMagicalLogId", "blockMagicalLeavesId", "blockCustomPlantId", new int[]{0,4,8}, new int[]{0,8}, 0, VarType.INT),
	SILVERWOOD(ModList.THAUMCRAFT, "blockMagicalLogId", "blockMagicalLeavesId", "blockCustomPlantId", new int[]{1,5,9}, new int[]{1,9}, 1, VarType.INT),
	EUCALYPTUS(ModList.NATURA, "tree", "floraLeaves", "floraSapling", 0, new int[]{1,9}, 1, VarType.BLOCK),
	SEQUOIA(ModList.NATURA, "redwood", "floraLeaves", "floraSapling", new int[]{0,1,2}, new int[]{0,8}, 0, VarType.BLOCK),
	SAKURA(ModList.NATURA, "tree", "floraLeavesNoColor", "floraSapling", new int[]{1,5,9}, new int[]{0,8}, 3, VarType.BLOCK),
	GHOSTWOOD(ModList.NATURA, "tree", "floraLeavesNoColor", "floraSapling", new int[]{2,6,10}, new int[]{1,9}, 4, VarType.BLOCK),
	HOPSEED(ModList.NATURA, "tree", "floraLeaves", "floraSapling", 3, new int[]{2,10}, 2, VarType.BLOCK),
	DARKNATURA(ModList.NATURA, "darkTree", "darkLeaves", "floraSapling", 0, new int[]{0,1,2,8,9,10}, 6, VarType.BLOCK),
	BLOODWOOD(ModList.NATURA, "bloodwood", "floraLeavesNoColor", "floraSapling", new int[]{0,1,2,3,4,5,15}, new int[]{2,10}, 5, VarType.BLOCK),
	FUSEWOOD(ModList.NATURA, "darkTree", "darkLeaves", "floraSapling", 1, new int[]{3,11}, 7, VarType.BLOCK),
	TIGERWOOD(ModList.NATURA, "rareTree", "rareLeaves", "rareSapling", 3, new int[]{3,11}, 3, VarType.BLOCK),
	SILVERBELL(ModList.NATURA, "rareTree", "rareLeaves", "rareSapling", 1, new int[]{1,9}, 1, VarType.BLOCK),
	MAPLE(ModList.NATURA, "rareTree", "rareLeaves", "rareSapling", 0, new int[]{0,8}, 0, VarType.BLOCK),
	WILLOW(ModList.NATURA, "willow", "floraLeavesNoColor", "rareSapling", 0, new int[]{3,11}, 4, VarType.BLOCK),
	AMARANTH(ModList.NATURA, "rareTree", "rareLeaves", "rareSapling", 2, new int[]{2,10}, 2, VarType.BLOCK),
	BAMBOO(ModList.BOP, "bambooID", "leaves1ID", "saplingsID", 0, 1, 2, VarType.INT),
	MAGIC(ModList.BOP, "logs2ID", "leaves1ID", "saplingsID", 1, new int[]{2,10}, 3, VarType.INT),
	DARK(ModList.BOP, "logs1ID", "leaves1ID", "saplingsID", 2, new int[]{3,11}, 4, VarType.INT),
	FIR(ModList.BOP, "logs1ID", "leaves1ID", "saplingsID", 3, new int[]{5,13}, 6, VarType.INT),
	LOFT(ModList.BOP, "logs2ID", "leaves1ID", "saplingsID", 0, new int[]{6,14}, 7, VarType.INT),
	CHERRY(ModList.BOP, "logs1ID", "leaves2ID", "saplingsID", new int[]{1,5,9}, new int[]{1,3,9,11}, 10, VarType.INT), //sapling 12 for white cherry
	HELLBARK(ModList.BOP, "logs4ID", "leaves2ID", "saplingsID", 1, 4, 13, VarType.INT),
	JACARANDA(ModList.BOP, "logs4ID", "leaves2ID", "saplingsID", 2, 5, 14, VarType.INT),
	ACACIA(ModList.BOP, "logs1ID", "colourizedLeavesID", "colourizedSaplingsID", 0, new int[]{0,8}, 0, VarType.INT),
	BOPMANGROVE(ModList.BOP, "logs2ID", "colourizedLeavesID", "colourizedSaplingsID", 2, new int[]{1,9}, 1, VarType.INT),
	PALM(ModList.BOP, "logs2ID", "colourizedLeavesID", "colourizedSaplingsID", 3, 2, 2, VarType.INT),
	REDWOOD(ModList.BOP, "logs3ID", "colourizedLeavesID", "colourizedSaplingsID", 0, new int[]{3,11}, 3, VarType.INT),
	BOPWILLOW(ModList.BOP, "logs3ID", "colourizedLeavesID", "colourizedSaplingsID", 1, new int[]{4,12}, 4, VarType.INT),
	PINE(ModList.BOP, "logs4ID", "colourizedLeavesID", "colourizedSaplingsID", 0, 5, 5, VarType.INT),
	XLREDWOOD(ModList.BXL, null, null, null, 0, VarType.BLOCK),
	RUBBER(ModList.INDUSTRIALCRAFT, "rubberWood", "rubberLeaves", "rubberSapling", new int[]{1,2,3,4,5}, 0, 0, VarType.ITEMSTACK),
	MINERUBBER(ModList.MINEFACTORY, "rubberWoodBlock", "rubberLeavesBlock", "rubberSaplingBlock", new int[]{0,1,2,3,4,5}, new int[]{0,8}, 0, VarType.BLOCK),
	TIMEWOOD(ModList.TWILIGHT, "magicLog", "magicLeaves", "sapling", new int[]{0,12}, new int[]{1,8}, 5, VarType.BLOCK),
	TRANSFORMATION(ModList.TWILIGHT, "magicLog", "magicLeaves", "sapling", new int[]{1,13}, new int[]{1,9}, 6, VarType.BLOCK),
	MINEWOOD(ModList.TWILIGHT, "magicLog", "magicLeaves", "sapling", new int[]{2,14}, new int[]{2,10}, 7, VarType.BLOCK),
	SORTING(ModList.TWILIGHT, "magicLog", "magicLeaves", "sapling", new int[]{3,15}, new int[]{3,11}, 8, VarType.BLOCK);

	private ModList mod;
	private int blockID = -1;
	private int leafID = -1;
	private int blockMeta[];
	private int leafMeta[];
	private boolean hasPlanks;

	private int saplingID;
	private int saplingMeta;

	private String varName;
	private Class containerClass;

	private boolean exists = false;

	public static final ModWoodList[] woodList = values();

	private ModWoodList(ModList req, int blockID, int leafID, int saplingID, VarType type) {
		this(req, blockID, leafID, saplingID, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}, 0, type);
	}

	private ModWoodList(ModList req, int blockID, int leafID, int saplingID, int[] metalog, int[] metaleaf, int metasapling, VarType type) {

	}

	private ModWoodList(ModList req, String blockVar, String leafVar, String saplingVar, int meta, int metaleaf, int metasapling, VarType type) {
		this(req, blockVar, leafVar, saplingVar, new int[]{meta}, new int[]{metaleaf}, metasapling, type);
	}

	private ModWoodList(ModList req, String blockVar, String leafVar, String saplingVar, int meta, VarType type) {
		this(req, blockVar, leafVar, saplingVar, new int[]{meta}, new int[]{meta}, meta, type);
	}

	private ModWoodList(ModList req, String blockVar, String leafVar, String saplingVar, int[] meta, int metaleaf, int metasapling, VarType type) {
		this(req, blockVar, leafVar, saplingVar, meta, new int[]{metaleaf}, metasapling, type);
	}

	private ModWoodList(ModList req, String blockVar, String leafVar, String saplingVar, int meta, int[] metaleaf, int metasapling, VarType type) {
		this(req, blockVar, leafVar, saplingVar, new int[]{meta}, metaleaf, metasapling, type);
	}

	private ModWoodList(ModList req, String blockVar, String leafVar, String saplingVar, int[] meta, int[] metaleaf, int metasapling, VarType type) {
		mod = req;
		if (!mod.isLoaded()) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Not loading "+this.getLabel()+": Mod not present.");
			return;
		}
		Class cl = req.getBlockClass();
		//ReikaJavaLibrary.pConsole("DRAGONAPI: Attempting to load "+this.getLabel()+". Data parameters:");
		ReikaJavaLibrary.pConsole(cl+", "+blockVar+", "+leafVar+", "+saplingVar+", "+type);
		if (cl == null) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this.getLabel()+": Empty block class");
			return;
		}
		if (blockVar == null || blockVar.isEmpty()) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this.getLabel()+": Empty variable name");
			return;
		}
		if (leafVar == null || leafVar.isEmpty()) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading leaves for wood "+this.getLabel()+": Empty variable name");
			return;
		}
		try {
			Field w = cl.getField(blockVar);
			Field l = cl.getField(leafVar);
			Field s = cl.getField(saplingVar);
			int id;
			int idleaf;
			int idsapling;
			switch(type) {
			case ITEMSTACK:
				ItemStack wood = (ItemStack)w.get(null);
				ItemStack leaf = (ItemStack)l.get(null);
				ItemStack sapling = (ItemStack)s.get(null);
				if (wood == null || leaf == null || sapling == null) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading "+this.getLabel()+": Block not instantiated!");
					return;
				}
				id = wood.itemID;
				idleaf = leaf.itemID;
				idsapling = sapling.itemID;
				break;
			case BLOCK:
				Block wood_b = (Block)w.get(null);
				Block leaf_b = (Block)l.get(null);
				Block sapling_b = (Block)s.get(null);
				if (wood_b == null || leaf_b == null || sapling_b == null) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading "+this.getLabel()+": Block not instantiated!");
					return;
				}
				id = wood_b.blockID;
				idleaf = leaf_b.blockID;
				idsapling = sapling_b.blockID;
				break;
			case INT:
				id = w.getInt(null);
				idleaf = l.getInt(null);
				idsapling = s.getInt(null);
				break;
			default:
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this.getLabel());
				ReikaJavaLibrary.pConsole("DRAGONAPI: Invalid variable type "+type+" for "+w+" or "+l);
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
			ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully loaded wood "+this.getLabel());
			exists = true;
		}
		catch (NoSuchFieldException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this.getLabel());
			e.printStackTrace();
		}
		catch (SecurityException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this.getLabel());
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this.getLabel());
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this.getLabel());
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name());
		sb.append(" from ");
		sb.append(mod);
		if (exists) {
			sb.append(" (LOG "+blockID+":"+Arrays.toString(blockMeta)+";");
			sb.append(" ");
			sb.append("LEAF "+leafID+":"+Arrays.toString(leafMeta)+";");
			sb.append(" ");
			sb.append("SAPLING "+saplingID+":"+saplingMeta);
			sb.append(")");
		}
		else {
			sb.append(" (Not loaded)");
		}
		return sb.toString();
	}

	public String getLabel() {
		return this.name()+" from "+this.getParentMod();
	}

	public boolean exists() {
		return exists && this.getParentMod().isLoaded();
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

	public List<Integer> getLogMetadatas() {
		List<Integer> li = new ArrayList();
		for (int i = 0; i < blockMeta.length; i++)
			li.add(blockMeta[i]);
		return li;
	}

	public List<Integer> getLeafMetadatas() {
		List<Integer> li = new ArrayList();
		for (int i = 0; i < leafMeta.length; i++)
			li.add(leafMeta[i]);
		return li;
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
				//ReikaJavaLibrary.pConsole(woodList[i]+" - "+woodList[i].getCorrespondingLeaf().itemID+":"+Arrays.toString(woodList[i].leafMeta));
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

	public ModList getParentMod() {
		return mod;
	}

	public boolean isRareTree() {
		if (this == TIMEWOOD)
			return true;
		if (this == SORTING)
			return true;
		if (this == MINEWOOD)
			return true;
		if (this == TRANSFORMATION)
			return true;
		if (this == ModWoodList.SILVERWOOD)
			return true;
		return false;
	}

	static enum VarType {
		ITEMSTACK(),
		BLOCK(),
		INT();

		@Override
		public String toString() {
			return "Variable Type "+ReikaStringParser.capFirstChar(this.name());
		}
	}
}

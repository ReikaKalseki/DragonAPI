/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Interfaces.TreeType;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public enum ModWoodList implements TreeType {

	CANOPY(ModList.TWILIGHT, 0x252517, 0x330464, "log", "leaves", "sapling", new int[]{1,13}, 1, 1, VarType.INSTANCE),
	DARKWOOD(ModList.TWILIGHT, 0x35281A, 0x395F41, "log", "hedge", "sapling", new int[]{3,15}, 1, 3, VarType.INSTANCE),
	MANGROVE(ModList.TWILIGHT, 0x8D8980, 0x431445, "log", "leaves", "sapling", new int[]{2,14}, new int[]{2,10}, 2, VarType.INSTANCE),
	TWILIGHTOAK(ModList.TWILIGHT, 0x806654, 0x764952, "log", "leaves", "sapling", new int[]{0,12}, 0, 0, VarType.INSTANCE),
	GREATWOOD(ModList.THAUMCRAFT, 0x4F3E37, 0x71924C, "blockMagicalLog", "blockMagicalLeaves", "blockCustomPlant", new int[]{0,4,8}, new int[]{0,8}, 0, VarType.INSTANCE),
	SILVERWOOD(ModList.THAUMCRAFT, 0xC9C3AC, 0x5782C7, "blockMagicalLog", "blockMagicalLeaves", "blockCustomPlant", new int[]{1,5,9}, new int[]{1,9}, 1, VarType.INSTANCE),
	EUCALYPTUS(ModList.NATURA, 0xE2CEB1, 0x3C9119, "tree", "floraLeaves", "floraSapling", 0, new int[]{1,9}, 1, VarType.INSTANCE),
	SEQUOIA(ModList.NATURA, 0x8C7162, 0x3C9119, "redwood", "floraLeaves", "floraSapling", new int[]{0,1,2}, new int[]{0,8}, 0, VarType.INSTANCE),
	SAKURA(ModList.NATURA, 0x703C02, 0xEB7F98, "tree", "floraLeavesNoColor", "floraSapling", new int[]{1,5,9}, new int[]{0,8}, 3, VarType.INSTANCE),
	GHOSTWOOD(ModList.NATURA, 0xB3B3B3, 0xEEE6D1, "tree", "floraLeavesNoColor", "floraSapling", new int[]{2,6,10}, new int[]{1,9}, 4, VarType.INSTANCE),
	HOPSEED(ModList.NATURA, 0x9F8661, 0x3C9119, "tree", "floraLeaves", "floraSapling", 3, new int[]{2,10}, 2, VarType.INSTANCE),
	NATURADARKWOOD(ModList.NATURA, 0x234D85, 0x061E4C, "darkTree", "darkLeaves", "floraSapling", 0, new int[]{0,1,2,8,9,10}, 6, VarType.INSTANCE),
	BLOODWOOD(ModList.NATURA, 0x8D4F05, 0xB10000, "bloodwood", "floraLeavesNoColor", "floraSapling", new int[]{0,1,2,3,4,5,15}, new int[]{2,10}, 5, VarType.INSTANCE),
	FUSEWOOD(ModList.NATURA, 0x2C3E38, 0x28818D, "darkTree", "darkLeaves", "floraSapling", 1, new int[]{3,11}, 7, VarType.INSTANCE),
	TIGERWOOD(ModList.NATURA, 0x544936, 0x5B2900, "rareTree", "rareLeaves", "rareSapling", 3, new int[]{3,11}, 3, VarType.INSTANCE),
	SILVERBELL(ModList.NATURA, 0x939C85, 0x73B849, "rareTree", "rareLeaves", "rareSapling", 1, new int[]{1,9}, 1, VarType.INSTANCE),
	MAPLE(ModList.NATURA, 0x503A23, 0x993412, "rareTree", "rareLeaves", "rareSapling", 0, new int[]{0,8}, 0, VarType.INSTANCE),
	WILLOW(ModList.NATURA, 0x584C30, 0x548941, "willow", "floraLeavesNoColor", "rareSapling", 0, new int[]{3,11}, 4, VarType.INSTANCE),
	AMARANTH(ModList.NATURA, 0x9C8B56, 0x3C9119, "rareTree", "rareLeaves", "rareSapling", 2, new int[]{2,10}, 2, VarType.INSTANCE),
	BAMBOO(ModList.BOP, 0xBBD26C, 0xAFD83B, "bambooID", "leaves1ID", "saplingsID", 0, 1, 2, VarType.INT),
	MAGIC(ModList.BOP, 0x78839E, 0x5687BE, "logs2ID", "leaves1ID", "saplingsID", 1, new int[]{2,10}, 3, VarType.INT),
	DARK(ModList.BOP, 0x664848, 0x312F42, "logs1ID", "leaves1ID", "saplingsID", 2, new int[]{3,11}, 4, VarType.INT),
	FIR(ModList.BOP, 0x675846, 0x518E5F, "logs1ID", "leaves2ID", "saplingsID", 3, new int[]{1,9}, 6, VarType.INT),
	LOFTWOOD(ModList.BOP, 0x817665, 0x3FD994, "logs2ID", "leaves2ID", "saplingsID", 0, new int[]{2,10}, 7, VarType.INT),
	CHERRY(ModList.BOP, 0x965441, 0xFFAFE0, "logs1ID", "leaves3ID", "saplingsID", new int[]{1,5,9}, new int[]{1,3,9,11}, 10, VarType.INT), //sapling 12 for white cherry
	HELLBARK(ModList.BOP, 0xB36F43, 0x7B5E1F, "logs4ID", "leaves4ID", "saplingsID", 1, 0, 13, VarType.INT),
	JACARANDA(ModList.BOP, 0x998177, 0x644F84, "logs4ID", "leaves4ID", "saplingsID", 2, new int[]{1,9}, 14, VarType.INT),
	ACACIA(ModList.BOP, 0x847956, 0x3E981A, "logs1ID", "colourizedLeaves1ID", "colourizedSaplingsID", 0, new int[]{0,8}, 0, VarType.INT),
	BOPMANGROVE(ModList.BOP, 0xDED1B5, 0x3E981A, "logs2ID", "colourizedLeaves1ID", "colourizedSaplingsID", 2, new int[]{1,9}, 1, VarType.INT),
	PALM(ModList.BOP, 0x936B40, 0x3E981A, "logs2ID", "colourizedLeaves1ID", "colourizedSaplingsID", 3, 2, 2, VarType.INT),
	REDWOOD(ModList.BOP, 0x722F0D, 0x3E981A, "logs3ID", "colourizedLeaves1ID", "colourizedSaplingsID", 0, new int[]{3,11}, 3, VarType.INT),
	BOPWILLOW(ModList.BOP, 0x767A47, 0x3E981A, "logs3ID", "colourizedLeaves2ID", "colourizedSaplingsID", 1, new int[]{0,8}, 4, VarType.INT),
	PINE(ModList.BOP, 0x896B4F, 0x3E981A, "logs4ID", "colourizedLeaves2ID", "colourizedSaplingsID", 0, new int[]{1,9}, 5, VarType.INT),
	BXLREDWOOD(ModList.BXL, 0, 0, null, null, null, 0, VarType.INSTANCE),
	IC2RUBBER(ModList.IC2, 0x3C2D20, 0x638143, "rubberWood", "rubberLeaves", "rubberSapling", new int[]{1,2,3,4,5}, 0, 0, VarType.ITEMSTACK),
	MFRRUBBER(ModList.MINEFACTORY, 0x7E5C25, 0x5DC123, "rubberWoodBlock", "rubberLeavesBlock", "rubberSaplingBlock", new int[]{0,1,2,3,4,5}, new int[]{0,8}, 0, VarType.INSTANCE),
	TIMEWOOD(ModList.TWILIGHT, 0x4F301D, 6986775, "magicLog", "magicLeaves", "sapling", new int[]{0,12}, new int[]{1,8}, 5, VarType.INSTANCE),
	TRANSFORMATION(ModList.TWILIGHT, 0x66727F, 7130346, "magicLog", "magicLeaves", "sapling", new int[]{1,13}, new int[]{1,9}, 6, VarType.INSTANCE),
	MINEWOOD(ModList.TWILIGHT, 0xC5A982, 16576836, "magicLog", "magicLeaves", "sapling", new int[]{2,14}, new int[]{2,10}, 7, VarType.INSTANCE),
	SORTING(ModList.TWILIGHT, 0x705835, 3558403, "magicLog", "magicLeaves", "sapling", new int[]{3,15}, new int[]{3,11}, 8, VarType.INSTANCE),
	GLOW(ModList.TRANSITIONAL, 0xE2B87B, 0xFFBC5E, "GlowWood", "GlowLeaf", "GlowSapling", 0, 0, 0, VarType.INSTANCE),
	FORCE(ModList.DARTCRAFT, 0xE0B749, 0xD9B22C, "forceLog", "forceLeaves", "forceSapling", 0, new int[]{0,8}, 0, VarType.INSTANCE),
	HIGHFIR(ModList.HIGHLANDS, 0x77ee77, 0x88dd88, "firWood", "firLeaves", "firSapling", VarType.INSTANCE),
	HIGHACACIA(ModList.HIGHLANDS, 0x77ee77, 0x88dd88, "acaciaWood", "acaciaLeaves", "acaciaSapling", VarType.INSTANCE),
	HIGHREDWOOD(ModList.HIGHLANDS, 0x77ee77, 0x88dd88, "redwoodWood", "redwoodLeaves", "redwoodSapling", VarType.INSTANCE),
	POPLAR(ModList.HIGHLANDS, 0x77ee77, 0x88dd88, "poplarWood", "poplarLeaves", "poplarSapling", VarType.INSTANCE),
	HIGHCANOPY(ModList.HIGHLANDS, 0x77ee77, 0x88dd88, "canopyWood", "canopyLeaves", "canopySapling", VarType.INSTANCE),
	HIGHPALM(ModList.HIGHLANDS, 0x77ee77, 0x88dd88, "palmWood", "palmLeaves", "palmSapling", VarType.INSTANCE),
	IRONWOOD(ModList.HIGHLANDS, 0x77ee77, 0x88dd88, "ironWood", "ironwoodLeaves", "ironwoodSapling", VarType.INSTANCE),
	HIGHMANGROVE(ModList.HIGHLANDS, 0x77ee77, 0x88dd88, "mangroveWood", "mangroveLeaves", "mangroveSapling", VarType.INSTANCE),
	ASH(ModList.HIGHLANDS, 0x77ee77, 0x88dd88, "ashWood", "ashLeaves", "ashSapling", VarType.INSTANCE),
	WITCHWOOD(ModList.ARSMAGICA, 0x584D32, 0x1F4719, "witchwoodLog", "witchwoodLeaves", "witchwoodSapling", VarType.INSTANCE),
	ROWAN(ModList.WITCHERY, 0x374633, 0x9E774D, "blockWitchLog", "blockWitchLeaves", "blockWitchSapling", new int[]{0,4,8}, new int[]{0,8}, 0, VarType.INSTANCE),
	HAWTHORNE(ModList.WITCHERY, 0x656566, 0xC3EEC3, "blockWitchLog", "blockWitchLeaves", "blockWitchSapling", new int[]{2,6,10}, new int[]{2,10}, 2, VarType.INSTANCE),
	ALDER(ModList.WITCHERY, 0x52544C, 0xC3D562, "blockWitchLog", "blockWitchLeaves", "blockWitchSapling", new int[]{1,5,9}, new int[]{1,9}, 1, VarType.INSTANCE);

	private ModList mod;
	private int blockID = -1;
	private int leafID = -1;
	private int blockMeta[];
	private int leafMeta[];
	private boolean hasPlanks;

	private int saplingID;
	private int saplingMeta;

	public final int logColor;
	public final int leafColor;

	private String varName;
	private Class containerClass;

	private boolean exists = false;

	public static final ModWoodList[] woodList = values();

	private static final HashMap<List<Integer>, ModWoodList> logMappings = new HashMap();
	private static final HashMap<List<Integer>, ModWoodList> leafMappings = new HashMap();
	private static final HashMap<List<Integer>, ModWoodList> saplingMappings = new HashMap();

	private ModWoodList(ModList req, int color, int leaf, String blockVar, String leafVar, String saplingVar, VarType type) {
		this(req, color, leaf, blockVar, leafVar, saplingVar, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}, 0, type);
	}

	private ModWoodList(ModList req, int color, int leaf, String blockVar, String leafVar, String saplingVar, int meta, int metaleaf, int metasapling, VarType type) {
		this(req, color, leaf, blockVar, leafVar, saplingVar, new int[]{meta}, new int[]{metaleaf}, metasapling, type);
	}

	private ModWoodList(ModList req, int color, int leaf, String blockVar, String leafVar, String saplingVar, int meta, VarType type) {
		this(req, color, leaf, blockVar, leafVar, saplingVar, new int[]{meta}, new int[]{meta}, meta, type);
	}

	private ModWoodList(ModList req, int color, int leaf, String blockVar, String leafVar, String saplingVar, int[] meta, int metaleaf, int metasapling, VarType type) {
		this(req, color, leaf, blockVar, leafVar, saplingVar, meta, new int[]{metaleaf}, metasapling, type);
	}

	private ModWoodList(ModList req, int color, int leaf, String blockVar, String leafVar, String saplingVar, int meta, int[] metaleaf, int metasapling, VarType type) {
		this(req, color, leaf, blockVar, leafVar, saplingVar, new int[]{meta}, metaleaf, metasapling, type);
	}

	private ModWoodList(ModList req, int color, int leafcolor, String blockVar, String leafVar, String saplingVar, int[] meta, int[] metaleaf, int metasapling, VarType type) {
		if (!DragonAPIInit.canLoadHandlers())
			throw new MisuseException("Accessed registry enum too early! Wait until postInit!");
		mod = req;
		leafColor = leafcolor;
		logColor = color;
		if (!mod.isLoaded()) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Not loading "+this.getLabel()+": Mod not present.");
			return;
		}
		Class cl = req.getBlockClass();
		//ReikaJavaLibrary.pConsole("DRAGONAPI: Attempting to load "+this.getLabel()+". Data parameters:");
		//ReikaJavaLibrary.pConsole(cl+", "+blockVar+", "+leafVar+", "+saplingVar+", "+type);
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
			case INSTANCE:
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
		sb.append(this.getName());
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

	public String getBasicInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getName());
		sb.append(" from ");
		sb.append(mod);
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

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}

	@Override
	public int getLogID() {
		return blockID;
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
		return logMappings.get(Arrays.asList(block.itemID, block.getItemDamage()));
	}

	public static ModWoodList getModWoodFromSapling(ItemStack block) {
		return saplingMappings.get(Arrays.asList(block.itemID, block.getItemDamage()));
	}

	public static ModWoodList getModWoodFromLeaf(ItemStack block) {
		return leafMappings.get(Arrays.asList(block.itemID, block.getItemDamage()));
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

	public static boolean isModLeaf(int id, int meta) {
		return isModLeaf(new ItemStack(id, 1, meta));
	}

	public static boolean isModLeaf(ItemStack block) {
		return getModWoodFromLeaf(block) != null;
	}

	public static boolean isModSapling(ItemStack block) {
		return getModWoodFromSapling(block) != null;
	}

	public static boolean isModSapling(int id, int meta) {
		return isModSapling(new ItemStack(id, 1, meta));
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

	public static ModWoodList getRandomWood(Random rand) {
		ModWoodList wood = woodList[rand.nextInt(woodList.length)];
		while (!wood.exists) {
			wood = woodList[rand.nextInt(woodList.length)];
		}
		return wood;
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
		if (this == SILVERWOOD)
			return true;
		return false;
	}

	public boolean canBePlacedSideways() {
		return this.getLogMetadatas().size() == 3;
	}

	public static enum VarType {
		ITEMSTACK(),
		INSTANCE(),
		INT();

		@Override
		public String toString() {
			return "Variable Type "+ReikaStringParser.capFirstChar(this.name());
		}
	}

	static {
		for (int i = 0; i < woodList.length; i++) {
			ModWoodList w = woodList[i];
			if (w.exists()) {
				int id = w.blockID;
				int leaf = w.leafID;
				int[] metas = w.blockMeta;
				int[] leafmetas = w.leafMeta;
				int sapling = w.saplingID;
				int saplingMeta = w.saplingMeta;
				for (int k = 0; k < metas.length; k++) {
					int meta = metas[k];
					List li = Arrays.asList(id, meta);
					logMappings.put(li, w);
				}
				for (int k = 0; k < leafmetas.length; k++) {
					int meta = leafmetas[k];
					List li = Arrays.asList(leaf, meta);
					leafMappings.put(li, w);
				}
				saplingMappings.put(Arrays.asList(sapling, saplingMeta), w);
			}
		}
	}
}

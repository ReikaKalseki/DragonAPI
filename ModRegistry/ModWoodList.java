/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Interfaces.Registry.TreeType;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.common.registry.GameRegistry;

public enum ModWoodList implements TreeType {

	CANOPY(ModList.TWILIGHT, 		0x252517, 0x330464, 18, 30, "log", "leaves", "sapling", new int[]{1,13}, new int[]{1,9}, 1, VarType.INSTANCE),
	DARKWOOD(ModList.TWILIGHT, 		0x35281A, 0x395F41, 60, 20, "log", "darkleaves", "sapling", new int[]{3,15}, 0, 3, VarType.INSTANCE),
	MANGROVE(ModList.TWILIGHT, 		0x8D8980, 0x431445, 10, 18, "log", "leaves", "sapling", new int[]{2,14}, new int[]{2,10}, 2, VarType.INSTANCE),
	TWILIGHTOAK(ModList.TWILIGHT, 	0x806654, 0x764952, 30, 180, "log", "leaves", "sapling", new int[]{0,12}, new int[]{0,8}, 0, VarType.INSTANCE),
	//RAINBOWOAK(ModList.TWILIGHT, 	0x806654, 0x764952, ?, ?, "---", "leaves", "sapling", new int[]{0,12}, new int[]{3,11}, 9, VarType.INSTANCE),
	GREATWOOD(ModList.THAUMCRAFT, 	0x4F3E37, 0x71924C, 10, 30, "blockMagicalLog", "blockMagicalLeaves", "blockCustomPlant", new int[]{0,4,8}, new int[]{0,8}, 0, VarType.INSTANCE),
	SILVERWOOD(ModList.THAUMCRAFT, 	0xC9C3AC, 0x5782C7, 8, 24, "blockMagicalLog", "blockMagicalLeaves", "blockCustomPlant", new int[]{1,5,9}, new int[]{1,9}, 1, VarType.INSTANCE),
	EUCALYPTUS(ModList.NATURA, 		0xE2CEB1, 0x3C9119, 10, 14, "tree", "floraLeaves", "floraSapling", 0, new int[]{1,9}, 1, VarType.INSTANCE),
	SEQUOIA(ModList.NATURA, 		0x8C7162, 0x3C9119, 50, 250, "redwood", "floraLeaves", "floraSapling", new int[]{0,1,2}, new int[]{0,8}, 0, VarType.INSTANCE),
	SAKURA(ModList.NATURA, 			0x703C02, 0xEB7F98, 16, 30, "tree", "floraLeavesNoColor", "floraSapling", new int[]{1,5,9}, new int[]{0,8}, 3, VarType.INSTANCE),
	GHOSTWOOD(ModList.NATURA, 		0xB3B3B3, 0xEEE6D1, 7, 12, "tree", "floraLeavesNoColor", "floraSapling", new int[]{2,6,10}, new int[]{1,9}, 4, VarType.INSTANCE),
	HOPSEED(ModList.NATURA, 		0x9F8661, 0x3C9119, 10, 7, "tree", "floraLeaves", "floraSapling", 3, new int[]{2,10}, 2, VarType.INSTANCE),
	NATURADARKWOOD(ModList.NATURA, 	0x234D85, 0x061E4C, 7, 12, "darkTree", "darkLeaves", "floraSapling", 0, new int[]{0,1,2,8,9,10}, 6, VarType.INSTANCE),
	BLOODWOOD(ModList.NATURA, 		0x8D4F05, 0xB10000, 14, 16, "bloodwood", "floraLeavesNoColor", "floraSapling", new int[]{0,1,2,3,4,5,15}, new int[]{2,10}, 5, VarType.INSTANCE),
	FUSEWOOD(ModList.NATURA, 		0x2C3E38, 0x28818D, 7, 12, "darkTree", "darkLeaves", "floraSapling", 1, new int[]{3,11}, 7, VarType.INSTANCE),
	TIGERWOOD(ModList.NATURA, 		0x544936, 0x5B2900, 7, 12, "rareTree", "rareLeaves", "rareSapling", 3, new int[]{3,11}, 3, VarType.INSTANCE),
	SILVERBELL(ModList.NATURA, 		0x939C85, 0x73B849, 7, 12, "rareTree", "rareLeaves", "rareSapling", 1, new int[]{1,9}, 1, VarType.INSTANCE),
	MAPLE(ModList.NATURA, 			0x503A23, 0x993412, 7, 12, "rareTree", "rareLeaves", "rareSapling", 0, new int[]{0,8}, 0, VarType.INSTANCE),
	WILLOW(ModList.NATURA, 			0x584C30, 0x548941, 9, 14, "willow", "floraLeavesNoColor", "rareSapling", 0, new int[]{3,11}, 4, VarType.INSTANCE),
	AMARANTH(ModList.NATURA, 		0x9C8B56, 0x3C9119, 6, 20, "rareTree", "rareLeaves", "rareSapling", 2, new int[]{2,10}, 2, VarType.INSTANCE),
	BAMBOO(ModList.BOP, 			0xBBD26C, 0xAFD83B, 5, 20, "bamboo", "leaves1", "saplings", 0, new int[]{1, 5, 9, 13}, 2, VarType.INSTANCE),
	MAGIC(ModList.BOP, 				0x78839E, 0x5687BE, 7, 12, "logs2", "leaves1", "saplings", new int[]{1, 5, 9, 13}, new int[]{2, 6, 10, 14}, 3, VarType.INSTANCE),
	DARK(ModList.BOP, 				0x664848, 0x312F42, 7, 12, "logs1", "leaves1", "saplings", new int[]{2, 6, 10, 14}, new int[]{3, 7, 11, 15}, 4, VarType.INSTANCE),
	FIR(ModList.BOP, 				0x675846, 0x518E5F, 12, 60, "logs1", "leaves2", "saplings", new int[]{3, 7, 11, 15}, new int[]{1, 5, 9, 13}, 6, VarType.INSTANCE),
	LOFTWOOD(ModList.BOP, 			0x817665, 0x3FD994, 8, 16, "logs2", "leaves2", "saplings", new int[]{0, 4, 8, 12}, new int[]{2, 6, 10, 14}, 7, VarType.INSTANCE),
	CHERRY(ModList.BOP, 			0x965441, 0xFFAFE0, 15, 20, "logs1", "leaves3", "saplings", new int[]{1, 5, 9, 13}, new int[]{1, 3, 5, 7, 9, 11, 13, 15}, 10, VarType.INSTANCE), //sapling 12 for white cherry
	HELLBARK(ModList.BOP, 			0xB36F43, 0x7B5E1F, 2, 5, "logs4", "leaves4", "saplings", new int[]{1, 5, 9, 13}, new int[]{0, 4, 8, 12}, 13, VarType.INSTANCE),
	JACARANDA(ModList.BOP, 			0x998177, 0x644F84, 7, 12, "logs4", "leaves4", "saplings", new int[]{2, 6, 10, 14}, new int[]{1, 5, 9, 13}, 14, VarType.INSTANCE),
	SACRED(ModList.BOP, 			0x896B4F, 0x3E981A, 30, 160, "logs1", "colorizedLeaves1", "colorizedSaplings", new int[]{0, 4, 8, 12}, new int[]{0, 4, 8, 12}, 0, VarType.INSTANCE),
	BOPMANGROVE(ModList.BOP, 		0xDED1B5, 0x3E981A, 5, 15, "logs2", "colorizedLeaves1", "colorizedSaplings", new int[]{2, 6, 10, 14}, new int[]{1, 5, 9, 13}, 1, VarType.INSTANCE),
	PALM(ModList.BOP, 				0x936B40, 0x3E981A, 6, 14, "logs2", "colorizedLeaves1", "colorizedSaplings", new int[]{3, 7, 11, 15}, new int[]{2, 6, 10, 14}, 2, VarType.INSTANCE),
	REDWOOD(ModList.BOP, 			0x722F0D, 0x3E981A, 6, 50, "logs3", "colorizedLeaves1", "colorizedSaplings", new int[]{0, 4, 8, 12}, new int[]{3, 7, 11, 15}, 3, VarType.INSTANCE),
	BOPWILLOW(ModList.BOP, 			0x767A47, 0x3E981A, 8, 15, "logs3", "colorizedLeaves2", "colorizedSaplings", new int[]{1, 5, 9, 13}, new int[]{0, 4, 8, 12}, 4, VarType.INSTANCE),
	PINE(ModList.BOP, 				0x896B4F, 0x3E981A, 8, 25, "logs4", "colorizedLeaves2", "colorizedSaplings", new int[]{0, 4, 8, 12}, new int[]{1, 5, 9, 13}, 5, VarType.INSTANCE),
	MAHOGANY(ModList.BOP, 			0x896B4F, 0x3E981A, 9, 16, "logs4", "colorizedLeaves2", "colorizedSaplings", new int[]{3, 7, 11, 15}, new int[]{2, 6, 10, 14}, 6, VarType.INSTANCE),
	BXLREDWOOD(ModList.BXL, 		0x000000, 0x000000, -1, -1, null, null, null, 0, VarType.INSTANCE),
	IC2RUBBER(ModList.IC2, 			0x3C2D20, 0x638143, 6, 15, "rubberWood", "rubberLeaves", "rubberSapling", new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}, new int[]{0,8}, 0, VarType.ITEMSTACK),
	MFRRUBBER(ModList.MINEFACTORY, 	0x7E5C25, 0x5DC123, 30, 90, "rubberWoodBlock", "rubberLeavesBlock", "rubberSaplingBlock", new int[]{0,1,2,3,4,5,13}, new int[]{0,8}, 0, VarType.INSTANCE),
	TIMEWOOD(ModList.TWILIGHT, 		0x4F301D, 6986775, 10, 20, "magicLog", "magicLeaves", "sapling", new int[]{0,12}, new int[]{1,8}, 5, VarType.INSTANCE),
	TRANSFORMATION(ModList.TWILIGHT, 0x66727F, 7130346, 12, 20, "magicLog", "magicLeaves", "sapling", new int[]{1,13}, new int[]{1,9}, 6, VarType.INSTANCE),
	MINEWOOD(ModList.TWILIGHT, 		0xC5A982, 16576836, 15, 20, "magicLog", "magicLeaves", "sapling", new int[]{2,14}, new int[]{2,10}, 7, VarType.INSTANCE),
	SORTING(ModList.TWILIGHT, 		0x705835, 3558403, 12, 20, "magicLog", "magicLeaves", "sapling", new int[]{3,15}, new int[]{3,11}, 8, VarType.INSTANCE),
	GLOW(ModList.TRANSITIONAL, 		0xE2B87B, 0xFFBC5E, 7, 12, "GlowWood", "GlowLeaf", "GlowSapling", 0, 0, 0, VarType.INSTANCE),
	FORCE(ModList.DARTCRAFT, 		0xE0B749, 0xD9B22C, 7, 12, "forceLog", "forceLeaves", "forceSapling", 0, new int[]{0,8}, 0, VarType.INSTANCE),
	HIGHFIR(ModList.HIGHLANDS, 		0x77ee77, 0x88dd88, 10, 30, "firWood", "firLeaves", "firSapling", VarType.INSTANCE),
	HIGHACACIA(ModList.HIGHLANDS, 	0x77ee77, 0x88dd88, 12, 10, "acaciaWood", "acaciaLeaves", "acaciaSapling", VarType.INSTANCE),
	HIGHREDWOOD(ModList.HIGHLANDS, 	0x77ee77, 0x88dd88, 12, 45, "redwoodWood", "redwoodLeaves", "redwoodSapling", VarType.INSTANCE),
	POPLAR(ModList.HIGHLANDS, 		0x77ee77, 0x88dd88, 8, 12, "poplarWood", "poplarLeaves", "poplarSapling", VarType.INSTANCE),
	HIGHCANOPY(ModList.HIGHLANDS, 	0x77ee77, 0x88dd88, 12, 30, "canopyWood", "canopyLeaves", "canopySapling", VarType.INSTANCE),
	HIGHPALM(ModList.HIGHLANDS, 	0x77ee77, 0x88dd88, 4, 12, "palmWood", "palmLeaves", "palmSapling", VarType.INSTANCE),
	IRONWOOD(ModList.HIGHLANDS, 	0x77ee77, 0x88dd88, 20, 50, "ironWood", "ironwoodLeaves", "ironwoodSapling", VarType.INSTANCE),
	HIGHMANGROVE(ModList.HIGHLANDS, 0x77ee77, 0x88dd88, 6, 10, "mangroveWood", "mangroveLeaves", "mangroveSapling", VarType.INSTANCE),
	ASH(ModList.HIGHLANDS, 			0x77ee77, 0x88dd88, 12, 25, "ashWood", "ashLeaves", "ashSapling", VarType.INSTANCE),
	WITCHWOOD(ModList.ARSMAGICA, 	0x584D32, 0x1F4719, 10, 20, "witchwoodLog", "witchwoodLeaves", "witchwoodSapling", VarType.INSTANCE),
	ROWAN(ModList.WITCHERY, 		0x374633, 0x9E774D, 7, 12, "LOG", "LEAVES", "SAPLING", new int[]{0,4,8}, new int[]{0,8}, 0, VarType.INSTANCE),
	HAWTHORNE(ModList.WITCHERY, 	0x656566, 0xC3EEC3, 10, 16, "LOG", "LEAVES", "SAPLING", new int[]{2,6,10}, new int[]{2,10}, 2, VarType.INSTANCE),
	ALDER(ModList.WITCHERY, 		0x52544C, 0xC3D562, 6, 10, "LOG", "LEAVES", "SAPLING", new int[]{1,5,9}, new int[]{1,9}, 1, VarType.INSTANCE),
	LIGHTED(ModList.CHROMATICRAFT,	0xA05F36, 0xFFD793, 10, 14, "GLOWLOG", "GLOWLEAF", "GLOWSAPLING", 0, new int[]{0,1,2,3,4}, 0, VarType.INSTANCE),
	SLIME(ModList.TINKERER,			0x68FF7A, 0x8EFFE1, 12, 15, "slimeGel", "slimeLeaves", "slimeSapling", 1, 0, 0, VarType.INSTANCE),
	TAINTED(ModList.FORBIDDENMAGIC,	0x40374B, 0x530D7B,	7, 12, "taintLog", "taintLeaves", "taintSapling", new int[]{0,4,8}, 0, 0, VarType.INSTANCE);

	private ModList mod;
	private Block blockID = null;
	private Block leafID = null;
	private int blockMeta[];
	private int leafMeta[];
	private boolean hasPlanks;

	private Block saplingID;
	private int saplingMeta;

	public final int logColor;
	public final int leafColor;
	public final BlockBox bounds;

	private String varName;
	private Class containerClass;

	private boolean exists = false;

	public static final ModWoodList[] woodList = values();

	private static final BlockMap<ModWoodList> logMappings = new BlockMap();
	private static final BlockMap<ModWoodList> leafMappings = new BlockMap();
	private static final BlockMap<ModWoodList> saplingMappings = new BlockMap();

	private static final MultiMap<ModList, ModWoodList> modMappings = new MultiMap(CollectionType.HASHSET);

	private ModWoodList(ModList req, int color, int leaf, int w, int h, String blockVar, String leafVar, String saplingVar, VarType type) {
		this(req, color, leaf, w, h, blockVar, leafVar, saplingVar, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}, 0, type);
	}

	private ModWoodList(ModList req, int color, int leaf, int w, int h, String blockVar, String leafVar, String saplingVar, int meta, int metaleaf, int metasapling, VarType type) {
		this(req, color, leaf, w, h, blockVar, leafVar, saplingVar, new int[]{meta}, new int[]{metaleaf}, metasapling, type);
	}

	private ModWoodList(ModList req, int color, int leaf, int w, int h, String blockVar, String leafVar, String saplingVar, int meta, VarType type) {
		this(req, color, leaf, w, h, blockVar, leafVar, saplingVar, new int[]{meta}, new int[]{meta}, meta, type);
	}

	private ModWoodList(ModList req, int color, int leaf, int w, int h, String blockVar, String leafVar, String saplingVar, int[] meta, int metaleaf, int metasapling, VarType type) {
		this(req, color, leaf, w, h, blockVar, leafVar, saplingVar, meta, new int[]{metaleaf}, metasapling, type);
	}

	private ModWoodList(ModList req, int color, int leaf, int w, int h, String blockVar, String leafVar, String saplingVar, int meta, int[] metaleaf, int metasapling, VarType type) {
		this(req, color, leaf, w, h, blockVar, leafVar, saplingVar, new int[]{meta}, metaleaf, metasapling, type);
	}

	private ModWoodList(ModList req, int color, int leafcolor, int w, int h, String blockVar, String leafVar, String saplingVar, int[] meta, int[] metaleaf, int metasapling, VarType type) {
		if (!DragonAPIInit.canLoadHandlers())
			throw new MisuseException("Accessed registry enum too early! Wait until postInit!");
		mod = req;
		leafColor = leafcolor;
		logColor = color;
		bounds = BlockBox.origin().expand(w, h, w);
		if (!mod.isLoaded()) {
			DragonAPICore.log("DRAGONAPI: Not loading "+this.getLabel()+": Mod not present.");
			blockMeta = new int[]{0};
			leafMeta = new int[]{0};
			return;
		}
		Class cl = req.getBlockClass();
		//DragonAPICore.log("DRAGONAPI: Attempting to load "+this.getLabel()+". Data parameters:");
		//DragonAPICore.log(cl+", "+blockVar+", "+leafVar+", "+saplingVar+", "+type);
		if (cl == null) {
			DragonAPICore.logError("Error loading wood "+this.getLabel()+": Empty block class");
			return;
		}
		if (blockVar == null || blockVar.isEmpty()) {
			DragonAPICore.logError("Error loading wood "+this.getLabel()+": Empty variable name");
			return;
		}
		if (leafVar == null || leafVar.isEmpty()) {
			DragonAPICore.logError("Error loading leaves for wood "+this.getLabel()+": Empty variable name");
			return;
		}
		try {
			Block id;
			Block idleaf;
			Block idsapling;
			switch(type) {
				case ITEMSTACK: {
					ItemStack wood = this.loadItemStack(cl, blockVar);
					ItemStack leaf = this.loadItemStack(cl, leafVar);
					ItemStack sapling = this.loadItemStack(cl, saplingVar);
					if (wood == null || leaf == null || sapling == null) {
						DragonAPICore.logError("Error loading "+this.getLabel()+": Block not instantiated!");
						return;
					}
					id = Block.getBlockFromItem(wood.getItem());
					idleaf = Block.getBlockFromItem(leaf.getItem());
					idsapling = Block.getBlockFromItem(sapling.getItem());
					break;
				}
				case INSTANCE: {
					Block wood_b = this.loadBlock(cl, blockVar);
					Block leaf_b = this.loadBlock(cl, leafVar);
					Block sapling_b = this.loadBlock(cl, saplingVar);
					if (wood_b == null || leaf_b == null || sapling_b == null) {
						DragonAPICore.logError("Error loading "+this.getLabel()+": Block not instantiated!");
						return;
					}
					id = wood_b;
					idleaf = leaf_b;
					idsapling = sapling_b;
					break;
				}
				case REGISTRY: {
					Block wood_b = GameRegistry.findBlock(mod.modLabel, blockVar);
					Block leaf_b = GameRegistry.findBlock(mod.modLabel, leafVar);
					Block sapling_b = GameRegistry.findBlock(mod.modLabel, saplingVar);
					if (wood_b == null || leaf_b == null || sapling_b == null) {
						DragonAPICore.logError("Error loading "+this.getLabel()+": Block not instantiated!");
						return;
					}
					id = wood_b;
					idleaf = leaf_b;
					idsapling = sapling_b;
					break;
				}
				default:
					DragonAPICore.logError("Error loading wood "+this.getLabel());
					DragonAPICore.logError("Invalid variable type "+type);
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
			DragonAPICore.log("Successfully loaded wood "+this.getLabel());
			exists = true;
		}
		catch (NoSuchFieldException e) {
			DragonAPICore.logError("Error loading wood "+this.getLabel());
			e.printStackTrace();
		}
		catch (SecurityException e) {
			DragonAPICore.logError("Error loading wood "+this.getLabel());
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			DragonAPICore.logError("Error loading wood "+this.getLabel());
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			DragonAPICore.logError("Error loading wood "+this.getLabel());
			e.printStackTrace();
		}
		catch (ReflectiveOperationException e) {
			DragonAPICore.logError("Error loading wood "+this.getLabel());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			DragonAPICore.logError("Error loading wood "+this.getLabel());
			e.printStackTrace();
		}
	}

	private ItemStack loadItemStack(Class cl, String field) throws ReflectiveOperationException {
		switch(mod) {
			default: {
				Object ins = this.getFieldInstance();
				Field f = cl.getField(field);
				return (ItemStack)f.get(ins);
			}
		}
	}

	private Block loadBlock(Class cl, String field) throws ReflectiveOperationException {
		switch(mod) {
			case CHROMATICRAFT: {
				Field f = cl.getField(field);
				Method block = cl.getMethod("getBlockInstance");
				Object entry = f.get(null);
				return (Block)block.invoke(entry);
			}
			default: {
				Object ins = this.getFieldInstance();
				Field f = cl.getField(field);
				return (Block)f.get(ins);
			}
		}
	}

	private Object getFieldInstance() throws ReflectiveOperationException {
		switch(mod) {
			case WITCHERY: {
				Class c = Class.forName("com.emoniph.witchery.Witchery");
				Field f = c.getField("Blocks");
				return f.get(null);
			}
			default:
				return null;
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
			return Block.getBlockFromItem(block.getItem()) == blockID;
		}
		for (int i = 0; i < blockMeta.length; i++) {
			if (ReikaItemHelper.matchStacks(block, this.getLogItemWithOffset(i)))
				return true;
		}
		return false;
	}

	public Block getBlock() {
		return blockID;
	}

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}

	@Override
	public Block getLogID() {
		return blockID;
	}

	@Override
	public Block getLeafID() {
		return leafID;
	}

	@Override
	public Block getSaplingID() {
		return saplingID;
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

	public static ModWoodList getModWood(Block id, int meta) {
		return logMappings.get(id, meta);
	}

	public static ModWoodList getModWood(ItemStack block) {
		return getModWood(Block.getBlockFromItem(block.getItem()), block.getItemDamage());
	}

	public static ModWoodList getModWoodFromSapling(Block id, int meta) {
		return saplingMappings.get(id, meta);
	}

	public static ModWoodList getModWoodFromSapling(ItemStack block) {
		return getModWoodFromSapling(Block.getBlockFromItem(block.getItem()), block.getItemDamage());
	}

	public static ModWoodList getModWoodFromLeaf(ItemStack block) {
		return getModWoodFromLeaf(Block.getBlockFromItem(block.getItem()), block.getItemDamage());
	}

	public static ModWoodList getModWoodFromLeaf(Block id, int meta) {
		return leafMappings.get(id, meta);
	}

	public static boolean isModWood(ItemStack block) {
		return getModWood(block) != null;
	}

	public static boolean isModWood(Block id, int meta) {
		return getModWood(id, meta) != null;
	}

	public static boolean isModLeaf(Block id, int meta) {
		return getModWoodFromLeaf(id, meta) != null;
	}

	public static boolean isModLeaf(ItemStack block) {
		return getModWoodFromLeaf(block) != null;
	}

	public static boolean isModSapling(ItemStack block) {
		return getModWoodFromSapling(block) != null;
	}

	public static boolean isModSapling(Block id, int meta) {
		return getModWoodFromSapling(id, meta) != null;
	}

	public IIcon getWoodIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return this.getBlock().getIcon(iba, x, y, z, s);
	}

	public IIcon getSideIcon() {
		return this.getBlock().getBlockTextureFromSide(2);
	}

	public EntityFallingBlock getFallingBlock(World world, int x, int y, int z) {
		EntityFallingBlock e = new EntityFallingBlock(world, x+0.5, y+0.5, z+0.5, blockID, blockMeta[0]);
		return e;
	}

	public ItemStack getBasicLeaf() {
		return new ItemStack(leafID, 1, leafMeta[0]);
	}

	public ArrayList<ItemStack> getAllLeaves() {
		ArrayList<ItemStack> li = new ArrayList();
		for (int i = 0; i < leafMeta.length; i++) {
			li.add(new ItemStack(leafID, 1, leafMeta[i]));
		}
		return li;
	}

	public ItemStack getCorrespondingSapling() {
		return new ItemStack(saplingID, 1, saplingMeta);
	}

	public int getSaplingMeta() {
		return saplingMeta;
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
		if (this.isMagicTFTree())
			return true;
		if (this == SILVERWOOD)
			return true;
		return false;
	}

	public boolean isMagicTFTree() {
		if (this == TIMEWOOD)
			return true;
		if (this == SORTING)
			return true;
		if (this == MINEWOOD)
			return true;
		if (this == TRANSFORMATION)
			return true;
		return false;
	}

	public boolean canBePlacedSideways() {
		return this.getLogMetadatas().size() == 3;
	}

	public boolean isNaturalLeaf(World world, int x, int y, int z) {
		if (this.getParentMod() == ModList.BOP || this.getParentMod() == ModList.THAUMCRAFT || this.getParentMod() == ModList.NATURA || this.getParentMod() == ModList.TWILIGHT)
			return (world.getBlockMetadata(x, y, z)&4) == 0; //these mods use the vanilla rules
		return true;
	}

	@Override
	public BlockBox getTypicalMaximumSize() {
		return bounds;
	}

	public static Collection<ModWoodList> getAllWoodsByMod(ModList mod) {
		return modMappings.get(mod);
	}

	public static enum VarType {
		ITEMSTACK(),
		INSTANCE(),
		REGISTRY();
		//INT();

		@Override
		public String toString() {
			return "Variable Type "+ReikaStringParser.capFirstChar(this.name());
		}
	}

	static {
		for (int i = 0; i < woodList.length; i++) {
			ModWoodList w = woodList[i];
			if (w.exists()) {
				Block id = w.blockID;
				Block leaf = w.leafID;
				int[] metas = w.blockMeta;
				int[] leafmetas = w.leafMeta;
				Block sapling = w.saplingID;
				int saplingMeta = w.saplingMeta;
				for (int k = 0; k < metas.length; k++) {
					logMappings.put(id, metas[k], w);
				}
				for (int k = 0; k < leafmetas.length; k++) {
					leafMappings.put(leaf, leafmetas[k], w);
				}
				saplingMappings.put(sapling, saplingMeta, w);

				modMappings.addValue(w.mod, w);
			}
		}
	}
}

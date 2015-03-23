/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Interfaces.OreType;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public enum ReikaOreHelper implements OreType {

	COAL("Coal", Blocks.coal_ore, Items.coal, 1, "oreCoal", "itemCoal", OreRarity.COMMON),
	IRON("Iron", Blocks.iron_ore, "oreIron", 1, "ingotIron", OreRarity.AVERAGE),
	GOLD("Gold", Blocks.gold_ore, "oreGold", 1, "ingotGold", OreRarity.SCATTERED),
	REDSTONE("Redstone", Blocks.redstone_ore, Items.redstone, 1, "oreRedstone", "dustRedstone", OreRarity.COMMON),
	LAPIS("Lapis Lazuli", Blocks.lapis_ore, ReikaDyeHelper.BLUE.getStackOf(), 1, "oreLapis", "gemLapis", OreRarity.SCARCE),
	DIAMOND("Diamond", Blocks.diamond_ore, Items.diamond, 1, "oreDiamond", "gemDiamond", OreRarity.SCARCE),
	EMERALD("Emerald", Blocks.emerald_ore, Items.emerald, 1, "oreEmerald", "gemEmerald", OreRarity.RARE),
	QUARTZ("Nether Quartz", Blocks.quartz_ore, Items.quartz, 1, "oreQuartz", "itemQuartz", OreRarity.EVERYWHERE);

	private String name;
	private ItemStack drop;
	private Block ore;
	private String oreDict;
	private String dropOreDict;
	public final OreRarity rarity;
	public final int blockDrops;
	private final ArrayList<ItemStack> ores = new ArrayList<ItemStack>();

	private static final HashMap<String, String> cases = new HashMap();
	private static final HashMap<Block, ReikaOreHelper> vanillaOres = new HashMap();
	private static final HashSet<String> oreNames = new HashSet();

	public static final ReikaOreHelper[] oreList = ReikaOreHelper.values();

	private static final ArrayList<ItemStack> extraOres = new ArrayList();

	private ReikaOreHelper(String n, Block b, ItemStack is, int nd, String d, String d2, OreRarity r) {
		name = n;
		ore = b;
		drop = is.copy();
		blockDrops = nd;
		oreDict = d;
		dropOreDict = d2;
		ores.addAll(OreDictionary.getOres(oreDict));
		rarity = r;
	}

	private ReikaOreHelper(String n, Block b, Item i, int nd, String d, String d2, OreRarity r) {
		this(n, b, new ItemStack(i), nd, d, d2, r);
	}

	private ReikaOreHelper(String n, Block b, String d, int nd, String d2, OreRarity r) {
		this(n, b, new ItemStack(b), nd, d, d2, r);
	}

	public String getName() {
		return name;
	}

	public ItemStack getDrop() {
		return drop.copy();
	}

	public ItemStack getOreBlock() {
		return new ItemStack(ore);
	}

	public Block getOreBlockInstance() {
		return ore;
	}

	public boolean dropsSelf() {
		return drop.getItem() == Item.getItemFromBlock(ore);
	}

	public String getOreDictName() {
		return oreDict;
	}

	public String getDropOreDictName() {
		return dropOreDict;
	}

	public static boolean isVanillaOre(Block id) {
		return getFromVanillaOre(id) != null;
	}

	public static boolean isVanillaOre(Item id) {
		return isVanillaOre(Block.getBlockFromItem(id));
	}

	public static ReikaOreHelper getFromVanillaOre(Item id) {
		return getFromVanillaOre(Block.getBlockFromItem(id));
	}

	public static ReikaOreHelper getFromVanillaOre(Block id) {
		return vanillaOres.get(id);
	}

	public static boolean isVanillaOre(ItemStack id) {
		return getFromVanillaOre(id) != null;
	}

	public static ReikaOreHelper getFromVanillaOre(ItemStack id) {
		return vanillaOres.get(Block.getBlockFromItem(id.getItem()));
	}

	public ItemStack getResource() {
		if (!this.dropsSelf())
			return this.getDrop();
		if (this == IRON)
			return new ItemStack(Items.iron_ingot);
		if (this == GOLD)
			return new ItemStack(Items.gold_ingot);
		return null;
	}

	public Collection<ItemStack> getAllOreBlocks() {
		return Collections.unmodifiableCollection(OreDictionary.getOres(this.getOreDictName()));
	}

	@Override
	public ItemStack getFirstOreBlock() {
		return new ItemStack(ore);
	}

	public static ReikaOreHelper getEntryByOreDict(ItemStack is) {
		ReikaOreHelper special = checkForSpecialCases(is);
		if (special != null)
			return special;
		for (int i = 0; i < oreList.length; i++) {
			ReikaOreHelper ore = oreList[i];
			ArrayList<ItemStack> li = ore.ores;
			if (ReikaItemHelper.collectionContainsItemStack(li, is))
				return ore;
		}
		return null;
	}

	public static ReikaOreHelper checkForSpecialCases(ItemStack is) {
		for (Map.Entry<String, String> entry : cases.entrySet()) {
			String key = entry.getKey();
			ArrayList<ItemStack> li = OreDictionary.getOres(key);
			if (ReikaItemHelper.collectionContainsItemStack(li, is)) {
				return getEntryFromOreName(entry.getValue());
			}
		}
		return null;
	}

	public static ReikaOreHelper getEntryFromOreName(String value) {
		for (int i = 0; i < oreList.length; i++) {
			ReikaOreHelper ore = oreList[i];
			if (ore.getOreDictName().equals(value))
				return ore;
		}
		return null;
	}

	static {
		//addSpecialCase("oreEmerald", "oreOlivine");
	}

	private static void addSpecialCase(String ore, String... names) {
		for (int i = 0; i < names.length; i++) {
			cases.put(names[i], ore);
		}
	}

	public static void addOreForReference(ItemStack ore) {
		ReikaJavaLibrary.pConsole("DRAGONAPI: Adding ore reference "+ore);
		extraOres.add(ore);
	}

	public static boolean isExtraOre(ItemStack is) {
		return ReikaItemHelper.collectionContainsItemStack(extraOres, is);
	}

	public Block getOreGenBlock() {
		return this.isEnd() ? Blocks.end_stone : this.isNether() ? Blocks.netherrack : Blocks.stone;
	}

	public static void refreshAll() {
		for (int i = 0; i < oreList.length; i++) {
			ReikaOreHelper ore = oreList[i];
			String tag = ore.oreDict;
			ArrayList<ItemStack> li = OreDictionary.getOres(tag);
			for (int k = 0; k < li.size(); k++) {
				ItemStack is = li.get(k);
				if (!ReikaItemHelper.collectionContainsItemStack(ore.ores, is))
					ore.ores.add(is);
			}
		}
	}

	public OreRarity getRarity() {
		return rarity;
	}

	@Override
	public boolean isNether() {
		return this == QUARTZ;
	}

	@Override
	public boolean isEnd() {
		return false;
	}

	public boolean canGenerateIn(Block b) {
		if (this.isNether())
			return b == Blocks.netherrack;
		if (this.isEnd())
			return b == Blocks.end_stone;
		return b == Blocks.stone;
	}

	@Override
	public boolean existsInGame() {
		return true;
	}

	static {
		for (int i = 0; i < oreList.length; i++) {
			vanillaOres.put(oreList[i].ore, oreList[i]);
			oreNames.add(oreList[i].oreDict);
		}
	}

	public static boolean isVanillaOreType(String s) {
		return oreNames.contains(s);
	}

}

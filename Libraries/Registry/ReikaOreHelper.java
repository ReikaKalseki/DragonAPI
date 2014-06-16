/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Interfaces.OreType;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public enum ReikaOreHelper implements OreType {

	COAL("Coal", Block.oreCoal, Item.coal, "oreCoal", "itemCoal", OreRarity.COMMON),
	IRON("Iron", Block.oreIron, "oreIron", "ingotIron", OreRarity.AVERAGE),
	GOLD("Gold", Block.oreGold, "oreGold", "ingotGold", OreRarity.SCATTERED),
	REDSTONE("Redstone", Block.oreRedstone, Item.redstone, "oreRedstone", "dustRedstone", OreRarity.COMMON),
	LAPIS("Lapis Lazuli", Block.oreLapis, ReikaDyeHelper.BLUE.getStackOf(), "oreLapis", "dyeBlue", OreRarity.SCARCE),
	DIAMOND("Diamond", Block.oreDiamond, Item.diamond, "oreDiamond", "gemDiamond", OreRarity.COMMON),
	EMERALD("Emerald", Block.oreEmerald, Item.emerald, "oreEmerald", "gemEmerald", OreRarity.RARE),
	QUARTZ("Nether Quartz", Block.oreNetherQuartz, Item.netherQuartz, "oreNetherQuartz", "itemQuartz", OreRarity.EVERYWHERE);

	private String name;
	private ItemStack drop;
	private Block ore;
	private String oreDict;
	private String dropOreDict;
	public final OreRarity rarity;
	private final ArrayList<ItemStack> ores = new ArrayList<ItemStack>();

	private static final HashMap<String, String> cases = new HashMap();
	private static final HashMap<Integer, ReikaOreHelper> vanillaOres = new HashMap();

	public static final ReikaOreHelper[] oreList = ReikaOreHelper.values();

	private static ArrayList<ItemStack> extraOres = new ArrayList();

	private ReikaOreHelper(String n, Block b, ItemStack is, String d, String d2, OreRarity r) {
		name = n;
		ore = b;
		drop = is.copy();
		oreDict = d;
		dropOreDict = d2;
		ores.addAll(OreDictionary.getOres(oreDict));
		rarity = r;
	}

	private ReikaOreHelper(String n, Block b, Item i, String d, String d2, OreRarity r) {
		this(n, b, new ItemStack(i), d, d2, r);
	}

	private ReikaOreHelper(String n, Block b, String d, String d2, OreRarity r) {
		this(n, b, new ItemStack(b), d, d2, r);
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

	public boolean dropsSelf() {
		return drop.itemID == ore.blockID;
	}

	public String getOreDictName() {
		return oreDict;
	}

	public String getDropOreDictName() {
		return dropOreDict;
	}

	public static boolean isVanillaOre(int id) {
		return getFromVanillaOre(id) != null;
	}

	public static ReikaOreHelper getFromVanillaOre(int id) {
		return vanillaOres.get(id);
	}

	public ItemStack getResource() {
		if (!this.dropsSelf())
			return this.getDrop();
		if (this == IRON)
			return new ItemStack(Item.ingotIron);
		if (this == GOLD)
			return new ItemStack(Item.ingotGold);
		return null;
	}

	public static ReikaOreHelper getEntryByOreDict(ItemStack is) {
		ReikaOreHelper special = checkForSpecialCases(is);
		if (special != null)
			return special;
		for (int i = 0; i < oreList.length; i++) {
			ReikaOreHelper ore = oreList[i];
			ArrayList<ItemStack> li = ore.ores;
			if (ReikaItemHelper.listContainsItemStack(li, is))
				return ore;
		}
		return null;
	}

	public static ReikaOreHelper checkForSpecialCases(ItemStack is) {
		for (Map.Entry<String, String> entry : cases.entrySet()) {
			String key = entry.getKey();
			ArrayList<ItemStack> li = OreDictionary.getOres(key);
			if (ReikaItemHelper.listContainsItemStack(li, is)) {
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
		return ReikaItemHelper.listContainsItemStack(extraOres, is);
	}

	public Block getOreGenBlock() {
		return this.isEnd() ? Block.whiteStone : this.isNether() ? Block.netherrack : Block.stone;
	}

	public static void refreshAll() {
		for (int i = 0; i < oreList.length; i++) {
			ReikaOreHelper ore = oreList[i];
			String tag = ore.oreDict;
			ArrayList<ItemStack> li = OreDictionary.getOres(tag);
			for (int k = 0; k < li.size(); k++) {
				ItemStack is = li.get(k);
				if (!ReikaItemHelper.listContainsItemStack(ore.ores, is))
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

	static {
		for (int i = 0; i < oreList.length; i++) {
			vanillaOres.put(oreList[i].ore.blockID, oreList[i]);
		}
	}

}

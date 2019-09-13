/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.ModInteract.RecipeHandlers.ModOreCompat;

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
	private static final HashMap<String, ReikaOreHelper> oreNames = new HashMap();
	private static final HashMap<String, ReikaOreHelper> enumNames = new HashMap();
	private static final ItemHashMap<ReikaOreHelper> itemMap = new ItemHashMap();

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

	public String[] getOreDictNames() {
		return new String[]{oreDict};
	}

	@Override
	public String getProductOreDictName() {
		return dropOreDict;
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

	public boolean contains(ItemStack is) {
		return ReikaItemHelper.collectionContainsItemStack(this.getAllOreBlocks(), is);
	}

	@Override
	public ItemStack getFirstOreBlock() {
		return new ItemStack(ore);
	}

	public static ReikaOreHelper getEntryByOreDict(ItemStack is) {
		return itemMap.get(is);
	}

	public static ReikaOreHelper getEntryFromOreName(String value) {
		return oreNames.get(value);
	}

	public Block getOreGenBlock() {
		return this.isEnd() ? Blocks.end_stone : this.isNether() ? Blocks.netherrack : Blocks.stone;
	}

	public static void refreshAll() {
		for (int i = 0; i < oreList.length; i++) {
			ReikaOreHelper ore = oreList[i];
			ore.refresh();
		}
	}

	private void refresh() {
		ores.clear();
		String tag = oreDict;
		ArrayList<ItemStack> li = new ArrayList(OreDictionary.getOres(tag)); //wrap in new list to get rid of Forge's immutable
		li.addAll(ModOreCompat.instance.load(this));
		for (ItemStack is : li) {
			if (!ReikaItemHelper.collectionContainsItemStack(ores, is)) {
				ores.add(is);
				itemMap.put(is, this);
			}
		}
	}

	public OreRarity getRarity() {
		return rarity;
	}

	public boolean isNether() {
		return this == QUARTZ;
	}

	public boolean isEnd() {
		return false;
	}

	public EnumSet<OreLocation> getOreLocations() {
		return this.isEnd() ? EnumSet.of(OreLocation.END) : this.isNether() ? EnumSet.of(OreLocation.NETHER) : EnumSet.of(OreLocation.OVERWORLD);
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
			oreNames.put(oreList[i].oreDict, oreList[i]);
			enumNames.put(oreList[i].name(), oreList[i]);
		}

		vanillaOres.put(Blocks.lit_redstone_ore, REDSTONE);
	}

	public static boolean isVanillaOreType(String s) {
		return oreNames.containsKey(s);
	}

	public static ReikaOreHelper getByDrop(ItemStack is) {
		int[] ids = OreDictionary.getOreIDs(is);
		for (int i = 0; i < ids.length; i++) {
			String s = OreDictionary.getOreName(ids[i]);
			ReikaOreHelper ore = oreNames.get(s);
			if (ore != null)
				return ore;
		}
		return null;
	}

	public static ReikaOreHelper getByEnumName(String name) {
		return enumNames.get(name);
	}

	@Override
	public int getDisplayColor() {
		switch(this) {
			case COAL:
				return 0x141414;
			case DIAMOND:
				return 0x00ffff;
			case EMERALD:
				return 0x00ff00;
			case GOLD:
				return 0xffd010;
			case IRON:
				return 0xCC998F;
			case LAPIS:
				return 0x2050ff;
			case QUARTZ:
				return 0xc0b0a0;
			case REDSTONE:
				return 0xff2020;
			default:
				return 0xffffff;
		}
	}

	@Override
	public int getDropCount() {
		switch(this) {
			case REDSTONE:
				return 4;
			case LAPIS:
				return 6;
			default:
				return 1;
		}
	}

	@Override
	public String getDisplayName() {
		return name;
	}

}

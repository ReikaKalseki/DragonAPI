/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Exception.WTFException;
import Reika.DragonAPI.Instantiable.ItemMaterial;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemMaterialController {

	private final ItemHashMap<ItemMaterial> data = new ItemHashMap<ItemMaterial>();
	private final ArrayList<ItemStack> locks = new ArrayList<ItemStack>();

	public static final ItemMaterialController instance = new ItemMaterialController();

	private ItemMaterialController() {
		this.addVanillaItem(Items.golden_hoe, ItemMaterial.GOLD);
		this.addVanillaItem(Items.golden_pickaxe, ItemMaterial.GOLD);
		this.addVanillaItem(Items.golden_shovel, ItemMaterial.GOLD);
		this.addVanillaItem(Items.golden_sword, ItemMaterial.GOLD);
		this.addVanillaItem(Items.golden_axe, ItemMaterial.GOLD);
		this.addVanillaItem(Blocks.golden_rail, ItemMaterial.GOLD);
		this.addVanillaItem(Items.golden_helmet, ItemMaterial.GOLD);
		this.addVanillaItem(Items.golden_boots, ItemMaterial.GOLD);
		this.addVanillaItem(Items.golden_leggings, ItemMaterial.GOLD);
		this.addVanillaItem(Items.golden_chestplate, ItemMaterial.GOLD);

		this.addVanillaItem(Items.iron_hoe, ItemMaterial.IRON);
		this.addVanillaItem(Items.iron_pickaxe, ItemMaterial.IRON);
		this.addVanillaItem(Items.iron_shovel, ItemMaterial.IRON);
		this.addVanillaItem(Items.iron_sword, ItemMaterial.IRON);
		this.addVanillaItem(Items.iron_axe, ItemMaterial.IRON);
		this.addVanillaItem(Blocks.detector_rail, ItemMaterial.IRON);
		this.addVanillaItem(Blocks.rail, ItemMaterial.IRON);
		this.addVanillaItem(Blocks.activator_rail, ItemMaterial.IRON);
		this.addVanillaItem(Items.iron_helmet, ItemMaterial.IRON);
		this.addVanillaItem(Items.iron_boots, ItemMaterial.IRON);
		this.addVanillaItem(Items.iron_leggings, ItemMaterial.IRON);
		this.addVanillaItem(Items.iron_chestplate, ItemMaterial.IRON);
		this.addVanillaItem(Items.flint_and_steel, ItemMaterial.IRON);
		this.addVanillaItem(Blocks.iron_bars, ItemMaterial.IRON);
		this.addVanillaItem(Items.cauldron, ItemMaterial.IRON);
		this.addVanillaItem(Blocks.anvil, ItemMaterial.IRON);
		this.addVanillaItem(Blocks.hopper, ItemMaterial.IRON);
		this.addVanillaItem(Items.iron_door, ItemMaterial.IRON);
		this.addVanillaItem(Items.bucket, ItemMaterial.IRON);
		this.addVanillaItem(Items.minecart, ItemMaterial.IRON);

		this.addVanillaItem(Blocks.obsidian, ItemMaterial.OBSIDIAN);
		this.addVanillaItem(Blocks.diamond_block, ItemMaterial.DIAMOND);
		this.addVanillaItem(Blocks.iron_block, ItemMaterial.IRON);
		this.addVanillaItem(Blocks.gold_block, ItemMaterial.GOLD);
		this.addVanillaItem(Blocks.stone, ItemMaterial.STONE);
		this.addVanillaItem(Blocks.cobblestone, ItemMaterial.STONE);
		this.addVanillaItem(Blocks.stonebrick, ItemMaterial.STONE);
		this.addVanillaItem(Blocks.brick_block, ItemMaterial.STONE);
		this.addVanillaItem(Blocks.bookshelf, ItemMaterial.WOOD);
		this.addVanillaItem(Items.wooden_door, ItemMaterial.WOOD);
		this.addVanillaItem(Items.sign, ItemMaterial.WOOD);
		this.addVanillaItem(Blocks.crafting_table, ItemMaterial.WOOD);
		this.addVanillaItem(Blocks.chest, ItemMaterial.WOOD);
		this.addVanillaItem(Blocks.trapped_chest, ItemMaterial.WOOD);
		this.addVanillaItem(Blocks.oak_stairs, ItemMaterial.WOOD);
		this.addVanillaItem(Blocks.birch_stairs, ItemMaterial.WOOD);
		this.addVanillaItem(Blocks.spruce_stairs, ItemMaterial.WOOD);
		this.addVanillaItem(Blocks.jungle_stairs, ItemMaterial.WOOD);
		this.addVanillaItem(Items.stick, ItemMaterial.WOOD);
		this.addVanillaItem(Items.bowl, ItemMaterial.WOOD);
		this.addVanillaItem(Items.wooden_sword, ItemMaterial.WOOD);
		this.addVanillaItem(Items.wooden_pickaxe, ItemMaterial.WOOD);
		this.addVanillaItem(Items.wooden_axe, ItemMaterial.WOOD);
		this.addVanillaItem(Items.wooden_shovel, ItemMaterial.WOOD);

		for (int i = 0; i < 4; i++) {
			this.addVanillaItem(new ItemStack(Blocks.planks, 1, i), ItemMaterial.WOOD);
			this.addVanillaItem(new ItemStack(Blocks.log, 1, i), ItemMaterial.WOOD);;
			this.addVanillaItem(new ItemStack(Blocks.log2, 1, i), ItemMaterial.WOOD);
		}

		this.addVanillaItem(Items.diamond, ItemMaterial.DIAMOND);
		this.addVanillaItem(Items.iron_ingot, ItemMaterial.IRON);
		this.addVanillaItem(Items.gold_ingot, ItemMaterial.GOLD);

		this.addVanillaItem(Items.coal, ItemMaterial.COAL);
		this.addVanillaItem(new ItemStack(Items.coal, 1, 1), ItemMaterial.COAL);
		this.addVanillaItem(Blocks.coal_block, ItemMaterial.COAL);
	}

	public void addItem(ItemStack is, ItemMaterial mat) {
		if (this.hasImmutableMapping(is))
			throw new MisuseException("Do not try to overwrite mappings of vanilla items!");
		ReikaJavaLibrary.pConsole("DRAGONAPI: Adding "+mat+" material properties to "+is);
		data.put(is, mat);
	}

	public void addItem(Item i, ItemMaterial mat) {
		this.addItem(new ItemStack(i), mat);
	}

	public void addItem(Block b, ItemMaterial mat) {
		this.addItem(new ItemStack(b), mat);
	}

	public boolean hasImmutableMapping(ItemStack is) {
		return ReikaItemHelper.collectionContainsItemStack(locks, is);
	}

	private void addVanillaItem(ItemStack is, ItemMaterial mat) {
		if (this.hasImmutableMapping(is))
			throw new MisuseException("Do not try to overwrite mappings of vanilla items!");
		ReikaJavaLibrary.pConsole("DRAGONAPI: Adding immutable material "+mat+" properties to vanilla item "+is);
		data.put(is, mat);
		locks.add(is);
	}

	private void addVanillaItem(Item i, ItemMaterial mat) {
		if (i == null)
			throw new WTFException("Some mod is deleting the vanilla item "+i+"!", true);
		this.addVanillaItem(new ItemStack(i), mat);
	}

	private void addVanillaItem(Block b, ItemMaterial mat) {
		if (b == null)
			throw new WTFException("Some mod is deleting the vanilla block "+b+"!", true);
		if (Item.getItemFromBlock(b) == null)
			ReikaJavaLibrary.pConsole("DRAGONAPI: Block "+b+" has no corresponding item!");
		else
			this.addVanillaItem(new ItemStack(b), mat);
	}

	public int getMeltingPoint(ItemStack is) {
		if (!this.hasDataFor(is))
			return 0;
		return data.get(is).getMelting();
	}

	public boolean hasDataFor(ItemStack is) {
		return data.containsKey(is);
	}

	public ItemMaterial getMaterial(ItemStack is) {
		return data.get(is);
	}

}

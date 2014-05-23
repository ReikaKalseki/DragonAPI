/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Exception.WTFException;
import Reika.DragonAPI.Instantiable.ItemMaterial;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemMaterialController {

	private final HashMap<List<Integer>, ItemMaterial> data = new HashMap<List<Integer>, ItemMaterial>();
	private final ArrayList<ItemStack> locks = new ArrayList<ItemStack>();

	public static final ItemMaterialController instance = new ItemMaterialController();

	private ItemMaterialController() {
		this.addVanillaItem(Item.hoeGold, ItemMaterial.GOLD);
		this.addVanillaItem(Item.pickaxeGold, ItemMaterial.GOLD);
		this.addVanillaItem(Item.shovelGold, ItemMaterial.GOLD);
		this.addVanillaItem(Item.swordGold, ItemMaterial.GOLD);
		this.addVanillaItem(Item.axeGold, ItemMaterial.GOLD);
		this.addVanillaItem(Block.railPowered, ItemMaterial.GOLD);
		this.addVanillaItem(Item.helmetGold, ItemMaterial.GOLD);
		this.addVanillaItem(Item.bootsGold, ItemMaterial.GOLD);
		this.addVanillaItem(Item.legsGold, ItemMaterial.GOLD);
		this.addVanillaItem(Item.plateGold, ItemMaterial.GOLD);

		this.addVanillaItem(Item.hoeIron, ItemMaterial.IRON);
		this.addVanillaItem(Item.pickaxeIron, ItemMaterial.IRON);
		this.addVanillaItem(Item.shovelIron, ItemMaterial.IRON);
		this.addVanillaItem(Item.swordIron, ItemMaterial.IRON);
		this.addVanillaItem(Item.axeIron, ItemMaterial.IRON);
		this.addVanillaItem(Block.railDetector, ItemMaterial.IRON);
		this.addVanillaItem(Block.rail, ItemMaterial.IRON);
		this.addVanillaItem(Block.railActivator, ItemMaterial.IRON);
		this.addVanillaItem(Item.helmetIron, ItemMaterial.IRON);
		this.addVanillaItem(Item.bootsIron, ItemMaterial.IRON);
		this.addVanillaItem(Item.legsIron, ItemMaterial.IRON);
		this.addVanillaItem(Item.plateIron, ItemMaterial.IRON);
		this.addVanillaItem(Item.flintAndSteel, ItemMaterial.IRON);
		this.addVanillaItem(Block.fenceIron, ItemMaterial.IRON);
		this.addVanillaItem(Block.cauldron, ItemMaterial.IRON);
		this.addVanillaItem(Block.anvil, ItemMaterial.IRON);
		this.addVanillaItem(Block.hopperBlock, ItemMaterial.IRON);
		this.addVanillaItem(Item.doorIron, ItemMaterial.IRON);
		this.addVanillaItem(Item.bucketEmpty, ItemMaterial.IRON);
		this.addVanillaItem(Item.minecartEmpty, ItemMaterial.IRON);

		this.addVanillaItem(Block.obsidian, ItemMaterial.OBSIDIAN);
		this.addVanillaItem(Block.blockDiamond, ItemMaterial.DIAMOND);
		this.addVanillaItem(Block.blockIron, ItemMaterial.IRON);
		this.addVanillaItem(Block.blockGold, ItemMaterial.GOLD);
		this.addVanillaItem(Block.stone, ItemMaterial.STONE);
		this.addVanillaItem(Block.cobblestone, ItemMaterial.STONE);
		this.addVanillaItem(Block.stoneBrick, ItemMaterial.STONE);
		this.addVanillaItem(Block.brick, ItemMaterial.STONE);
		this.addVanillaItem(Block.bookShelf, ItemMaterial.WOOD);
		this.addVanillaItem(Item.doorWood, ItemMaterial.WOOD);
		this.addVanillaItem(Item.sign, ItemMaterial.WOOD);
		this.addVanillaItem(Block.workbench, ItemMaterial.WOOD);
		this.addVanillaItem(Block.chest, ItemMaterial.WOOD);
		this.addVanillaItem(Block.chestTrapped, ItemMaterial.WOOD);
		this.addVanillaItem(Block.stairsWoodOak, ItemMaterial.WOOD);
		this.addVanillaItem(Block.stairsWoodBirch, ItemMaterial.WOOD);
		this.addVanillaItem(Block.stairsWoodSpruce, ItemMaterial.WOOD);
		this.addVanillaItem(Block.stairsWoodJungle, ItemMaterial.WOOD);
		this.addVanillaItem(Item.stick, ItemMaterial.WOOD);
		this.addVanillaItem(Item.bowlEmpty, ItemMaterial.WOOD);
		this.addVanillaItem(Item.swordWood, ItemMaterial.WOOD);
		this.addVanillaItem(Item.pickaxeWood, ItemMaterial.WOOD);
		this.addVanillaItem(Item.axeWood, ItemMaterial.WOOD);
		this.addVanillaItem(Item.shovelWood, ItemMaterial.WOOD);

		for (int i = 0; i < 4; i++) {
			this.addVanillaItem(new ItemStack(Block.planks.blockID, 1, i), ItemMaterial.WOOD);
			this.addVanillaItem(new ItemStack(Block.wood.blockID, 1, i), ItemMaterial.WOOD);
		}

		this.addVanillaItem(Item.diamond, ItemMaterial.DIAMOND);
		this.addVanillaItem(Item.ingotIron, ItemMaterial.IRON);
		this.addVanillaItem(Item.ingotGold, ItemMaterial.GOLD);

		this.addVanillaItem(Item.coal, ItemMaterial.COAL);
		this.addVanillaItem(new ItemStack(Item.coal.itemID, 1, 1), ItemMaterial.COAL);
		this.addVanillaItem(Block.coalBlock, ItemMaterial.COAL);
	}

	public void addItem(ItemStack is, ItemMaterial mat) {
		if (this.hasImmutableMapping(is))
			throw new MisuseException("Do not try to overwrite mappings of vanilla items!");
		ReikaJavaLibrary.pConsole("DRAGONAPI: Adding "+mat+" material properties to "+is);
		data.put(Arrays.asList(is.itemID, is.getItemDamage()), mat);
	}

	public void addItem(Item i, ItemMaterial mat) {
		this.addItem(new ItemStack(i), mat);
	}

	public void addItem(Block b, ItemMaterial mat) {
		this.addItem(new ItemStack(b), mat);
	}

	public boolean hasImmutableMapping(ItemStack is) {
		return ReikaItemHelper.listContainsItemStack(locks, is);
	}

	private void addVanillaItem(ItemStack is, ItemMaterial mat) {
		if (this.hasImmutableMapping(is))
			throw new MisuseException("Do not try to overwrite mappings of vanilla items!");
		ReikaJavaLibrary.pConsole("DRAGONAPI: Adding immutable material "+mat+" properties to vanilla item "+is);
		data.put(Arrays.asList(is.itemID, is.getItemDamage()), mat);
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
		this.addVanillaItem(new ItemStack(b), mat);
	}

	public int getMeltingPoint(ItemStack is) {
		if (!this.hasDataFor(is))
			return 0;
		return data.get(Arrays.asList(is.itemID, is.getItemDamage())).getMelting();
	}

	public boolean hasDataFor(ItemStack is) {
		return data.containsKey(Arrays.asList(is.itemID, is.getItemDamage()));
	}

	public ItemMaterial getMaterial(ItemStack is) {
		return data.get(Arrays.asList(is.itemID, is.getItemDamage()));
	}

}

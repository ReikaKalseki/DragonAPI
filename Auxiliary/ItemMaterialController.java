/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.ItemMaterial;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemMaterialController {

	private static final HashMap<int[], ItemMaterial> data = new HashMap<int[], ItemMaterial>();
	private static final ArrayList<ItemStack> locks = new ArrayList<ItemStack>();

	public static void addItem(ItemStack is, ItemMaterial mat) {
		if (hasImmutableMapping(is))
			throw new MisuseException("Do not try to overwrite mappings of vanilla items!");
		ReikaJavaLibrary.pConsole("DRAGONAPI: Adding "+mat+" material properties to "+is);
		data.put(new int[]{is.itemID, is.getItemDamage()}, mat);
	}

	public static void addItem(Item i, ItemMaterial mat) {
		addItem(new ItemStack(i), mat);
	}

	public static void addItem(Block b, ItemMaterial mat) {
		addItem(new ItemStack(b), mat);
	}

	public static boolean hasImmutableMapping(ItemStack is) {
		return ReikaItemHelper.listContainsItemStack(locks, is);
	}

	private static void addVanillaItem(ItemStack is, ItemMaterial mat) {
		if (hasImmutableMapping(is))
			throw new MisuseException("Do not try to overwrite mappings of vanilla items!");
		ReikaJavaLibrary.pConsole("DRAGONAPI: Adding immutable material "+mat+" properties to vanilla item "+is);
		data.put(new int[]{is.itemID, is.getItemDamage()}, mat);
		locks.add(is);
	}

	private static void addVanillaItem(Item i, ItemMaterial mat) {
		addVanillaItem(new ItemStack(i), mat);
	}

	private static void addVanillaItem(Block b, ItemMaterial mat) {
		addVanillaItem(new ItemStack(b), mat);
	}

	public static int getMeltingPoint(ItemStack is) {
		if (!hasDataFor(is))
			return 0;
		return data.get(new int[]{is.itemID, is.getItemDamage()}).getMelting();
	}

	public static boolean hasDataFor(ItemStack is) {
		return data.containsKey(new int[]{is.itemID, is.getItemDamage()});
	}

	public static ItemMaterial getMaterial(ItemStack is) {
		return data.get(new int[]{is.itemID, is.getItemDamage()});
	}

	static {
		addVanillaItem(Item.hoeGold, ItemMaterial.GOLD);
		addVanillaItem(Item.pickaxeGold, ItemMaterial.GOLD);
		addVanillaItem(Item.shovelGold, ItemMaterial.GOLD);
		addVanillaItem(Item.swordGold, ItemMaterial.GOLD);
		addVanillaItem(Item.axeGold, ItemMaterial.GOLD);
		addVanillaItem(Block.railPowered, ItemMaterial.GOLD);
		addVanillaItem(Item.helmetGold, ItemMaterial.GOLD);
		addVanillaItem(Item.bootsGold, ItemMaterial.GOLD);
		addVanillaItem(Item.legsGold, ItemMaterial.GOLD);
		addVanillaItem(Item.plateGold, ItemMaterial.GOLD);

		addVanillaItem(Item.hoeIron, ItemMaterial.IRON);
		addVanillaItem(Item.pickaxeIron, ItemMaterial.IRON);
		addVanillaItem(Item.shovelIron, ItemMaterial.IRON);
		addVanillaItem(Item.swordIron, ItemMaterial.IRON);
		addVanillaItem(Item.axeIron, ItemMaterial.IRON);
		addVanillaItem(Block.railDetector, ItemMaterial.IRON);
		addVanillaItem(Block.rail, ItemMaterial.IRON);
		addVanillaItem(Block.railActivator, ItemMaterial.IRON);
		addVanillaItem(Item.helmetIron, ItemMaterial.IRON);
		addVanillaItem(Item.bootsIron, ItemMaterial.IRON);
		addVanillaItem(Item.legsIron, ItemMaterial.IRON);
		addVanillaItem(Item.plateIron, ItemMaterial.IRON);
		addVanillaItem(Item.flintAndSteel, ItemMaterial.IRON);
		addVanillaItem(Block.fenceIron, ItemMaterial.IRON);
		addVanillaItem(Block.cauldron, ItemMaterial.IRON);
		addVanillaItem(Block.anvil, ItemMaterial.IRON);
		addVanillaItem(Block.hopperBlock, ItemMaterial.IRON);
		addVanillaItem(Item.doorIron, ItemMaterial.IRON);
		addVanillaItem(Item.bucketEmpty, ItemMaterial.IRON);
		addVanillaItem(Item.minecartEmpty, ItemMaterial.IRON);

		addVanillaItem(Block.obsidian, ItemMaterial.OBSIDIAN);
		addVanillaItem(Block.blockDiamond, ItemMaterial.DIAMOND);
		addVanillaItem(Block.blockIron, ItemMaterial.IRON);
		addVanillaItem(Block.blockGold, ItemMaterial.GOLD);
		addVanillaItem(Block.stone, ItemMaterial.STONE);
		addVanillaItem(Block.cobblestone, ItemMaterial.STONE);
		addVanillaItem(Block.stoneBrick, ItemMaterial.STONE);
		addVanillaItem(Block.brick, ItemMaterial.STONE);
		addVanillaItem(Block.planks, ItemMaterial.WOOD);

		addVanillaItem(Item.diamond, ItemMaterial.DIAMOND);
		addVanillaItem(Item.ingotIron, ItemMaterial.IRON);
		addVanillaItem(Item.ingotGold, ItemMaterial.GOLD);
	}

}

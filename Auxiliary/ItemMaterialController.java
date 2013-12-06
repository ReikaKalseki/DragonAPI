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
import Reika.DragonAPI.Exception.WTFException;
import Reika.DragonAPI.Instantiable.ItemMaterial;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemMaterialController {

	private final HashMap<int[], ItemMaterial> data = new HashMap<int[], ItemMaterial>();
	private final ArrayList<ItemStack> locks = new ArrayList<ItemStack>();

	public static final ItemMaterialController instance = new ItemMaterialController();

	private ItemMaterialController() {

	}

	public void addItem(ItemStack is, ItemMaterial mat) {
		if (this.hasImmutableMapping(is))
			throw new MisuseException("Do not try to overwrite mappings of vanilla items!");
		ReikaJavaLibrary.pConsole("DRAGONAPI: Adding "+mat+" material properties to "+is);
		data.put(new int[]{is.itemID, is.getItemDamage()}, mat);
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
		data.put(new int[]{is.itemID, is.getItemDamage()}, mat);
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
		return data.get(new int[]{is.itemID, is.getItemDamage()}).getMelting();
	}

	public boolean hasDataFor(ItemStack is) {
		return data.containsKey(new int[]{is.itemID, is.getItemDamage()});
	}

	public ItemMaterial getMaterial(ItemStack is) {
		return data.get(new int[]{is.itemID, is.getItemDamage()});
	}

	{
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
		this.addVanillaItem(Block.planks, ItemMaterial.WOOD);

		this.addVanillaItem(Item.diamond, ItemMaterial.DIAMOND);
		this.addVanillaItem(Item.ingotIron, ItemMaterial.IRON);
		this.addVanillaItem(Item.ingotGold, ItemMaterial.GOLD);
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import ic2.api.item.IElectricItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import mekanism.api.gas.IGasItem;
import net.machinemuse.api.electricity.MuseElectricItem;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.ImmutableItemStack;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import Reika.DragonAPI.ModRegistry.ModOreList;
import cofh.api.energy.IEnergyContainerItem;

import com.builtbroken.mc.api.items.energy.IEnergyItem;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReikaItemHelper extends DragonAPICore {

	public static final ItemStack inksac = new ItemStack(Items.dye, 1, 0);
	public static final ItemStack redDye = new ItemStack(Items.dye, 1, 1);
	public static final ItemStack cactusDye = new ItemStack(Items.dye, 1, 2);
	public static final ItemStack cocoaBeans = new ItemStack(Items.dye, 1, 3);
	public static final ItemStack lapisDye = new ItemStack(Items.dye, 1, 4);
	public static final ItemStack purpleDye = new ItemStack(Items.dye, 1, 5);
	public static final ItemStack cyanDye = new ItemStack(Items.dye, 1, 6);
	public static final ItemStack lgrayDye = new ItemStack(Items.dye, 1, 7);
	public static final ItemStack grayDye = new ItemStack(Items.dye, 1, 8);
	public static final ItemStack pinkDye = new ItemStack(Items.dye, 1, 9);
	public static final ItemStack limeDye = new ItemStack(Items.dye, 1, 10);
	public static final ItemStack yellowDye = new ItemStack(Items.dye, 1, 11);
	public static final ItemStack lblueDye = new ItemStack(Items.dye, 1, 12);
	public static final ItemStack magentaDye = new ItemStack(Items.dye, 1, 13);
	public static final ItemStack orangeDye = new ItemStack(Items.dye, 1, 14);
	public static final ItemStack bonemeal = new ItemStack(Items.dye, 1, 15);

	public static final ItemStack blackWool = new ItemStack(Blocks.wool, 1, 15);
	public static final ItemStack redWool = new ItemStack(Blocks.wool, 1, 14);
	public static final ItemStack greenWool = new ItemStack(Blocks.wool, 1, 13);
	public static final ItemStack brownWool = new ItemStack(Blocks.wool, 1, 12);
	public static final ItemStack blueWool = new ItemStack(Blocks.wool, 1, 11);
	public static final ItemStack purpleWool = new ItemStack(Blocks.wool, 1, 10);
	public static final ItemStack cyanWool = new ItemStack(Blocks.wool, 1, 9);
	public static final ItemStack lgrayWool = new ItemStack(Blocks.wool, 1, 8);
	public static final ItemStack grayWool = new ItemStack(Blocks.wool, 1, 7);
	public static final ItemStack pinkWool = new ItemStack(Blocks.wool, 1, 6);
	public static final ItemStack limeWool = new ItemStack(Blocks.wool, 1, 5);
	public static final ItemStack yellowWool = new ItemStack(Blocks.wool, 1, 4);
	public static final ItemStack lblueWool = new ItemStack(Blocks.wool, 1, 3);
	public static final ItemStack magentaWool = new ItemStack(Blocks.wool, 1, 2);
	public static final ItemStack orangeWool = new ItemStack(Blocks.wool, 1, 1);
	public static final ItemStack whiteWool = new ItemStack(Blocks.wool, 1, 0);

	public static final ItemStack stoneBricks = new ItemStack(Blocks.stonebrick, 1, 0);
	public static final ItemStack mossyBricks = new ItemStack(Blocks.stonebrick, 1, 1);
	public static final ItemStack crackBricks = new ItemStack(Blocks.stonebrick, 1, 2);
	public static final ItemStack circleBricks = new ItemStack(Blocks.stonebrick, 1, 3);

	public static final ItemStack sandstone = new ItemStack(Blocks.sandstone, 1, 0);
	public static final ItemStack carvedSandstone = new ItemStack(Blocks.sandstone, 1, 1);
	public static final ItemStack smoothSandstone = new ItemStack(Blocks.sandstone, 1, 2);

	public static final ItemStack quartz = new ItemStack(Blocks.quartz_block, 1, 0);
	public static final ItemStack carvedQuartz = new ItemStack(Blocks.quartz_block, 1, 1);
	public static final ItemStack columnQuartz = new ItemStack(Blocks.quartz_block, 1, 2);

	/*
	public static final ItemStack oakLog = new ItemStack(Blocks.log, 1, 0);
	public static final ItemStack spruceLog = new ItemStack(Blocks.log, 1, 1);
	public static final ItemStack birchLog = new ItemStack(Blocks.log, 1, 2);
	public static final ItemStack jungleLog = new ItemStack(Blocks.log, 1, 3);
	public static final ItemStack acaciaLog = new ItemStack(Blocks.log2, 1, 0);
	public static final ItemStack darkOakLog = new ItemStack(Blocks.log2, 1, 1);
	public static final ItemStack oakLeaves = new ItemStack(Blocks.leaves, 1, 0);
	public static final ItemStack spruceLeaves = new ItemStack(Blocks.leaves, 1, 1);
	public static final ItemStack birchLeaves = new ItemStack(Blocks.leaves, 1, 2);
	public static final ItemStack jungleLeaves = new ItemStack(Blocks.leaves, 1, 3);
	public static final ItemStack acaciaLeaves = new ItemStack(Blocks.leaves2, 1, 0);
	public static final ItemStack darkOakLeaves = new ItemStack(Blocks.leaves2, 1, 1);
	public static final ItemStack oakSapling = new ItemStack(Blocks.sapling, 1, 0);
	public static final ItemStack spruceSapling = new ItemStack(Blocks.sapling, 1, 1);
	public static final ItemStack birchSapling = new ItemStack(Blocks.sapling, 1, 2);
	public static final ItemStack jungleSapling = new ItemStack(Blocks.sapling, 1, 3);
	public static final ItemStack acaciaSapling = new ItemStack(Blocks.sapling, 1, 4);
	public static final ItemStack darkOakSapling = new ItemStack(Blocks.sapling, 1, 5);
	 */
	public static final ItemStack oakWood = new ItemStack(Blocks.planks, 1, 0);
	public static final ItemStack spruceWood = new ItemStack(Blocks.planks, 1, 1);
	public static final ItemStack birchWood = new ItemStack(Blocks.planks, 1, 2);
	public static final ItemStack jungleWood = new ItemStack(Blocks.planks, 1, 3);
	public static final ItemStack acaciaWood = new ItemStack(Blocks.planks, 1, 4);
	public static final ItemStack darkOakWood = new ItemStack(Blocks.planks, 1, 5);

	public static final ItemStack stoneSlab = new ItemStack(Blocks.stone_slab, 1, 0);
	public static final ItemStack sandstoneSlab = new ItemStack(Blocks.stone_slab, 1, 1);
	public static final ItemStack cobbleSlab = new ItemStack(Blocks.stone_slab, 1, 3);
	public static final ItemStack brickSlab = new ItemStack(Blocks.stone_slab, 1, 4);
	public static final ItemStack stonebrickSlab = new ItemStack(Blocks.stone_slab, 1, 5);
	public static final ItemStack netherSlab = new ItemStack(Blocks.stone_slab, 1, 6);
	public static final ItemStack quartzSlab = new ItemStack(Blocks.stone_slab, 1, 7);

	public static final ItemStack stoneDoubleSlab = new ItemStack(Blocks.double_stone_slab, 1, 0);
	public static final ItemStack sandstoneDoubleSlab = new ItemStack(Blocks.double_stone_slab, 1, 1);
	public static final ItemStack cobbleDoubleSlab = new ItemStack(Blocks.double_stone_slab, 1, 3);
	public static final ItemStack brickDoubleSlab = new ItemStack(Blocks.double_stone_slab, 1, 4);
	public static final ItemStack stonebrickDoubleSlab = new ItemStack(Blocks.double_stone_slab, 1, 5);
	public static final ItemStack netherDoubleSlab = new ItemStack(Blocks.double_stone_slab, 1, 6);
	public static final ItemStack quartzDoubleSlab = new ItemStack(Blocks.double_stone_slab, 1, 7);

	public static final ItemStack oakSlab = new ItemStack(Blocks.wooden_slab, 1, 0);
	public static final ItemStack spruceSlab = new ItemStack(Blocks.wooden_slab, 1, 1);
	public static final ItemStack birchSlab = new ItemStack(Blocks.wooden_slab, 1, 2);
	public static final ItemStack jungleSlab = new ItemStack(Blocks.wooden_slab, 1, 3);

	public static final ItemStack oakDoubleSlab = new ItemStack(Blocks.double_wooden_slab, 1, 0);
	public static final ItemStack spruceDoubleSlab = new ItemStack(Blocks.double_wooden_slab, 1, 1);
	public static final ItemStack birchDoubleSlab = new ItemStack(Blocks.double_wooden_slab, 1, 2);
	public static final ItemStack jungleDoubleSlab = new ItemStack(Blocks.double_wooden_slab, 1, 3);

	public static final ItemStack tallgrass = new ItemStack(Blocks.tallgrass, 1, 1);
	public static final ItemStack fern = new ItemStack(Blocks.tallgrass, 1, 2);

	public static final ItemStack chiseledQuartz = new ItemStack(Blocks.quartz_block, 1, 1);
	public static final ItemStack quartzPillar = new ItemStack(Blocks.quartz_block, 1, 2);

	public static final ItemComparator comparator = new ItemComparator();

	private static final HashMap<Fluid, ItemStack> fluidContainerData = new HashMap();
	private static final HashMap<Item, Double> itemMass = new HashMap();

	private static Field oreListField;

	/** Returns true if the block or item has metadata variants. Args: ID *//*
	public static boolean hasMetadata(Item id) {
		if (id > 255)
			return Items.itemsList[id].getHasSubtypes();
		else {
			return Items.itemsList[id-256].getHasSubtypes();
		}
	}*/

	public static boolean matchStacks(ImmutableItemStack a, ImmutableItemStack b) {
		return matchStacks(a.getItemStack(), b.getItemStack());
	}

	public static boolean matchStacks(ItemStack a, ImmutableItemStack b) {
		return matchStacks(a, b.getItemStack());
	}

	/** Like .equals for comparing ItemStacks, but does not care about size or NBT tags.
	 * Returns true if the ids and metadata match (or both are null).
	 * Args: ItemStacks a, b */
	public static boolean matchStacks(ItemStack a, ItemStack b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		if (a.getItem() == null || b.getItem() == null)
			return false;
		//if (!ItemStack.areItemStackTagsEqual(a, b))
		//	return false;
		if (a.getItem().getHasSubtypes() || b.getItem().getHasSubtypes())
			return (a.getItem() == b.getItem() && (a.getItemDamage() == b.getItemDamage() || a.getItemDamage() == OreDictionary.WILDCARD_VALUE || b.getItemDamage() == OreDictionary.WILDCARD_VALUE));
		else
			return a.getItem() == b.getItem();
	}

	public static boolean isFireworkIngredient(Item id) {
		if (id == Items.diamond)
			return true;
		if (id == Items.dye)
			return true;
		if (id == Items.glowstone_dust)
			return true;
		if (id == Items.feather)
			return true;
		if (id == Items.gold_nugget)
			return true;
		if (id == Items.fire_charge)
			return true;
		if (id == Items.diamond)
			return true;
		if (id == Items.skull)
			return true;
		if (id == Items.firework_charge)
			return true;
		if (id == Items.paper)
			return true;
		if (id == Items.gunpowder)
			return true;
		return false;
	}

	public static ItemStack getSizedItemStack(ItemStack is, int num) {
		if (is == null)
			return null;
		if (is.getItem() == null)
			return null;
		if (num <= 0)
			return null;
		ItemStack is2 = new ItemStack(is.getItem(), num, is.getItemDamage());
		if (is.stackTagCompound != null)
			is2.stackTagCompound = (NBTTagCompound)is.stackTagCompound.copy();
		return is2;
	}

	public static EntityItem dropItem(Entity e, ItemStack is) {
		return dropItem(e.worldObj, e.posX, e.posY, e.posZ, is);
	}

	public static EntityItem dropItem(World world, double x, double y, double z, ItemStack is) {
		return dropItem(world, x, y, z, is, 1);
	}

	public static EntityItem dropItem(World world, double x, double y, double z, ItemStack is, double vscale) {
		if (is == null)
			return null;
		EntityItem ei = new EntityItem(world, x, y, z, is.copy());
		ei.delayBeforeCanPickup = 10;
		ei.motionX = (-0.1+0.2*rand.nextDouble())*vscale;
		ei.motionZ = (-0.1+0.2*rand.nextDouble())*vscale;
		ei.motionY = (0.2*rand.nextDouble())*vscale;
		if (!world.isRemote) {
			world.spawnEntityInWorld(ei);
		}
		return ei;
	}

	public static void dropItems(World world, double x, double y, double z, Collection<ItemStack> li) {
		for (ItemStack is : li)
			dropItem(world, x, y, z, is);
	}

	public static boolean isBlock(ItemStack is) {
		Block b = Block.getBlockFromItem(is.getItem());
		return b != null && b != Blocks.air;
	}

	public static boolean collectionContainsItemStack(Collection<ItemStack> li, ItemStack is) {
		return listContainsItemStack(li, is, false);
	}

	public static boolean listContainsItemStack(Collection<ItemStack> li, ItemStack is, boolean NBT) {
		for (ItemStack is2 : li) {
			if (matchStacks(is, is2) && (!NBT || ItemStack.areItemStackTagsEqual(is, is2)))
				return true;
		}
		return false;
	}

	public static void dropInventory(World world, int x, int y, int z)
	{
		IInventory ii = (IInventory)world.getTileEntity(x, y, z);
		Random par5Random = new Random();
		if (ii != null) {
			label0:
				for (int i = 0; i < ii.getSizeInventory(); i++){
					ItemStack itemstack = ii.getStackInSlot(i);
					if (itemstack == null)
						continue;
					float f = par5Random.nextFloat() * 0.8F + 0.1F;
					float f1 = par5Random.nextFloat() * 0.8F + 0.1F;
					float f2 = par5Random.nextFloat() * 0.8F + 0.1F;
					do {
						if (itemstack.stackSize <= 0)
							continue label0;
						int j = par5Random.nextInt(21) + 10;
						if (j > itemstack.stackSize)
							j = itemstack.stackSize;
						itemstack.stackSize -= j;
						EntityItem ei = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));
						if (itemstack.hasTagCompound())
							ei.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
						float f3 = 0.05F;
						ei.motionX = (float)par5Random.nextGaussian() * f3;
						ei.motionY = (float)par5Random.nextGaussian() * f3 + 0.2F;
						ei.motionZ = (float)par5Random.nextGaussian() * f3;
						ei.delayBeforeCanPickup = 10;
						world.spawnEntityInWorld(ei);
					}
					while (true);
				}
		}
	}

	public static BlockKey getWorldBlockFromItem(ItemStack is) {
		if (is == null)
			return new BlockKey(Blocks.air);
		if (!(is.getItem() instanceof ItemBlock))
			return new BlockKey(Blocks.air);
		int meta = is.getItem().getMetadata(is.getItemDamage());
		if (matchStackWithBlock(is, Blocks.piston) || matchStackWithBlock(is, Blocks.sticky_piston))
			meta = 0;
		return new BlockKey(Block.getBlockFromItem(is.getItem()), meta);
	}

	public static boolean oreItemExists(String tag) {
		ArrayList<ItemStack> li = OreDictionary.getOres(tag);
		return li != null && !li.isEmpty();
	}

	public static boolean oreItemsExist(String... tags) {
		for (int i = 0; i < tags.length; i++) {
			if (!oreItemExists(tags[i]))
				return false;
		}
		return true;
	}

	public static void addToList(ArrayList<ItemStack> items, ArrayList<ItemStack> toAdd) {
		for (int i = 0; i < toAdd.size(); i++) {
			ItemStack is = toAdd.get(i);
			addItemToList(items, is);
		}
	}

	public static void addItemToList(ArrayList<ItemStack> items, ItemStack is) {
		for (int i = 0; i < items.size() && is.stackSize > 0; i++) {
			ItemStack in = items.get(i);
			if (ReikaItemHelper.matchStacks(is, in) && ItemStack.areItemStackTagsEqual(is, in)) {
				int sum = in.stackSize+is.stackSize;
				if (sum <= in.getMaxStackSize()) {
					in.stackSize = sum;
					return;
				}
				else {
					int diff = in.getMaxStackSize()-in.stackSize;
					in.stackSize = in.getMaxStackSize();
					is.stackSize -= diff;
				}
			}
		}
		if (is.stackSize > 0) {
			items.add(is);
		}
	}

	public static boolean matchStackWithBlock(ItemStack is, Block b) {
		return is.getItem() == Item.getItemFromBlock(b);
	}

	public static ItemStack stripNBT(ItemStack is) {
		ItemStack is2 = is.copy();
		is2.stackTagCompound = null;
		return is2;
	}

	private static final HashMap<Item, ArrayList<ItemStack>> permutations = new HashMap();

	@SideOnly(Side.CLIENT)
	public static List<ItemStack> getAllMetadataPermutations(Item item) {
		if (item == null)
			throw new MisuseException("You cannot get the permutations of null!");
		ArrayList<ItemStack> li = permutations.get(item);
		if (li == null) {
			li = new ArrayList();
			CreativeTabs[] tabs = item.getCreativeTabs();
			for (int k = 0; k < tabs.length; k++) {
				CreativeTabs tab = tabs[k];
				item.getSubItems(item, tab, li);
			}
			permutations.put(item, li);
		}
		return Collections.unmodifiableList(li);
	}

	public static void sortItems(List<ItemStack> li) {
		Collections.sort(li, comparator);
	}

	private static class ItemComparator implements Comparator<ItemStack> {

		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			if (o1.getItem() == o2.getItem()) {
				if (o1.getItemDamage() == o2.getItemDamage()) {
					if (o1.stackSize == o2.stackSize) {
						if (o1.stackTagCompound == o2.stackTagCompound || (o1.stackTagCompound != null && o1.stackTagCompound.equals(o2.stackTagCompound))) {
							return 0;
						}
						else {
							if (o1.stackTagCompound == null && o2.stackTagCompound != null) {
								return -1;
							}
							else if (o2.stackTagCompound == null && o1.stackTagCompound != null) {
								return 1;
							}
							else {
								return ReikaNBTHelper.compareNBTTags(o1.stackTagCompound, o2.stackTagCompound);
							}
						}
					}
					else {
						return o1.stackSize-o2.stackSize;
					}
				}
				else {
					return o1.getItemDamage()-o2.getItemDamage();
				}
			}
			else {
				return Item.getIdFromItem(o1.getItem())-Item.getIdFromItem(o2.getItem());
			}
		}

	}

	public static boolean isItemAddedByMod(Item i, String modID) {
		UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(i);
		return id != null ? modID.equalsIgnoreCase(id.modId) : modID == null;
	}

	public static ItemStack getContainerForFluid(Fluid fluid) {
		if (fluidContainerData.containsKey(fluid)) {
			ItemStack ret = fluidContainerData.get(fluid);
			return ret != null ? ret.copy() : null;
		}
		else {
			ItemStack ret = calculateContainerForFluid(fluid);
			fluidContainerData.put(fluid, ret);
			return ret != null ? ret.copy() : null;
		}
	}

	private static ItemStack calculateContainerForFluid(Fluid fluid) {
		ItemStack is = new ItemStack(Items.bucket);
		ItemStack fill = FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, Integer.MAX_VALUE), is);
		if (fill != null) {
			return fill;
		}
		FluidContainerData[] dat = FluidContainerRegistry.getRegisteredFluidContainerData();
		for (int i = 0; i < dat.length; i++) {
			FluidContainerData fcd = dat[i];
			if (fcd.fluid != null && fcd.fluid.getFluid() == fluid && fcd.filledContainer != null) {
				return fcd.filledContainer.copy();
			}
		}
		return null;
	}

	public static boolean isVanillaBlock(Block b) {
		return isVanillaItem(Item.getItemFromBlock(b));
	}

	public static boolean isVanillaItem(ItemStack is) {
		return isVanillaItem(is.getItem());
	}

	public static boolean isVanillaItem(Item i) {
		return "minecraft".equals(getNamespace(i));
	}

	public static String getNamespace(Block b) {
		if (b == null)
			return null;
		String s = Block.blockRegistry.getNameForObject(b);
		return s.substring(0, s.indexOf(':'));
	}

	public static String getNamespace(Item i) {
		if (i == null)
			return null;
		String s = Item.itemRegistry.getNameForObject(i);
		return s.substring(0, s.indexOf(':'));
	}

	public static String getRegistrantMod(ItemStack is) {
		if (isVanillaItem(is))
			return "Minecraft";
		UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(is.getItem());
		return id != null ? id.modId : "[No Mod]";
	}

	public static boolean checkOreDictOverlap(ItemStack is1, ItemStack is2) {
		for (String s : getOreNames(is1))
			if (isInOreTag(is2, s))
				return true;
		return false;
	}

	public static EntityPlayer getDropper(EntityItem ei) {
		if (ei.getEntityData().hasKey("dropper")) {
			try {
				return ei.worldObj.func_152378_a(UUID.fromString(ei.getEntityData().getString("dropper")));
			}
			catch (IllegalArgumentException e) {

			}
		}
		return null;
	}

	public static HashSet<String> getOreNames(ItemStack in) {
		HashSet<String> set = new HashSet();
		int[] ids = OreDictionary.getOreIDs(in);
		for (int i = 0; i < ids.length; i++) {
			set.add(OreDictionary.getOreName(ids[i]));
		}
		return set;
	}

	public static String[] getOreNamesArray(ItemStack is) {
		int[] ids = OreDictionary.getOreIDs(is);
		String[] ore = new String[ids.length];
		for (int i = 0; i < ore.length; i++) {
			ore[i] = OreDictionary.getOreName(ids[i]);
		}
		return ore;
	}

	public static boolean isInOreTag(ItemStack is, String name) {
		return getOreNames(is).contains(name);
	}

	public static Collection<ItemStack> getAllOreItemsExcept(String tag, ItemStack excl) {
		ArrayList<ItemStack> li = new ArrayList();
		for (ItemStack is : OreDictionary.getOres(tag)) {
			if (!ReikaItemHelper.matchStacks(is, excl))
				li.add(is);
		}
		return li;
	}

	public static boolean verifyItemStack(ItemStack is, boolean fullCheck) {
		if (is == null)
			return true;
		if (is.getItem() == null)
			return false;
		try {
			is.toString();
			if (fullCheck)
				is.getDisplayName();
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void removeOreDictEntry(String tag, ItemStack is) throws Exception {
		ArrayList<ItemStack> ores = OreDictionary.getOres(tag);
		ArrayList<ItemStack> li = (ArrayList<ItemStack>)oreListField.get(ores);
		li.remove(is);
		DragonAPICore.log("Removed item "+is+" from OreDict tag '"+tag+"'");
	}

	static {
		try {
			Class c = Class.forName("net.minecraftforge.oredict.OreDictionary$UnmodifiableArrayList");
			oreListField = c.getDeclaredField("list");
			oreListField.setAccessible(true);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		double ingotFactor = 0.0625/8; //to keep values reasonable
		double massIron = ReikaEngLibrary.rhoiron/9D*ingotFactor;
		double massGold = ReikaEngLibrary.rhogold/9D*ingotFactor;
		double massDiamond = ReikaEngLibrary.rhodiamond/9D*ingotFactor;

		itemMass.put(Items.iron_ingot, massIron);
		itemMass.put(Items.gold_ingot, massGold);
		itemMass.put(Items.diamond, massDiamond);

		itemMass.put(Items.iron_helmet, massIron*5);
		itemMass.put(Items.iron_chestplate, massIron*8);
		itemMass.put(Items.iron_leggings, massIron*7);
		itemMass.put(Items.iron_boots, massIron*4);

		itemMass.put(Items.golden_helmet, massGold*5);
		itemMass.put(Items.golden_chestplate, massGold*8);
		itemMass.put(Items.golden_leggings, massGold*7);
		itemMass.put(Items.golden_boots, massGold*4);

		itemMass.put(Items.diamond_helmet, massDiamond*5);
		itemMass.put(Items.diamond_chestplate, massDiamond*8);
		itemMass.put(Items.diamond_leggings, massDiamond*7);
		itemMass.put(Items.diamond_boots, massDiamond*4);
	}

	public static void registerItemMass(Item i, double density, int ingots) {
		registerItemMass(i, density/9D*ingots*0.125);
	}

	public static void registerItemMass(Item i, double mass) {
		itemMass.put(i, mass);
	}

	public static void toggleDamageBit(ItemStack is, int bit) {
		is.setItemDamage(ReikaMathLibrary.toggleBit(is.getItemDamage(), bit));
	}

	public static ArrayList<ItemStack> collateItemList(Collection<ItemStack> c) {
		ArrayList<ItemStack> li = new ArrayList();
		HashMap<KeyedItemStack, Integer> vals = new HashMap();
		for (ItemStack is : c) {
			KeyedItemStack ks = new KeyedItemStack(is).setSimpleHash(true).setIgnoreNBT(false);
			Integer get = vals.get(ks);
			int val = get != null ? get.intValue() : 0;
			vals.put(ks, val+is.stackSize);
		}
		for (KeyedItemStack is : vals.keySet()) {
			int val = vals.get(is);
			while (val > 0) {
				int amt = Math.min(val, is.getItemStack().getMaxStackSize());
				ItemStack copy = getSizedItemStack(is.getItemStack(), amt);
				li.add(copy);
				val -= amt;
			}
		}
		return li;
	}

	public static ItemStack lookupItem(String s) {
		String[] parts = s.split(":");
		int m = 0;
		if (parts.length == 3) {
			try {
				m = parts[2].equalsIgnoreCase("*") ? OreDictionary.WILDCARD_VALUE : Integer.parseInt(parts[2]);
			}
			catch (NumberFormatException e) {

			}
		}
		return lookupItem(parts[0], parts[1], m);
	}

	public static ItemStack lookupItem(ModList mod, String s, int meta) {
		return lookupItem(mod.modLabel, s, meta);
	}

	public static ItemStack lookupItem(String mod, String item, int meta) {
		Item i = GameRegistry.findItem(mod, item);
		return i != null ? new ItemStack(i, 1, meta) : null;
	}

	public static ItemStack lookupBlock(ModList mod, String s, int meta) {
		return lookupBlock(mod.modLabel, s, meta);
	}

	public static ItemStack lookupBlock(String mod, String s, int meta) {
		Block b = Block.getBlockFromName(mod+":"+s);
		return b != null ? new ItemStack(b, 1, meta) : null;
	}

	public static boolean isOreDrop(ItemStack is) {
		OreType ore = ReikaOreHelper.getByDrop(is);
		if (ore != null)
			return true;
		ore = ModOreList.getByDrop(is);
		if (ore != null)
			return true;
		return false;
	}

	public static double getItemMass(ItemStack is) {
		if (is == null)
			return 0;
		Double get = itemMass.get(is);
		return get != null ? get.doubleValue() : 0;
	}

	public static ItemStack dechargeItem(ItemStack is) {
		if (is != null) {
			Item i = is.getItem();
			if (InterfaceCache.GASITEM.instanceOf(i))
				((IGasItem)i).removeGas(is, Integer.MAX_VALUE);
			if (InterfaceCache.ENERGYITEM.instanceOf(i))
				((IEnergyItem)i).discharge(is, Double.POSITIVE_INFINITY, true);
			if (InterfaceCache.RFENERGYITEM.instanceOf(i))
				((IEnergyContainerItem)i).extractEnergy(is, Integer.MAX_VALUE, false);
			if (InterfaceCache.IELECTRICITEM.instanceOf(i))
				is = new ItemStack(((IElectricItem)i).getEmptyItem(is));
			if (InterfaceCache.MUSEELECTRICITEM.instanceOf(i))
				((MuseElectricItem)i).extractEnergy(is, Integer.MAX_VALUE, false);
		}
		return is;
	}

	public static ItemStack getAnyMetaStack(ItemStack is) {
		ItemStack ret = is.copy();
		ret.setItemDamage(OreDictionary.WILDCARD_VALUE);
		return ret;
	}

	public static boolean areStacksCombinable(ItemStack is1, ItemStack is2, int limit) {
		if (is1 != null)
			limit = Math.min(limit, is1.getMaxStackSize());
		return is1 != null && is2 != null && matchStacks(is1, is2) && ItemStack.areItemStackTagsEqual(is1, is2) && is1.stackSize+is2.stackSize <= limit;
	}

	public static ItemStack parseItem(Object o) {
		if (o instanceof ItemStack) {
			return ((ItemStack)o).copy();
		}
		else if (o instanceof Item) {
			return new ItemStack((Item)o);
		}
		else if (o instanceof Block) {
			return new ItemStack((Block)o);
		}
		else if (o instanceof String) {
			return lookupItem((String)o);
		}
		return null;
	}
}

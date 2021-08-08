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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.builtbroken.mc.api.items.energy.IEnergyItem;

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
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
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
import Reika.DragonAPI.Instantiable.ItemFilter;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.ImmutableItemStack;
import Reika.DragonAPI.Instantiable.Recipe.FlexibleIngredient;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import Reika.DragonAPI.ModRegistry.ModOreList;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import mekanism.api.gas.IGasItem;

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

	public static final BlockKey blackWool = new BlockKey(Blocks.wool, 15);
	public static final BlockKey redWool = new BlockKey(Blocks.wool, 14);
	public static final BlockKey greenWool = new BlockKey(Blocks.wool, 13);
	public static final BlockKey brownWool = new BlockKey(Blocks.wool, 12);
	public static final BlockKey blueWool = new BlockKey(Blocks.wool, 11);
	public static final BlockKey purpleWool = new BlockKey(Blocks.wool, 10);
	public static final BlockKey cyanWool = new BlockKey(Blocks.wool, 9);
	public static final BlockKey lgrayWool = new BlockKey(Blocks.wool, 8);
	public static final BlockKey grayWool = new BlockKey(Blocks.wool, 7);
	public static final BlockKey pinkWool = new BlockKey(Blocks.wool, 6);
	public static final BlockKey limeWool = new BlockKey(Blocks.wool, 5);
	public static final BlockKey yellowWool = new BlockKey(Blocks.wool, 4);
	public static final BlockKey lblueWool = new BlockKey(Blocks.wool, 3);
	public static final BlockKey magentaWool = new BlockKey(Blocks.wool, 2);
	public static final BlockKey orangeWool = new BlockKey(Blocks.wool, 1);
	public static final BlockKey whiteWool = new BlockKey(Blocks.wool, 0);

	public static final BlockKey stoneBricks = new BlockKey(Blocks.stonebrick, 0);
	public static final BlockKey mossyBricks = new BlockKey(Blocks.stonebrick, 1);
	public static final BlockKey crackBricks = new BlockKey(Blocks.stonebrick, 2);
	public static final BlockKey circleBricks = new BlockKey(Blocks.stonebrick, 3);

	public static final BlockKey sandstone = new BlockKey(Blocks.sandstone, 0);
	public static final BlockKey carvedSandstone = new BlockKey(Blocks.sandstone, 1);
	public static final BlockKey smoothSandstone = new BlockKey(Blocks.sandstone, 2);

	public static final BlockKey quartz = new BlockKey(Blocks.quartz_block, 0);
	public static final BlockKey carvedQuartz = new BlockKey(Blocks.quartz_block, 1);
	public static final BlockKey columnQuartz = new BlockKey(Blocks.quartz_block, 2);

	public static final BlockKey oakWood = new BlockKey(Blocks.planks, 0);
	public static final BlockKey spruceWood = new BlockKey(Blocks.planks, 1);
	public static final BlockKey birchWood = new BlockKey(Blocks.planks, 2);
	public static final BlockKey jungleWood = new BlockKey(Blocks.planks, 3);
	public static final BlockKey acaciaWood = new BlockKey(Blocks.planks, 4);
	public static final BlockKey darkOakWood = new BlockKey(Blocks.planks, 5);

	public static final BlockKey stoneSlab = new BlockKey(Blocks.stone_slab, 0);
	public static final BlockKey sandstoneSlab = new BlockKey(Blocks.stone_slab, 1);
	public static final BlockKey cobbleSlab = new BlockKey(Blocks.stone_slab, 3);
	public static final BlockKey brickSlab = new BlockKey(Blocks.stone_slab, 4);
	public static final BlockKey stonebrickSlab = new BlockKey(Blocks.stone_slab, 5);
	public static final BlockKey netherSlab = new BlockKey(Blocks.stone_slab, 6);
	public static final BlockKey quartzSlab = new BlockKey(Blocks.stone_slab, 7);

	public static final BlockKey stoneDoubleSlab = new BlockKey(Blocks.double_stone_slab, 0);
	public static final BlockKey sandstoneDoubleSlab = new BlockKey(Blocks.double_stone_slab, 1);
	public static final BlockKey cobbleDoubleSlab = new BlockKey(Blocks.double_stone_slab, 3);
	public static final BlockKey brickDoubleSlab = new BlockKey(Blocks.double_stone_slab, 4);
	public static final BlockKey stonebrickDoubleSlab = new BlockKey(Blocks.double_stone_slab, 5);
	public static final BlockKey netherDoubleSlab = new BlockKey(Blocks.double_stone_slab, 6);
	public static final BlockKey quartzDoubleSlab = new BlockKey(Blocks.double_stone_slab, 7);

	public static final BlockKey oakSlab = new BlockKey(Blocks.wooden_slab, 0);
	public static final BlockKey spruceSlab = new BlockKey(Blocks.wooden_slab, 1);
	public static final BlockKey birchSlab = new BlockKey(Blocks.wooden_slab, 2);
	public static final BlockKey jungleSlab = new BlockKey(Blocks.wooden_slab, 3);

	public static final BlockKey oakDoubleSlab = new BlockKey(Blocks.double_wooden_slab, 0);
	public static final BlockKey spruceDoubleSlab = new BlockKey(Blocks.double_wooden_slab, 1);
	public static final BlockKey birchDoubleSlab = new BlockKey(Blocks.double_wooden_slab, 2);
	public static final BlockKey jungleDoubleSlab = new BlockKey(Blocks.double_wooden_slab, 3);

	public static final BlockKey tallgrass = new BlockKey(Blocks.tallgrass, 1);
	public static final BlockKey fern = new BlockKey(Blocks.tallgrass, 2);

	public static final BlockKey chiseledQuartz = new BlockKey(Blocks.quartz_block, 1);
	public static final BlockKey quartzPillar = new BlockKey(Blocks.quartz_block, 2);

	public static final Comparator<ItemStack> comparator = new ItemComparator();
	public static final Comparator<Object> itemListComparator = new ItemListComparator();

	private static final HashMap<Fluid, ItemStack> fluidContainerData = new HashMap();
	private static final HashMap<Item, Double> itemMass = new HashMap();

	public static final String PLAYER_DEATH_DROP_KEY = "PLAYER_DEATH_DROP";

	private static Field oreListField;

	private static final Pattern ORE_MATERIAL_PATTERN = Pattern.compile("[A-Z].*$");

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

	public static boolean matchStacks(ItemStack a, Object b) {
		if (a == b) {
			return true;
		}
		else if (a == null || b == null) {
			return false;
		}
		else if (b instanceof ItemStack) {
			return matchStacks(a, (ItemStack)b);
		}
		else if (b instanceof Collection) {
			return ReikaItemHelper.listContainsItemStack((Collection<ItemStack>)b, a, false);
		}
		else if (b instanceof String) {
			return ReikaItemHelper.listContainsItemStack(OreDictionary.getOres((String)b), a, false);
		}
		else if (b instanceof ItemFilter) {
			return ((ItemFilter)b).matches(a);
		}
		else if (b instanceof ItemMatch) {
			return ((ItemMatch)b).match(a);
		}
		else if (b instanceof FlexibleIngredient) {
			return ((FlexibleIngredient)b).match(a);
		}
		else {
			return false;
		}
	}

	/** Like .equals for comparing ItemStacks, but does not care about size or NBT tags.
	 * Returns true if the ids and metadata match (or both are null).
	 * Args: ItemStacks a, b */
	public static boolean matchStacks(ItemStack a, ItemStack b) {
		return matchStacks(a, b, false);
	}

	/** Like .equals for comparing ItemStacks, but does not care about size or NBT tags.
	 * Returns true if the ids and metadata match (or both are null).
	 * Args: ItemStacks a, b, whether tool damage counts as mismatch */
	public static boolean matchStacks(ItemStack a, ItemStack b, boolean checkDurability) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		if (a.getItem() == null || b.getItem() == null)
			return false;
		if (a.getItem() != b.getItem())
			return false;
		return areMetasCombinable(a, b, checkDurability);
	}

	private static boolean areMetasCombinable(ItemStack a, ItemStack b, boolean checkDurability) {
		int d1 = a.getItemDamage();
		int d2 = b.getItemDamage();
		if (d1 == d2 || d1 == OreDictionary.WILDCARD_VALUE || d2 == OreDictionary.WILDCARD_VALUE)
			return true;
		if (a.getHasSubtypes())
			return false;
		if (checkDurability && a.getMaxDamage() > 0)
			return false;
		return true;
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

	public static void dropInventory(EntityPlayer ep) {
		IInventory ii = ep.inventory;
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
						EntityItem ei = new EntityItem(ep.worldObj, ep.posX + f, ep.posY+0.25 + f1, ep.posZ + f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));
						if (itemstack.hasTagCompound())
							ei.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
						float f3 = 0.05F;
						ei.motionX = (float)par5Random.nextGaussian() * f3;
						ei.motionY = (float)par5Random.nextGaussian() * f3 + 0.2F;
						ei.motionZ = (float)par5Random.nextGaussian() * f3;
						ei.delayBeforeCanPickup = 10;
						ep.worldObj.spawnEntityInWorld(ei);
					}
					while (true);
				}
		}
	}

	public static void dropInventory(World world, int x, int y, int z) {
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
		if (s == null) {
			DragonAPICore.logError("Found null ID string for non-null item '"+i.getClass().getName()+"'!");
			return "ERROR";
		}
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

	public static void setDropper(EntityItem ei, EntityPlayer ep) {
		String s = ep.getUniqueID().toString();
		ei.getEntityData().setString("dropper", s);
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
			if (ReikaObfuscationHelper.isDeObfEnvironment())
				e.printStackTrace();
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
		if (c.size() <= 1)
			return new ArrayList(c);
		ArrayList<ItemStack> li = new ArrayList();
		HashMap<KeyedItemStack, Integer> vals = new HashMap();
		for (ItemStack is : c) {
			KeyedItemStack ks = new KeyedItemStack(is).setSimpleHash(true).setIgnoreNBT(false);
			Integer get = vals.get(ks);
			int val = get != null ? get.intValue() : 0;
			vals.put(ks, val+is.stackSize);
		}
		for (Entry<KeyedItemStack, Integer> e : vals.entrySet()) {
			KeyedItemStack is = e.getKey();
			Integer val = e.getValue();
			if (val == null) {
				DragonAPICore.logError("Item "+is+" was mapped to null!");
				continue;
			}
			while (val > 0) {
				int amt = Math.min(val, is.getItemStack().getMaxStackSize());
				ItemStack copy = getSizedItemStack(is.getItemStack(), amt);
				li.add(copy);
				val -= amt;
			}
		}
		return li;
	}

	public static boolean matchStackCollections(Collection<ItemStack> c1, Collection<ItemStack> c2) {
		if (c1.size() != c2.size())
			return false;
		ArrayList<ItemStack> li = new ArrayList(c1);
		ArrayList<ItemStack> li2 = new ArrayList(c2);
		for (int i = 0; i < li.size(); i++) {
			ItemStack o1 = li.get(i);
			ItemStack o2 = li2.get(i);
			if (!matchStacks(o1, o2))
				return false;
		}
		return true;
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
			else if (InterfaceCache.ENERGYITEM.instanceOf(i))
				((IEnergyItem)i).discharge(is, Integer.MAX_VALUE, true);
			else if (InterfaceCache.RFENERGYITEM.instanceOf(i))
				((IEnergyContainerItem)i).extractEnergy(is, Integer.MAX_VALUE, false);
			else if (InterfaceCache.MUSEELECTRICITEM.instanceOf(i))
				((MuseElectricItem)i).extractEnergy(is, Integer.MAX_VALUE, false);
			else if (InterfaceCache.IELECTRICITEM.instanceOf(i))
				ElectricItem.manager.discharge(is, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, false, false);
		}
		return is;
	}

	public static ItemStack getAnyMetaStack(ItemStack is) {
		ItemStack ret = is.copy();
		ret.setItemDamage(OreDictionary.WILDCARD_VALUE);
		return ret;
	}

	/** Pass int.max to ignore stack limits */
	public static boolean areStacksCombinable(ItemStack is1, ItemStack is2, int limit) {
		if (is1 != null && limit != Integer.MAX_VALUE)
			limit = Math.min(limit, is1.getMaxStackSize());
		return is1 != null && is2 != null && matchStacks(is1, is2, true) && ItemStack.areItemStackTagsEqual(is1, is2) && is1.stackSize+is2.stackSize <= limit;
	}

	public static ItemStack parseItem(Object o, boolean useWildcards) {
		if (o instanceof ItemStack) {
			return ((ItemStack)o).copy();
		}
		else if (o instanceof Item) {
			return new ItemStack((Item)o, 1, useWildcards ? OreDictionary.WILDCARD_VALUE : 0);
		}
		else if (o instanceof Block) {
			return new ItemStack((Block)o, 1, useWildcards ? OreDictionary.WILDCARD_VALUE : 0);
		}
		else if (o instanceof String) {
			return lookupItem((String)o);
		}
		else if (o instanceof BlockKey) {
			return ((BlockKey)o).asItemStack();
		}
		return null;
	}

	public static OreType parseOreTypeName(String name) {
		Matcher m = ORE_MATERIAL_PATTERN.matcher(name);
		return m.find() ? getOreType(m.group()) : null;
	}

	public static OreType getOreType(String name) {
		OreType ore = ReikaOreHelper.getByEnumName(name);
		if (ore == null)
			ore = ModOreList.getByEnumName(name);
		return ore;
	}

	/** For formatting like "tile.TFPlant/3,4,8-11,13,14" */
	public static Collection<ItemStack> parseMultiRangedMeta(ModList mod, String s) {
		ArrayList<ItemStack> li = new ArrayList();
		String[] parts = s.split("/");
		String item = parts[0];
		if (parts.length > 1) {
			ArrayList<Integer> metas = new ArrayList();
			String[] parts2 = parts[1].split(",");
			for (int i = 0; i < parts2.length; i++) {
				String part = parts2[i];
				if (part.contains("-")) {
					String[] parts3 = part.split("-");
					int low = Integer.parseInt(parts3[0]);
					int high = Integer.parseInt(parts3[1]);
					for (int v = low; v <= high; v++)
						metas.add(v);
				}
				else {
					metas.add(Integer.parseInt(part));
				}
			}
			for (int meta : metas) {
				li.add(ReikaItemHelper.lookupItem(mod, item, meta));
			}
		}
		else {
			li.add(ReikaItemHelper.lookupItem(mod, item, 0));
		}
		return li;
	}

	public static boolean isOreNugget(ItemStack is) {
		for (String s : getOreNames(is))
			if (s.startsWith("nugget"))
				return true;
		return false;
	}

	public static boolean isToolOrArmor(Item i) {
		return i instanceof ItemTool || i instanceof ItemSword || i instanceof ItemShears || i instanceof ItemHoe || i instanceof ItemArmor;
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
						return Integer.compare(o2.stackSize, o1.stackSize);
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

	/** Suitable for either raw ItemStacks or lists thereof, like what is found inside an OreRecipe. */
	private static class ItemListComparator implements Comparator<Object> {

		@Override
		public int compare(Object o1, Object o2) {
			if (o1 instanceof ItemStack) {
				if (o2 instanceof ItemStack) {
					return comparator.compare((ItemStack)o1, (ItemStack)o2);
				}
				else {
					return Integer.MIN_VALUE;
				}
			}
			else if (o2 instanceof ItemStack) {
				return Integer.MAX_VALUE;
			}
			List<ItemStack> l1 = (List<ItemStack>)o1;
			List<ItemStack> l2 = (List<ItemStack>)o2;
			int fast = Integer.compare(l1.size(), l2.size());
			if (fast != 0)
				return fast;
			ArrayList<ItemStack> li1 = new ArrayList(l1);
			ArrayList<ItemStack> li2 = new ArrayList(l2);
			if (li1.size() > 1) {
				Collections.sort(li1, comparator);
				Collections.sort(li2, comparator);
			}
			for (int i = 0; i < li1.size(); i++) { //must be same size
				ItemStack is1 = li1.get(i);
				ItemStack is2 = li2.get(i);
				int get = comparator.compare(is1, is2);
				if (get != 0)
					return get;
			}
			return 0;
		}

	}

	public static int getIndexOf(List li, ItemStack is) {
		for (int i = 0; i < li.size(); i++) {
			Object o = li.get(i);
			if (o instanceof ItemStack && matchStacks((ItemStack)o, is))
				return i;
		}
		return -1;
	}

	public static void setStackItem(ItemStack is, Item item, int meta) {
		is.func_150996_a(item); //setItem
		is.setItemDamage(meta);
	}

	public static boolean isOreIngot(ItemStack is) {
		for (int id : OreDictionary.getOreIDs(is)) {
			String s = OreDictionary.getOreName(id);
			if (s.startsWith("ingot")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isOreDust(ItemStack is) {
		for (int id : OreDictionary.getOreIDs(is)) {
			String s = OreDictionary.getOreName(id);
			if (s.startsWith("dust")) {
				return true;
			}
		}
		return false;
	}

	public static ItemStack[] createStackArray(Item... items) {
		ItemStack[] ret = new ItemStack[items.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = new ItemStack(items[i]);
		}
		return ret;
	}

	public static Collection<ItemStack> makeListOf(Item... in) {
		Collection<ItemStack> ret = new ArrayList();
		for (Item i : in) {
			ret.add(new ItemStack(i));
		}
		return ret;
	}

	public static Collection<ItemStack> makeListOf(Block... in) {
		Collection<ItemStack> ret = new ArrayList();
		for (Block i : in) {
			ret.add(new ItemStack(i));
		}
		return ret;
	}

	public static boolean isDenseArmor(ItemStack is) {
		if (is.getItem() instanceof ItemArmor) {
			ArmorMaterial am = ((ItemArmor)is.getItem()).getArmorMaterial();
			return am == ArmorMaterial.GOLD || am.getDamageReductionAmount(1) >= 6; //gold or at least 3 hearts worth from chestplate
		}
		return false;
	}

	public static ItemStack cookFood(ItemStack food) {
		ItemStack smelted = FurnaceRecipes.smelting().getSmeltingResult(food);
		return smelted != null ? smelted : food;
	}
}

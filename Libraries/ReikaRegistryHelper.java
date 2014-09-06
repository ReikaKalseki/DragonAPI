/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.ItemBlockCustomLocalization;
import Reika.DragonAPI.Interfaces.BlockEnum;
import Reika.DragonAPI.Interfaces.ItemEnum;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;
import Reika.DragonAPI.ModInteract.LegacyWailaHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public final class ReikaRegistryHelper extends DragonAPICore {

	private static final HashMap<BlockEnum, ArrayList<Integer>> blockVariants = new HashMap();
	private static final HashMap<ItemEnum, ArrayList<Integer>> itemVariants = new HashMap();

	/** Instantiates all blocks and registers them to the game. Uses an Enum[] that implements RegistrationList.
	 * Args: Mod, Enum.values(), Target Block[] array to save instances. */
	public static void instantiateAndRegisterBlocks(DragonAPIMod mod, BlockEnum[] enumr, Block[] target) {
		if (enumr.length != target.length)
			throw new RegistrationException(mod, "Invalid storage array!");
		boolean canLoad = !Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION);
		if (!canLoad)
			throw new RegistrationException(mod, "This mod is loading blocks too late in the setup!");
		for (int i = 0; i < enumr.length; i++) {
			BlockEnum r = enumr[i];
			if (!r.isDummiedOut()) {
				target[i] = ReikaReflectionHelper.createBlockInstance(mod, r);
				String regname = (mod.getTechnicalName()+"_block_"+((Enum)r).name()).toLowerCase();
				if (r.hasItemBlock())
					GameRegistry.registerBlock(target[i], r.getItemBlock(), regname);
				else
					GameRegistry.registerBlock(target[i], ItemBlockCustomLocalization.class, regname);
				int num = r.getNumberMetadatas();
				for (int k = 0; k < num; k++)
					registerBlockVariant(r, k);
				if (r.hasItemBlock())
					mod.getModLogger().log("Instantiating Block "+r.getBasicName()+" with ID "+target[i]+" to Block Variable "+target[i].getClass().getSimpleName()+" (enum index "+i+") with ItemBlock "+r.getItemBlock().getSimpleName());
				else
					mod.getModLogger().log("Instantiating Block "+r.getBasicName()+" with ID "+target[i]+" to Block Variable "+target[i].getClass().getSimpleName()+" (enum index "+i+")");
				if (IWailaDataProvider.class.isAssignableFrom(r.getObjectClass())) {
					LegacyWailaHelper.registerLegacyWAILACompat(r);
				}
			}
			else {
				mod.getModLogger().log("Not instantiating Item "+r.getBasicName()+", as it is dummied out.");
			}
		}
	}

	/** Instantiates all items and registers them to the game. Uses an Enum[] that implements RegistrationList.
	 * Args: Mod, Enum.values(), Target Item[] array to save instances. */
	public static void instantiateAndRegisterItems(DragonAPIMod mod, ItemEnum[] enumr, Item[] target) {
		if (enumr.length != target.length)
			throw new RegistrationException(mod, "Invalid storage array!");
		boolean canLoad = !Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION);
		if (!canLoad)
			throw new RegistrationException(mod, "This mod is loading items too late in the setup!");
		for (int i = 0; i < enumr.length; i++) {
			ItemEnum r = enumr[i];
			if (!r.isDummiedOut()) {
				target[i] = ReikaReflectionHelper.createItemInstance(mod, r);
				String regname = (mod.getTechnicalName()+"_item_"+((Enum)r).name()).toLowerCase();
				int num = r.getNumberMetadatas();
				for (int j = 0; j < num; j++) {
					registerItemVariant(r, j);
				}
				GameRegistry.registerItem(target[i], regname);
				mod.getModLogger().log("Instantiating Item "+r.getBasicName()+" with ID "+target[i]+" to Item Variable "+target[i].getClass().getSimpleName()+" (enum index "+i+"). Has "+enumr[i].getNumberMetadatas()+" metadatas.");
			}
			else {
				mod.getModLogger().log("Not instantiating Item "+r.getBasicName()+", as it is dummied out.");
			}
		}
	}

	private static void registerBlockVariant(BlockEnum e, int meta) {
		ArrayList<Integer> li = blockVariants.get(e);
		if (li == null) {
			li = new ArrayList();
			blockVariants.put(e, li);
		}
		li.add(meta);
	}

	private static void registerItemVariant(ItemEnum e, int meta) {
		ArrayList<Integer> li = itemVariants.get(e);
		if (li == null) {
			li = new ArrayList();
			itemVariants.put(e, li);
		}
		li.add(meta);
	}

	public static int getNumberVariantsFor(BlockEnum b) {
		return blockVariants.containsKey(b) ? blockVariants.get(b).size() : 1;
	}

	public static int getNumberVariantsFor(ItemEnum e) {
		return itemVariants.containsKey(e) ? itemVariants.get(e).size() : 1;
	}

	public static ArrayList<Integer> getVariantsFor(BlockEnum b) {
		ArrayList<Integer> ret = new ArrayList();
		if (blockVariants.containsKey(b)) {
			ArrayList li = blockVariants.get(b);
			ret.addAll(li);
		}
		return ret;
	}

	public static ArrayList<Integer> getVariantsFor(ItemEnum e) {
		ArrayList<Integer> ret = new ArrayList();
		if (itemVariants.containsKey(e)) {
			ArrayList li = itemVariants.get(e);
			ret.addAll(li);
		}
		return ret;
	}

	public static void loadNames() {/*
		for (BlockEnum b : blockVariants.keySet()) {
			ArrayList<Integer> metas = blockVariants.get(b);
			if (metas == null || metas.isEmpty()) {
				LanguageRegistry.addName(b.getBlockInstance(), b.getBasicName());
			}
			else {
				for (int i = 0; i < metas.size(); i++) {
					int meta = metas.get(i);
					ItemStack is = new ItemStack(b.getBlockInstance(), 1, meta);
					LanguageRegistry.addName(is, b.getMultiValuedName(meta));
				}
			}
		}

		for (ItemEnum e : itemVariants.keySet()) {
			ArrayList<Integer> metas = itemVariants.get(e);
			if (metas == null || metas.isEmpty()) {
				LanguageRegistry.addName(e.getItemInstance(), e.getBasicName());
			}
			else {
				for (int i = 0; i < metas.size(); i++) {
					int meta = metas.get(i);
					ItemStack is = new ItemStack(e.getItemInstance(), 1, meta);
					LanguageRegistry.addName(is, e.getMultiValuedName(meta));
				}
			}
		}*/

		for (BlockEnum b : blockVariants.keySet()) {
			Item item = Item.getItemFromBlock(b.getBlockInstance());
			if (item instanceof ItemBlockCustomLocalization) {
				((ItemBlockCustomLocalization)item).setEnumObject(b);
			}
			else {

			}
		}
	}

	public static LoaderState getForgeLoadState() {
		LoaderState[] list = LoaderState.values();
		for (int i = list.length-1; i >= 0; i--) {
			if (Loader.instance().hasReachedState(list[i]))
				return list[i];
		}
		return list[0];
	}

	public static void setupModData(DragonAPIMod mod, FMLPreInitializationEvent evt) {
		ModMetadata dat = evt.getModMetadata();
		dat.authorList.clear();
		dat.authorList.add(mod.getModAuthorName());
	}

	public static void setupVersionChecking(FMLPreInitializationEvent evt) {
		if (ReikaObfuscationHelper.isDeObfEnvironment())
			return;
		File f = evt.getSourceFile();
		String hash = ReikaFileReader.getHash(f);
		ModMetadata meta = evt.getModMetadata();
		meta.version = hash;
	}

	/** Overrides one block in the Block database with another. The new block must
	 * have an ID, material constructor! Returns true if successful. */
	public static boolean overrideBlock(DragonAPIMod mod, String blockField, Class<?extends Block> toOverride) {
		mod.getModLogger().log("Overriding Blocks."+blockField+" with "+toOverride);
		try {
			Field f = Blocks.class.getField(blockField);
			Block target = (Block)f.get(null);
			Constructor c = toOverride.getConstructor(Material.class);
			Block block = (Block)c.newInstance(target.getMaterial());
			block.setTickRandomly(target.getTickRandomly());
			block.setBlockName(target.getUnlocalizedName().substring(5));
			block.setLightOpacity(target.getLightOpacity());
			//block.opaqueCubeLookup[target.blockID] = target.isOpaqueCube();
			block.setLightLevel(block.getLightValue());
			block.slipperiness = target.slipperiness;
			block.blockHardness = target.blockHardness;
			block.blockResistance = target.blockResistance;
			int id = Block.getIdFromBlock(target);
			String name = Block.blockRegistry.getNameForObject(target);
			Block.blockRegistry.addObject(id, name, block);
			ReikaReflectionHelper.setFinalField(f, null, block);
			return true;
		}
		catch (Exception e) {
			mod.getModLogger().logError("Could not override Blocks."+blockField+" with "+toOverride);
			e.printStackTrace();
			return false;
		}
	}
}

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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public final class ReikaRegistryHelper extends DragonAPICore {

	/** Instantiates all blocks and registers them to the game. Uses an Enum[] that implements RegistrationList.
	 * Args: Mod, Enum.values(), Target Block[] array to save instances. */
	public static void instantiateAndRegisterBlocks(DragonAPIMod mod, RegistrationList[] enumr, Block[] target) {
		boolean canLoad = !Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION);
		if (!canLoad)
			throw new RegistrationException(mod, "This mod is loading blocks too late in the setup!");
		for (int i = 0; i < enumr.length; i++) {
			if (!enumr[i].isDummiedOut()) {
				target[i] = ReikaReflectionHelper.createBlockInstance(mod, enumr[i]);
				String regname = enumr[i].getBasicName().toLowerCase().replaceAll("\\s","");
				if (enumr[i].hasItemBlock())
					GameRegistry.registerBlock(target[i], enumr[i].getItemBlock(), regname);
				else
					GameRegistry.registerBlock(target[i], regname);
				if (enumr[i].hasMultiValuedName()) {
					for (int k = 0; k < enumr[i].getNumberMetadatas(); k++)
						LanguageRegistry.addName(new ItemStack(target[i].blockID, 1, k), enumr[i].getMultiValuedName(k));
				}
				LanguageRegistry.addName(target[i], enumr[i].getBasicName());
				if (enumr[i].hasItemBlock())
					mod.getModLogger().log("Instantiating Block "+enumr[i].getBasicName()+" with ID "+target[i].blockID+" to Block Variable "+target[i].getClass().getSimpleName()+" (enum index "+i+") with ItemBlock "+enumr[i].getItemBlock().getSimpleName());
				else
					mod.getModLogger().log("Instantiating Block "+enumr[i].getBasicName()+" with ID "+target[i].blockID+" to Block Variable "+target[i].getClass().getSimpleName()+" (enum index "+i+")");
			}
			else {
				mod.getModLogger().log("Not instantiating Item "+enumr[i].getBasicName()+", as it is dummied out.");
			}
		}
	}

	/** Instantiates all items and registers them to the game. Uses an Enum[] that implements RegistrationList.
	 * Args: Mod, Enum.values(), Target Item[] array to save instances. */
	public static void instantiateAndRegisterItems(DragonAPIMod mod, RegistrationList[] enumr, Item[] target) {
		for (int i = 0; i < enumr.length; i++) {
			if (!enumr[i].isDummiedOut()) {
				target[i] = ReikaReflectionHelper.createItemInstance(mod, enumr[i]);
				RegistrationList r = enumr[i];
				if (r.hasMultiValuedName()) {
					for (int j = 0; j < r.getNumberMetadatas(); j++) {
						ItemStack is = new ItemStack(target[i].itemID, 1, j);
						LanguageRegistry.addName(is, r.getMultiValuedName(j));
					}
				}
				else
					LanguageRegistry.addName(target[i], r.getBasicName());
				mod.getModLogger().log("Instantiating Item "+enumr[i].getBasicName()+" with ID "+target[i].itemID+" to Item Variable "+target[i].getClass().getSimpleName()+" (enum index "+i+"). Has "+enumr[i].getNumberMetadatas()+" metadatas.");
			}
			else {
				mod.getModLogger().log("Not instantiating Item "+enumr[i].getBasicName()+", as it is dummied out.");
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
		mod.getModLogger().log("Overriding Block."+blockField+" with "+toOverride);
		try {
			Field f = Block.class.getField(blockField);
			Block target = (Block)f.get(null);
			Constructor c = toOverride.getConstructor(int.class, Material.class);
			Block.blocksList[target.blockID] = null;
			Block block = (Block)c.newInstance(target.blockID, target.blockMaterial);
			block.setTickRandomly(target.getTickRandomly());
			block.setUnlocalizedName(target.getUnlocalizedName().substring(5));
			block.setLightOpacity(Block.lightOpacity[target.blockID]);
			Block.opaqueCubeLookup[target.blockID] = target.isOpaqueCube();
			block.setLightValue(Block.lightOpacity[target.blockID]);
			block.slipperiness = target.slipperiness;
			block.blockHardness = target.blockHardness;
			block.blockResistance = target.blockResistance;
			Block.blocksList[target.blockID] = block;
			ReikaReflectionHelper.setFinalField(f, null, block);
			return true;
		}
		catch (Exception e) {
			mod.getModLogger().logError("Could not override Block."+blockField+" with "+toOverride);
			e.printStackTrace();
			return false;
		}
	}
}

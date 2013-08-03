/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.ModLogger;
import Reika.DragonAPI.Interfaces.RegistrationList;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public final class ReikaRegistryHelper extends DragonAPICore {

	/** Instantiates all blocks and registers them to the game. Uses an Enum[] that implements RegistrationList.
	 * Args: Mod, Enum.values(), Target Block[] array to save instances. */
	public static void instantiateAndRegisterBlocks(DragonAPIMod mod, RegistrationList[] enumr, Block[] target, ModLogger log) {
		for (int i = 0; i < enumr.length; i++) {
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
			if (log == null)
				return;
			if (enumr[i].hasItemBlock())
				log.log(mod.getDisplayName().toUpperCase()+": Instantiating Block "+enumr[i].getBasicName()+" with ID "+target[i].blockID+" to Block Variable "+target[i].getClass().getSimpleName()+" (slot "+i+") with ItemBlock "+enumr[i].getItemBlock().getSimpleName());
			else
				log.log(mod.getDisplayName().toUpperCase()+": Instantiating Block "+enumr[i].getBasicName()+" with ID "+target[i].blockID+" to Block Variable "+target[i].getClass().getSimpleName()+" (slot "+i+")");
		}
	}

	/** Instantiates all items and registers them to the game. Uses an Enum[] that implements RegistrationList.
	 * Args: Mod, Enum.values(), Target Item[] array to save instances. */
	public static void instantiateAndRegisterItems(DragonAPIMod mod, RegistrationList[] enumr, Item[] target, ModLogger log) {
		for (int i = 0; i < enumr.length; i++) {
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
			if (log != null)
				log.log(mod.getDisplayName().toUpperCase()+": Instantiating Item "+enumr[i].getBasicName()+" with ID "+target[i].itemID+" to Item Variable "+target[i].getClass().getSimpleName()+" (slot "+i+")");
		}
	}
}

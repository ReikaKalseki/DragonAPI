/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Method;

import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Interfaces.Registry.RegistrationList;
import Reika.DragonAPI.Interfaces.Registry.RegistryEntry;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;

public class ReikaEEHelper {

	private static Method configRemove;

	public static void blacklistItemStack(ItemStack is) {
		if (!isLoaded())
			return;
		if (ModList.PROJECTE.isLoaded()) {
			registerCustomEMC(is, 0);
			removeFromConfig(is);
		}
	}

	public static void blacklistBlock(Block item) {
		if (!isLoaded())
			return;
		RegistryEntry e = ReikaRegistryHelper.getRegistryForObject(item);
		if (e instanceof RegistrationList) {
			blacklistEntry((RegistrationList)e);
		}
		else {
			for (int i = 0; i < 32768; i++) {
				blacklistItemStack(new ItemStack(item, 1, i));
			}
		}
	}

	public static void blacklistItem(Item item) {
		if (!isLoaded())
			return;
		RegistryEntry e = ReikaRegistryHelper.getRegistryForObject(item);
		if (e instanceof RegistrationList) {
			blacklistEntry((RegistrationList)e);
		}
		else {
			for (int i = 0; i < 32768; i++) {
				blacklistItemStack(new ItemStack(item, 1, i));
			}
		}
	}

	public static void blacklistEntry(RegistrationList reg) {
		if (!isLoaded())
			return;
		for (int i = 0; i < reg.getNumberMetadatas(); i++) {
			blacklistItemStack(reg.getStackOfMetadata(i));
		}
	}

	/** Do not use until the EMC registry supports wildcards! */
	public static void blacklistRegistry(RegistrationList[] reg) {
		if (!isLoaded())
			return;
		for (int i = 0; i < reg.length; i++) {
			blacklistEntry(reg[i]);
		}
	}

	public static boolean isLoaded() {
		return ModList.PROJECTE.isLoaded();
	}

	public static void registerCustomEMC(ItemStack is, int emc) {
		if (!ModList.PROJECTE.isLoaded()) {
			try {
				ProjectEAPI.getEMCProxy().registerCustomEMC(is, emc);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void removeFromConfig(ItemStack is) {
		try {
			configRemove.invoke(null, Item.itemRegistry.getNameForObject(is.getItem()), is.getItemDamage());
		}
		catch (Exception e) {
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.PROJECTE, e);
		}
	}

	static {
		if (ModList.PROJECTE.isLoaded()) {
			try {
				Class c = Class.forName("moze_intel.projecte.config.CustomEMCParser");
				configRemove = c.getDeclaredMethod("removeFromFile", String.class, int.class);
				configRemove.setAccessible(true);
			}
			catch (Exception e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.PROJECTE, e);
			}
		}
	}

}

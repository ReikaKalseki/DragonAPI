/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModInteract.TooltipOccluder;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

/** Allows you to hide items from NEI without having to write a special class implementing {@link codechicken.nei.api.IConfigureNEI}.
 * Only call this class if NEI is loaded, or you will create an NEI dependence in your mod. */
public class NEI_DragonAPI_Config implements IConfigureNEI {

	private static final ArrayList<ItemStack> items = new ArrayList();

	private static final TooltipOccluder occlusion = new TooltipOccluder();

	public static void hideBlocks(Block... block) {
		for (int i = 0; i < block.length; i++)
			hideBlock(block[i]);
	}

	public static void hideBlock(Block block) {
		hideItem(new ItemStack(block));
	}

	public static void hideItems(Item... item) {
		for (int i = 0; i < item.length; i++)
			hideItem(item[i]);
	}

	public static void hideItem(Item item) {
		hideItem(new ItemStack(item));
	}

	public static void hideItem(ItemStack item) {
		items.add(item);
	}

	@Override
	public void loadConfig() {
		API.registerNEIGuiHandler(occlusion);

		for (int i = 0; i < items.size(); i++) {
			ItemStack id = items.get(i);
			API.hideItem(id);
		}
	}

	@Override
	public String getName() {
		return "DragonAPI Anonymous Item Hiding Utility";
	}

	@Override
	public String getVersion() {
		return "None";
	}

}

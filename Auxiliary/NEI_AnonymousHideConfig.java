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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

/** Allows you to hide items from NEI without having to write a special class implementing {@link codechicken.nei.api.IConfigureNEI}.
 * Only call this class if NEI is loaded, or you will create an NEI dependence in your mod. */
public class NEI_AnonymousHideConfig implements IConfigureNEI {

	private static final ArrayList<Integer> items = new ArrayList();

	public static void addID(int id) {
		items.add(id);
	}

	public static void addBlocks(Block[] block) {
		for (int i = 0; i < block.length; i++)
			addBlock(block[i]);
	}

	public static void addBlock(Block block) {
		addID(block.blockID);
	}

	public static void addItems(Item[] item) {
		for (int i = 0; i < item.length; i++)
			addItem(item[i]);
	}

	public static void addItem(Item item) {
		addID(item.itemID);
	}

	public static void addItemStack(ItemStack item) {
		addID(item.itemID);
	}

	@Override
	public void loadConfig() {
		for (int i = 0; i < items.size(); i++) {
			int id = items.get(i);
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

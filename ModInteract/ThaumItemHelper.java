/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import cpw.mods.fml.common.registry.GameRegistry;

public class ThaumItemHelper {

	public static enum ItemEntry {
		ALUMENTUM("ItemResource", 0),
		NITOR("ItemResource", 1),
		THAUMIUM("ItemResource", 2),
		TALLOW("ItemResource", 4),
		FABRIC("ItemResource", 7),
		VISFITLER("ItemResource", 8),
		FRAGMENT("ItemResource", 9),
		MIRROR("ItemResource", 10),
		GOO("ItemResource", 11),
		TENDRIL("ItemResource", 12),
		LABEL("ItemResource", 13),
		SALIS("ItemResource", 14),
		CHARM("ItemResource", 15),
		VOIDMETAL("ItemResource", 16),
		VOIDSEED("ItemResource", 17),
		PRIMALFOCUS("FocusPrimal"),
		SALTS("ItemBathSalts"),
		ELDRITCHEYE("ItemEldritchObject"),
		;

		public final int metadata;
		private final String item;

		private ItemEntry(String s) {
			this(s, 0);
		}

		private ItemEntry(String s, int meta) {
			item = s;
			metadata = meta;
		}

		public ItemStack getItem() {
			ItemStack is = GameRegistry.findItemStack(ModList.THAUMCRAFT.modLabel, item, 1);
			return new ItemStack(is.getItem(), 1, metadata);
		}
	}

	public static enum BlockEntry {
		CRYSTALCORE("blockCrystal", 6),
		;

		public final int metadata;
		private final String item;

		private BlockEntry(String s, int meta) {
			item = s;
			metadata = meta;
		}

		public ItemStack getItem() {
			Block b = GameRegistry.findBlock(ModList.THAUMCRAFT.modLabel, item);
			return new ItemStack(b, 1, metadata);
		}
	}

}

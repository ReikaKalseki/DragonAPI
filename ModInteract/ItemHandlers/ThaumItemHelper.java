/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.IWarpingGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
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
		BALANCED("ItemShard", 6),
		PHIAL("ItemEssence", 0),
		CRYSTALESSENCE("ItemCrystalEssence", 0),
		NUGGETCLUSTER("ItemNugget", 0),
		LOOTBAG1("ItemLootBag", 0),
		LOOTBAG2("ItemLootBag", 1),
		LOOTBAG3("ItemLootBag", 2),
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
			Item is = GameRegistry.findItem(ModList.THAUMCRAFT.modLabel, item);
			return new ItemStack(is, 1, metadata);
		}
	}

	public static enum BlockEntry {
		TOTEM("blockCosmeticSolid", 0),
		TILE("blockCosmeticSolid", 1),
		THAUMIUM("blockCosmeticSolid", 4),
		TALLOW("blockCosmeticSolid", 5),
		ARCANESTONE("blockCosmeticSolid", 6),
		ARCANEBRICKS("blockCosmeticSolid", 7),
		ANCIENTSTONE("blockCosmeticSolid", 11),
		ANCIENTROCK("blockCosmeticSolid", 12),
		CRUSTEDSTONE("blockCosmeticSolid", 14),
		ANCIENTPEDESTAL("blockCosmeticSolid", 15),
		SHIMMER("blockCustomPlant", 2),
		CINDER("blockCustomPlant", 3),
		ETHEREAL("blockCustomPlant", 4),
		CRYSTAL("blockCrystal"),
		JAR("blockJar"),
		NODE("blockAiry", 0),
		TOTEMNODE("blockAiry", 4),
		;

		public final int metadata;
		private final String item;

		private BlockEntry(String s) {
			this(s, 0);
		}

		private BlockEntry(String s, int meta) {
			item = s;
			metadata = meta;
		}

		public Block getBlock() {
			return GameRegistry.findBlock(ModList.THAUMCRAFT.modLabel, item);
		}

		public ItemStack getItem() {
			return new ItemStack(this.getBlock(), 1, metadata);
		}

		public boolean match(Block b, int meta) {
			return b == this.getBlock() && meta == metadata;
		}
	}

	public static boolean isTotemBlock(Block b, int meta) {
		return (b == BlockEntry.TOTEM.getBlock() && meta < 2) || (b == BlockEntry.TOTEMNODE.getBlock() && meta == BlockEntry.TOTEMNODE.metadata);
	}

	public static boolean isCrystalCluster(Block b) {
		return b == BlockEntry.CRYSTAL.getBlock();
	}

	public static boolean isWarpingTool(ItemStack is) {
		return is != null && is.getItem() instanceof IWarpingGear;
	}

	public static boolean isVoidMetalTool(ItemStack is) {
		return is != null && is.getItem().getClass().getName().startsWith("thaumcraft.common.items.equipment.ItemVoid");
	}

	public static ItemStack getCrystallizedEssentia(Aspect a) {
		ItemStack is = ItemEntry.CRYSTALESSENCE.getItem();
		((IEssentiaContainerItem)is.getItem()).setAspects(is, new AspectList().add(a, 1));
		return is;
	}

}

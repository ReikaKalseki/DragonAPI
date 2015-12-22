/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

public class ForestryHandler extends ModHandlerBase {

	private boolean init = false;

	private static final ForestryHandler instance = new ForestryHandler();

	public enum ItemEntry {
		APATITE("forestry.core.items.ItemRegistryCore", "apatite"),
		FERTILIZER("forestry.core.items.ItemRegistryCore", "fertilizerCompound"),
		SAPLING("forestry.arboriculture.items.ItemRegistryArboriculture", "sapling"),
		COMB("forestry.apiculture.items.ItemRegistryApiculture", "beeComb"),
		HONEY("forestry.apiculture.items.ItemRegistryApiculture", "honeyDrop"),
		HONEYDEW("forestry.apiculture.items.ItemRegistryApiculture", "honeydew"),
		JELLY("forestry.apiculture.items.ItemRegistryApiculture", "royalJelly"),
		PROPOLIS("forestry.apiculture.items.ItemRegistryApiculture", "propolis"),
		WAX("forestry.core.items.ItemRegistryCore", "beeswax"),
		REFWAX("forestry.core.items.ItemRegistryCore", "refractoryWax"),
		POLLEN("forestry.apiculture.items.ItemRegistryApiculture", "pollenCluster"),
		TREEPOLLEN("forestry.arboriculture.items.ItemRegistryArboriculture", "pollenFertile"),
		QUEEN("forestry.apiculture.items.ItemRegistryApiculture", "beeQueenGE"),
		PRINCESS("forestry.apiculture.items.ItemRegistryApiculture", "beePrincessGE"),
		DRONE("forestry.apiculture.items.ItemRegistryApiculture", "beeDroneGE"),
		LARVA("forestry.apiculture.items.ItemRegistryApiculture", "beeLarvaeGE");

		private final String reg;
		private final String tag;
		private Item item;

		private static final ItemEntry[] list = values();

		private ItemEntry(String c, String id) {
			reg = c;
			tag = id;
		}

		public Item getItem() {
			return item;
		}

		private Object getRegistryObject() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
			String n = reg.split("\\.")[1];
			n = ReikaStringParser.capFirstChar(n);
			n = "forestry.plugins.Plugin"+n;
			Class c = Class.forName(n);
			Field f = c.getDeclaredField("items");
			f.setAccessible(true);
			return f.get(null);
		}
	}

	public enum BlockEntry {
		SAPLING("forestry.arboriculture.blocks.BlockRegistryArboriculture", "saplingGE"),
		LEAF("forestry.arboriculture.blocks.BlockRegistryArboriculture", "leaves"),
		LOG("forestry.arboriculture.blocks.BlockRegistryArboriculture", "logs"),
		HIVE("forestry.apiculture.blocks.BlockRegistryApiculture", "beehives");

		private final String reg;
		private final String tag;
		private Block item;

		private static final BlockEntry[] list = values();

		private BlockEntry(String c, String id) {
			reg = c;
			tag = id;
		}

		public Block getBlock() {
			return item;
		}

		private Object getRegistryObject() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
			String n = reg.split("\\.")[1];
			n = ReikaStringParser.capFirstChar(n);
			n = "forestry.plugins.Plugin"+n;
			Class c = Class.forName(n);
			Field f = c.getDeclaredField("blocks");
			f.setAccessible(true);
			return f.get(null);
		}
	}

	private ForestryHandler() {
		super();
		if (this.hasMod()) {
			for (int i = 0; i < ItemEntry.list.length; i++) {
				ItemEntry ie = ItemEntry.list[i];
				try {
					Class c = Class.forName(ie.reg);
					Field f = c.getDeclaredField(ie.tag); //is no longer enum object
					Object reg = ie.getRegistryObject();
					ie.item = (Item)f.get(reg);
				}
				catch (NoSuchFieldException e) {
					DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (IllegalArgumentException e) {
					DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (IllegalAccessException e) {
					DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (NullPointerException e) {
					DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (ClassNotFoundException e) {
					DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
				}
			}

			for (int i = 0; i < BlockEntry.list.length; i++) {
				BlockEntry ie = BlockEntry.list[i];
				try {
					Class c = Class.forName(ie.reg);
					Field f = c.getDeclaredField(ie.tag); //is no longer enum object
					Object reg = ie.getRegistryObject();
					ie.item = (Block)f.get(reg);
				}
				catch (NoSuchFieldException e) {
					DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (IllegalArgumentException e) {
					DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (IllegalAccessException e) {
					DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (NullPointerException e) {
					DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (ClassNotFoundException e) {
					DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
				}
			}

			init = true;
		}
		else {
			this.noMod();
		}
	}

	public static ForestryHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return init;
	}

	@Override
	public ModList getMod() {
		return ModList.FORESTRY;
	}

	public enum Combs {

		HONEY(0),
		SIMMERING(2),
		STRINGY(3),
		FROZEN(4),
		DRIPPING(5),
		SILKY(6),
		PARCHED(7),
		MOSSY(15),
		MELLOW(16);

		public final int damageValue;

		private Combs(int dmg) {
			damageValue = dmg;
		}

		public ItemStack getItem() {
			return new ItemStack(ItemEntry.COMB.getItem(), 1, damageValue);
		}
	}

}

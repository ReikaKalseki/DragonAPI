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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ForestryHandler extends ModHandlerBase {

	private boolean init = false;

	private static final ForestryHandler instance = new ForestryHandler();

	public enum ItemEntry {
		APATITE("apatite"),
		FERTILIZER("fertilizerCompound"),
		SAPLING("sapling"),
		COMB("beeComb"),
		HONEY("honeyDrop"),
		HONEYDEW("honeydew"),
		JELLY("royalJelly"),
		PROPOLIS("propolis"),
		WAX("beeswax"),
		POLLEN("pollenCluster"),
		TREEPOLLEN("pollenFertile"),
		QUEEN("beeQueenGE"),
		PRINCESS("beePrincessGE"),
		DRONE("beeDroneGE"),
		LARVA("beeLarvaeGE");

		private final String tag;
		private Item item;

		private static final ItemEntry[] list = values();

		private ItemEntry(String id) {
			tag = id;
		}

		public Item getItem() {
			return item;
		}
	}

	public enum BlockEntry {
		SAPLING("saplingGE"),
		LEAF("leaves"),
		LOG1("log1"),
		LOG2("log2"),
		LOG3("log3"),
		LOG4("log4"),
		LOG5("log5"),
		LOG6("log6"),
		LOG7("log7"),
		LOG8("log8"),
		HIVE("beehives");

		private final String tag;
		private Block item;

		private static final BlockEntry[] list = values();

		private BlockEntry(String id) {
			tag = id;
		}

		public Block getBlock() {
			return item;
		}

		public boolean isLog() {
			return this.name().toLowerCase().startsWith("log");
		}
	}

	private ForestryHandler() {
		super();
		if (this.hasMod()) {
			try {

				Class forest = this.getMod().getItemClass();
				Method get = forest.getMethod("item");
				for (int i = 0; i < ItemEntry.list.length; i++) {
					ItemEntry ie = ItemEntry.list[i];
					Field f = forest.getDeclaredField(ie.tag); //is enum object now
					Object entry = f.get(null);
					Item item = (Item)get.invoke(entry);
					ie.item = item;
				}

				Class blocks = this.getMod().getBlockClass();
				get = blocks.getMethod("block");
				for (int i = 0; i < BlockEntry.list.length; i++) {
					BlockEntry ie = BlockEntry.list[i];
					Field f = blocks.getDeclaredField(ie.tag); //is enum object now
					Object entry = f.get(null);
					Block b = (Block)get.invoke(entry);
					ie.item = b;
				}

				init = true;
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (NoSuchMethodException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" method not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal argument for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (NullPointerException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Invocation target exception for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
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

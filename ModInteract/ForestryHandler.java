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

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ForestryHandler extends ModHandlerBase {

	public final Item apatiteID;
	public final Item fertilizerID;
	public final Block saplingID;
	public final Item combID;

	private static final ForestryHandler instance = new ForestryHandler();

	private ForestryHandler() {
		super();
		Item idapatite = null;
		Item idfertilizer = null;
		Block idsapling = null;
		Item idcomb = null;
		if (this.hasMod()) {
			try {
				Class forest = this.getMod().getItemClass();
				Field apa = forest.getField("apatite"); //is enum object now
				Object entry = apa.get(null);
				Method get = forest.getMethod("item");
				Item item = (Item)get.invoke(entry);
				idapatite = item;

				Field fert = forest.getField("fertilizerCompound"); //is enum object now
				entry = fert.get(null);
				item = (Item)get.invoke(entry);
				idfertilizer = item;

				Field comb = forest.getField("beeComb"); //is enum object now
				entry = comb.get(null);
				item = (Item)get.invoke(entry);
				idcomb = item;

				Class blocks = this.getMod().getBlockClass();
				Field sapling = blocks.getField("saplingGE");
				Block s = (Block)sapling.get(null);
				idsapling = s;
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

		apatiteID = idapatite;
		saplingID = idsapling;
		fertilizerID = idfertilizer;
		combID = idcomb;
	}

	public static ForestryHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return apatiteID != null && saplingID != null && fertilizerID != null && combID != null;
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
			return new ItemStack(instance.combID, 1, damageValue);
		}
	}

}
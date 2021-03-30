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

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.ModInteract.ItemHandlers.HexcraftHandler.HexHandler;

import cpw.mods.fml.common.registry.GameRegistry;

public class HexBlockHandlerSimple extends ModHandlerBase implements HexHandler {

	private static final HexBlockHandlerSimple instance = new HexBlockHandlerSimple();

	public static enum BasicHexColors {
		RED,
		GREEN,
		BLUE,
		WHITE,
		BLACK,
		;

		private Item crystal;
		private Block block;
		private Block netherBlock;

		public static final BasicHexColors[] list = values();

		private static final HashMap<Item, BasicHexColors> lookup = new HashMap();

		public Item getCrystal() {
			return crystal;
		}

		public boolean isPrimary(boolean nether) {
			return nether ? this == WHITE || this == BLACK : !this.isPrimary(true);
		}

		public static BasicHexColors getColorForItem(ItemStack is) {
			return lookup.get(is.getItem());
		}
	}

	private HexBlockHandlerSimple() {
		super();

		if (this.hasMod()) {
			try {
				/*
				Class blocks = this.getMod().getBlockClass();
				Field ore = blocks.getField("MimichiteOre");
				idstone = ((Block)ore.get(null));
				 */

				Class items = this.getMod().getItemClass();
				for (BasicHexColors hex : BasicHexColors.values()) {
					String key = ReikaStringParser.capFirstChar(hex.name());
					Field item = items.getField("itemHexoriumCrystal"+key);
					item.setAccessible(true);
					hex.crystal = ((Item)item.get(null));
					hex.block = GameRegistry.findBlock(this.getMod().modLabel, "blockHexoriumMonolith"+key);
					hex.netherBlock = GameRegistry.findBlock(this.getMod().modLabel, "blockHexoriumNetherMonolith"+key);
					BasicHexColors.lookup.put(hex.crystal, hex);
					BasicHexColors.lookup.put(Item.getItemFromBlock(hex.block), hex);
					BasicHexColors.lookup.put(Item.getItemFromBlock(hex.netherBlock), hex);
				}
			}
			catch (NoSuchFieldException e) {
				DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (SecurityException e) {
				DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
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

			HexcraftHandler.setHandler(this);
		}
		else {
			this.noMod();
		}
	}

	public static HexBlockHandlerSimple getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		for (BasicHexColors c : BasicHexColors.list) {
			if (c.block == null || c.netherBlock == null || c.crystal == null)
				return false;
		}
		return true;
	}

	@Override
	public ModList getMod() {
		return ModList.HEXCRAFT;
	}

	public boolean isMonolith(Block b) {
		for (BasicHexColors c : BasicHexColors.list) {
			if (c.block == b || c.netherBlock == b)
				return true;
		}
		return false;
	}

}

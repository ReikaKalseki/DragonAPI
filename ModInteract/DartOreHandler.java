/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModRegistry.ModOreList;
import cpw.mods.fml.common.registry.GameRegistry;

public final class DartOreHandler extends ModHandlerBase {

	private static final DartOreHandler instance = new DartOreHandler();

	public final int oreID;
	public final int gemID;

	private final ItemStack oreItem;
	private final ItemStack gemItem;

	private boolean isOreDict = false;

	private DartOreHandler() {
		super();
		int idgem = -1;
		int idore = -1;
		if (this.hasMod()) {
			try {
				Class block = this.getMod().getBlockClass();
				Class item = Class.forName("bluedart.item.DartItem");
				Field ore = block.getField("powerOre");
				//Field force = item.getField("gemForce");
				Block powerOre = (Block)ore.get(null);
				//idgem = ((Item)force.get(null)).itemID;
				idore = powerOre.blockID;
				//idgem = DartItem.gemForce.itemID;
				idgem = GameRegistry.findItemStack("DartCraft", "item.gemForce", 1).itemID;
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: DartCraft Item class not found! Cannot read its items!");
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
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
		}
		else {
			this.noMod();
		}

		gemID = idgem;
		gemItem = new ItemStack(gemID, 1, 0);
		oreID = idore;
		oreItem = new ItemStack(oreID, 1, 0);
	}

	public static DartOreHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return gemID != -1 && oreID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.DARTCRAFT;
	}

	public ItemStack getOre() {
		if (!this.initializedProperly())
			return null;
		return oreItem.copy();
	}

	public ItemStack getForceGem() {
		if (!this.initializedProperly())
			return null;
		return gemItem.copy();
	}

	public boolean isDartOre(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return block.itemID == oreID;
	}

	public void forceOreRegistration() {
		if (!isOreDict) {
			ItemStack overworld = new ItemStack(oreID, 1, 0);
			ItemStack nether = new ItemStack(oreID, 1, 1);
			OreDictionary.registerOre(ModOreList.FORCE.getOreDictNames()[0], overworld);
			OreDictionary.registerOre(ModOreList.FORCE.getOreDictNames()[0], nether);
			ModOreList.FORCE.reloadOreList();
			ReikaJavaLibrary.pConsole("DRAGONAPI: Power ore registered to ore dictionary!");
			isOreDict = true;
		}
		else {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Power ore already registered to ore dictionary! No action taken!");
			Thread.dumpStack();
		}
	}

}

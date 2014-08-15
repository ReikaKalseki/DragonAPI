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
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;

public final class DartOreHandler extends ModHandlerBase {

	private static final DartOreHandler instance = new DartOreHandler();

	public final Block oreID;
	public final Item gemID;

	private final ItemStack oreItem;
	private final ItemStack gemItem;

	private boolean isOreDict = false;

	private DartOreHandler() {
		super();
		Item idgem = null;
		Block idore = null;
		if (this.hasMod()) {
			try {
				Class block = this.getMod().getBlockClass();
				Class item = Class.forName("bluedart.Items.DartItem");
				Field ore = block.getField("powerOre");
				//Field force = Items.getField("gemForce");
				Block powerOre = (Block)ore.get(null);
				//idgem = ((Item)force.get(null)).itemID;
				idore = powerOre;
				//idgem = DartItems.gemForce.itemID;
				idgem = GameRegistry.findItemStack("DartCraft", "Items.gemForce", 1).getItem();
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
		return gemID != null && oreID != null;
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
		return ReikaItemHelper.matchStackWithBlock(block, oreID);
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
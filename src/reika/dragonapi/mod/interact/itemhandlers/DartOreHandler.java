/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.itemhandlers;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.base.ModHandlerBase;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.dragonapi.mod.registry.ModOreList;
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
				DragonAPICore.logError("DartCraft Item class not found! Cannot read its items!");
				e.printStackTrace();
				this.logFailure(e);
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
			ModOreList.FORCE.initialize();
			DragonAPICore.log("Power ore registered to ore dictionary!");
			isOreDict = true;
		}
		else {
			DragonAPICore.logError("Power ore already registered to ore dictionary! No action taken!");
			ReikaJavaLibrary.dumpStack();
		}
	}

}

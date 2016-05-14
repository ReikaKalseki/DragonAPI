/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;

public class IC2Handler extends ModHandlerBase {

	public final Item iridiumID;
	public final Item scrapID;
	public final Item scrapBoxID;

	private ItemStack purifiedUranium;

	private static final IC2Handler instance = new IC2Handler();

	private IC2Handler() {
		super();
		Item idiridium = null;
		Item idscrap = null;
		Item idscrapbox = null;
		if (this.hasMod()) {
			try {
				Class ic2 = this.getMod().getItemClass();

				Field f = ic2.getField("iridiumOre");
				ItemStack is = (ItemStack)f.get(null);
				idiridium = is.getItem();

				Field crush = ic2.getField("purifiedCrushedUraniumOre");
				ItemStack pureCrushU = (ItemStack)crush.get(null);
				purifiedUranium = pureCrushU;

				f = ic2.getField("scrap");
				is = (ItemStack)f.get(null);
				idscrap = is.getItem();

				f = ic2.getField("scrapBox");
				is = (ItemStack)f.get(null);
				idscrapbox = is.getItem();
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

		iridiumID = idiridium;
		scrapBoxID = idscrapbox;
		scrapID = idscrap;
	}

	public static IC2Handler getInstance() {
		return instance;
	}

	public ItemStack getPurifiedCrushedUranium() {
		return this.initializedProperly() ? purifiedUranium.copy() : null;
	}

	@Override
	public boolean initializedProperly() {
		return iridiumID != null && purifiedUranium != null && scrapBoxID != null && scrapID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.IC2;
	}

}

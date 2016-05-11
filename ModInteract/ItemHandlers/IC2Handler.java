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

	private ItemStack purifiedUranium;

	private static final IC2Handler instance = new IC2Handler();

	private IC2Handler() {
		super();
		Item idiridium = null;
		if (this.hasMod()) {
			try {
				Class ic2 = this.getMod().getItemClass();
				Field irid = ic2.getField("iridiumOre");
				Field crush = ic2.getField("purifiedCrushedUraniumOre");
				ItemStack iridium = (ItemStack)irid.get(null);
				idiridium = iridium.getItem();
				ItemStack pureCrushU = (ItemStack)crush.get(null);
				purifiedUranium = pureCrushU;
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
	}

	public static IC2Handler getInstance() {
		return instance;
	}

	public ItemStack getPurifiedCrushedUranium() {
		return this.initializedProperly() ? purifiedUranium.copy() : null;
	}

	@Override
	public boolean initializedProperly() {
		return iridiumID != null && purifiedUranium != null;
	}

	@Override
	public ModList getMod() {
		return ModList.IC2;
	}

}

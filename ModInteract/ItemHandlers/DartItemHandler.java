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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;

public final class DartItemHandler extends ModHandlerBase {

	private static final DartItemHandler instance = new DartItemHandler();

	public final Item wrenchID;
	public final Item meatID;

	private DartItemHandler() {
		super();
		Item idwrench = null;
		Item idmeat = null;

		if (this.hasMod()) {
			try {
				Class item = Class.forName("bluedart.core.Config");

				Field wrench = item.getField("forceWrenchID");
				idwrench = (Item)wrench.get(null);

				Field meat = item.getField("rawLambchopID");
				idmeat = (Item)meat.get(null);
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError("DartCraft Item class not found! Cannot read its items!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NoSuchFieldException e) {
				DragonAPICore.logError("DartCraft item field not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (SecurityException e) {
				DragonAPICore.logError("Cannot read DartCraft items (Security Exception)! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalArgumentException e) {
				DragonAPICore.logError("Illegal argument for reading DartCraft items!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalAccessException e) {
				DragonAPICore.logError("Illegal access exception for reading DartCraft items!");
				e.printStackTrace();
				this.logFailure(e);
			}
		}
		else {
			this.noMod();
		}

		wrenchID = idwrench;
		meatID = idmeat;
	}

	public static DartItemHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return wrenchID != null && meatID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.DARTCRAFT;
	}

	public boolean isWrench(ItemStack held) {
		if (held == null)
			return false;
		if (!this.initializedProperly())
			return false;
		return held.getItem() == wrenchID;
	}

}

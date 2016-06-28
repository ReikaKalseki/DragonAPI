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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.base.ModHandlerBase;

public class RedstoneArsenalHandler extends ModHandlerBase {

	private static final String configTag = "ToolFluxInfusedHarvestLevel";
	private static final String categoryTag = "Items.feature";

	private static final RedstoneArsenalHandler instance = new RedstoneArsenalHandler();

	public final Item pickID;
	public final int pickLevel;
	private final ItemStack fluxDust;
	private final ItemStack fluxIngot;

	private RedstoneArsenalHandler() {
		super();
		Item idpick = null;
		int levelpick = -1;
		ItemStack dust = null;
		ItemStack ingot = null;

		if (this.hasMod()) {
			try {
				Class ars = ModList.ARSENAL.getItemClass();
				Field item = ars.getField("itemPickaxeFlux");
				idpick = ((Item)item.get(null));

				item = ars.getField("dustElectrumFlux");
				dust = ((ItemStack)item.get(null));

				item = ars.getField("ingotElectrumFlux");
				ingot = ((ItemStack)item.get(null));

				Class c = Class.forName("cofh.redstonearsenal.RedstoneArsenal");
				Field config = c.getField("config");
				Object obj = config.get(null);
				Method get = obj.getClass().getMethod("get", String.class, String.class, int.class);
				levelpick = (Integer)get.invoke(obj, categoryTag, configTag, 4);
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (ClassCastException e) {
				DragonAPICore.logError(this.getMod()+" config not being read properly! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NoSuchFieldException e) {
				DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NoSuchMethodException e) {
				DragonAPICore.logError(this.getMod()+" method not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (SecurityException e) {
				DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (InvocationTargetException e) {
				DragonAPICore.logError("Invocation target exception for reading "+this.getMod()+"!");
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

		pickID = idpick;
		pickLevel = levelpick;

		fluxDust = dust;
		fluxIngot = ingot;
	}

	public static RedstoneArsenalHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return pickID != null && pickLevel != -1 && fluxDust != null && fluxIngot != null;
	}

	public ItemStack getFluxIngot() {
		return fluxIngot != null ? fluxIngot.copy() : null;
	}

	public ItemStack getFluxDust() {
		return fluxDust != null ? fluxDust.copy() : null;
	}

	@Override
	public ModList getMod() {
		return ModList.ARSENAL;
	}

}

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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.item.Item;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class RedstoneArsenalHandler extends ModHandlerBase {

	private static final String configTag = "ToolFluxInfusedHarvestLevel";
	private static final String categoryTag = "item.feature";

	private static final RedstoneArsenalHandler instance = new RedstoneArsenalHandler();

	public final int pickID;
	public final int pickLevel;

	private RedstoneArsenalHandler() {
		super();
		int idpick = -1;
		int levelpick = -1;

		if (this.hasMod()) {
			try {
				Class ars = ModList.ARSENAL.getItemClass();
				Field item = ars.getField("itemPickaxe");
				idpick = ((Item)item.get(null)).itemID;

				Field config = ars.getField("config");
				Object obj = config.get(null);
				Method get = obj.getClass().getMethod("get", String.class, String.class, int.class);
				levelpick = (Integer)get.invoke(obj, categoryTag, configTag, 4);
			}
			catch (ClassCastException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" config not being read properly! "+e.getMessage());
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (NoSuchMethodException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" method not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Invocation target exception for reading "+this.getMod()+"!");
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

		pickID = idpick;
		pickLevel = levelpick;
	}

	public static RedstoneArsenalHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return pickID != -1 && pickLevel != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.ARSENAL;
	}

}

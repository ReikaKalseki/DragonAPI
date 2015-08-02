/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;

public class HungerOverhaulHandler extends ModHandlerBase {

	public final int regenHungerValue;

	private static final HungerOverhaulHandler instance = new HungerOverhaulHandler();

	private HungerOverhaulHandler() {
		super();
		int regenFood = -1;
		if (this.hasMod()) {
			try {
				Class c = Class.forName("iguanaman.hungeroverhaul.IguanaConfig");

				Field f = c.getField("minHungerToHeal");
				regenFood = f.getInt(null);
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
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

		regenHungerValue = regenFood;
	}

	public static HungerOverhaulHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return regenHungerValue != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.HUNGEROVERHAUL;
	}

}

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

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class IC2Handler extends ModHandlerBase {

	public final int iridiumID;

	private ItemStack purifiedUranium;

	private static final IC2Handler instance = new IC2Handler();

	private IC2Handler() {
		super();
		int idiridium = -1;
		if (this.hasMod()) {
			try {
				Class ic2 = this.getMod().getItemClass();
				Field irid = ic2.getField("iridiumOre");
				Field crush = ic2.getField("purifiedCrushedUraniumOre");
				ItemStack iridium = (ItemStack)irid.get(null);
				idiridium = iridium.itemID;
				ItemStack pureCrushU = (ItemStack)crush.get(null);
				purifiedUranium = pureCrushU;
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
		return iridiumID != -1 && purifiedUranium != null;
	}

	@Override
	public ModList getMod() {
		return ModList.IC2;
	}

}

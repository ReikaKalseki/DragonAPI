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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Auxiliary.APIRegistry;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class MekanismHandler extends ModHandlerBase {

	private static final MekanismHandler instance = new MekanismHandler();

	public final int paxelID;

	private MekanismHandler() {
		super();
		int idpaxel = -1;

		if (this.hasMod()) {
			try {
				Class item = Class.forName("mods.tinker.tconstruct.common.TContent");
				Field paxel = item.getField("paxel");
				idpaxel = ((Item)paxel.get(null)).itemID;
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Mekanism Item class not found! Cannot read its items!");
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Mekanism item field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read Mekanism items (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal argument for reading Mekanism items!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading Mekanism items!");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}

		paxelID = idpaxel;
	}

	public static MekanismHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return paxelID != -1;
	}

	@Override
	public APIRegistry getMod() {
		return APIRegistry.MEKANISM;
	}

	public boolean isPaxel(ItemStack held) {
		if (!this.initializedProperly())
			return false;
		return held.itemID == paxelID;
	}

}

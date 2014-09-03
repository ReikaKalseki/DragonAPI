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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class AppEngHandler extends ModHandlerBase {

	private static final AppEngHandler instance = new AppEngHandler();

	private ItemStack certus;
	private ItemStack dust;

	private static Method itemGet;
	private static Method itemDefGet;
	private static Object aeCoreObj;

	private AppEngHandler() {
		super();
		if (this.hasMod()) {
			try {
				certus = getItemStack("materialCertusQuartzCrystal");
				dust = getItemStack("materialCertusQuartzDust");
			}
			catch (Exception e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read AE class contents!");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}
	}

	private static ItemStack getItemStack(String field) throws Exception {
		if (aeCoreObj == null || itemGet == null || itemDefGet == null) {
			Class ae = Class.forName("appeng.core.Api");
			Field inst = ae.getField("instance");
			aeCoreObj = inst.get(null);
			itemGet = ae.getMethod("items");

			Class idef = Class.forName("appeng.api.util");
			itemDefGet = idef.getMethod("item");

			Class mat = Class.forName("appeng.api.definitions.Materials");
			Field f = mat.getField(field);
		}
		if (aeCoreObj == null || itemGet == null || itemDefGet == null) {
			return null;
		}

		Object def = itemGet.invoke(aeCoreObj);
		Item item = (Item)itemDefGet.invoke(def);
		return new ItemStack(item);
	}

	public static AppEngHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return certus != null && dust != null;
	}

	@Override
	public ModList getMod() {
		return ModList.APPENG;
	}

	public ItemStack getCertusQuartz() {
		return certus.copy();
	}

	public ItemStack getCertusQuartzDust() {
		return dust.copy();
	}

}

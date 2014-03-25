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

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class AppEngHandler extends ModHandlerBase {

	private static final AppEngHandler instance = new AppEngHandler();

	private ItemStack certus;
	private ItemStack dust;

	private AppEngHandler() {
		super();
		if (this.hasMod()) {
			try {
				Class ae = Class.forName("appeng.api.Materials");
				Field item = ae.getField("matQuartz");
				ItemStack quartz = (ItemStack)item.get(null);
				certus = quartz.copy();

				Field item2 = ae.getField("matQuartzDust");
				ItemStack quartzdust = (ItemStack)item2.get(null);
				dust = quartzdust.copy();
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: AppEng class not found! Cannot read its contents!");
				e.printStackTrace();
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

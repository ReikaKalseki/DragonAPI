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
import Reika.DragonAPI.Auxiliary.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ForestryHandler extends ModHandlerBase {

	public final int apatiteID;

	private static final ForestryHandler instance = new ForestryHandler();

	private ForestryHandler() {
		super();
		int idapatite = -1;
		if (this.hasMod()) {
			try {
				Class forest = Class.forName("forestry.core.config.ForestryItem");
				Field apa = forest.getField("apatite");
				Item item = (Item)apa.get(null);
				idapatite = item.itemID;
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Forestry class not found! Cannot read its contents!");
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Forestry apatiteBlock field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read Forestry class (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal argument for reading Forestry class!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading Forestry class!");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}

		apatiteID = idapatite;
	}

	public static ForestryHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return apatiteID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.FORESTRY;
	}

}

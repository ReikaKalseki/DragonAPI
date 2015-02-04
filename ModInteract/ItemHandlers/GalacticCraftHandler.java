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

import net.minecraft.item.Item;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class GalacticCraftHandler extends ModHandlerBase {

	private static final GalacticCraftHandler instance = new GalacticCraftHandler();

	public final Item basicItemID;

	public static final int siliconMeta = 2;

	private GalacticCraftHandler() {
		super();
		Item idbasicItem = null;
		if (this.hasMod()) {
			try {
				Class items = this.getMod().getItemClass();
				Field f = items.getField("basicItem");
				Item i = (Item)f.get(null);
				idbasicItem = i;
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
		basicItemID = idbasicItem;
	}

	public static GalacticCraftHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return basicItemID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.GALACTICRAFT;
	}

}

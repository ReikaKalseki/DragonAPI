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

import net.minecraft.item.Item;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.base.ModHandlerBase;

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

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
import Reika.DragonAPI.Auxiliary.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ThaumBlockHandler extends ModHandlerBase {

	private static final ThaumBlockHandler instance = new ThaumBlockHandler();

	public final int totemID;

	private ThaumBlockHandler() {
		super();
		int idtile = -1;

		if (this.hasMod()) {
			try {
				Class thaum = ModList.THAUMCRAFT.getBlockClass();
				Field totem = thaum.getField("blockCosmeticSolidId");
				idtile = totem.getInt(null);
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

		totemID = idtile;
	}

	public static ThaumBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return totemID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.THAUMCRAFT;
	}

	public boolean isTotemBlock(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return block.itemID == totemID && block.getItemDamage() < 2;
	}

}

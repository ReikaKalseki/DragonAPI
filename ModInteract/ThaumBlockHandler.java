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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Auxiliary.APIRegistry;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;

public class ThaumBlockHandler extends ModHandlerBase {

	private static final ThaumBlockHandler instance = new ThaumBlockHandler();

	public final int totemID;

	private ThaumBlockHandler() {
		int idtile = -1;

		if (this.hasMod()) {
			try {
				Class thaum = Class.forName("thaumcraft.common.Config");
				Field totem = thaum.getField("blockCosmeticSolid");
				Block block = (Block)totem.get(null);
				idtile = block.blockID;
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Thaumcraft Config class not found! Cannot read its items!");
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Thaumcraft block field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read Thaumcraft blocks (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal argument for reading Thaumcraft blocks!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading Thaumcraft blocks!");
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
	public APIRegistry getMod() {
		return APIRegistry.THAUMCRAFT;
	}

	public boolean isTotemBlock(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return block.itemID == totemID && block.getItemDamage() < 2;
	}

}

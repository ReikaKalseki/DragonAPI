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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class MimicryHandler extends ModHandlerBase {

	private static final MimicryHandler instance = new MimicryHandler();

	public final int oreID;
	public final int itemID;

	private MimicryHandler() {
		super();
		int idstone = -1;
		int iditem = -1;

		if (this.hasMod()) {
			try {
				Class blocks = ModList.MIMICRY.getBlockClass();
				Field ore = blocks.getField("MimichiteOre");
				idstone = ((Block)ore.get(null)).blockID;

				Class items = ModList.MIMICRY.getItemClass();
				Field item = items.getField("Mimichite");
				iditem = ((Item)item.get(null)).itemID;
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

		oreID = idstone;
		itemID = iditem;
	}

	public static MimicryHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return itemID != -1 && oreID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.MIMICRY;
	}

	public boolean isMimichiteOre(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return block.itemID == oreID;
	}

}

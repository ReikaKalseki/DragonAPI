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

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MimicryHandler extends ModHandlerBase {

	private static final MimicryHandler instance = new MimicryHandler();

	public final Block oreID;
	public final Item itemID;

	private MimicryHandler() {
		super();
		Block idstone = null;
		Item iditem = null;

		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();
				Field ore = blocks.getField("MimichiteOre");
				idstone = ((Block)ore.get(null));

				Class items = this.getMod().getItemClass();
				Field item = items.getField("Mimichite");
				iditem = ((Item)item.get(null));
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
		return itemID != null && oreID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.MIMICRY;
	}

	public boolean isMimichiteOre(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return ReikaItemHelper.matchStackWithBlock(block, oreID);
	}

}

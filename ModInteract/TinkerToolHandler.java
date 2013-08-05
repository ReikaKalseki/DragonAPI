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
import net.minecraft.nbt.NBTTagCompound;
import Reika.DragonAPI.Auxiliary.APIRegistry;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;

public final class TinkerToolHandler extends ModHandlerBase {

	private static final TinkerToolHandler instance = new TinkerToolHandler();

	public final int pickID;

	private TinkerToolHandler() {
		int idpick = -1;

		if (this.hasMod()) {
			try {
				Class item = Class.forName("mods.tinker.tconstruct.common.TContent");
				Field pick = item.getField("pickaxe");
				idpick = ((Item)pick.get(null)).itemID;
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: DartCraft Item class not found! Cannot read its items!");
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: DartCraft item field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read DartCraft items (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal argument for reading DartCraft items!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading DartCraft items!");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}

		pickID = idpick;
	}

	public static TinkerToolHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return pickID != -1;
	}

	@Override
	public APIRegistry getMod() {
		return APIRegistry.TINKERER;
	}

	public boolean isPick(ItemStack held) {
		if (!this.initializedProperly())
			return false;
		return held.itemID == pickID;
	}

	public int getHarvestLevel(ItemStack is) {
		if (is.stackTagCompound == null)
			return 0;
		NBTTagCompound tag = is.stackTagCompound.getCompoundTag("InfiTool");
		return tag.getInteger("HarvestLevel");
	}

	public boolean isStoneOrBetterPick(ItemStack is) {
		return this.isPick(is) && this.getHarvestLevel(is) >= 1;
	}

	public boolean isIronOrBetterPick(ItemStack is) {
		return this.isPick(is) && this.getHarvestLevel(is) >= 2;
	}

	public boolean isDiamondOrBetterPick(ItemStack is) {
		return this.isPick(is) && this.getHarvestLevel(is) >= 3;
	}

}

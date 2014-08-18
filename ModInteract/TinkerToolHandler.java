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

import java.lang.reflect.Field;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class TinkerToolHandler extends ModHandlerBase {

	private static final TinkerToolHandler instance = new TinkerToolHandler();

	public final Item pickID;
	public final Item hammerID;

	private TinkerToolHandler() {
		super();
		Item idpick = null;
		Item idhammer = null;

		if (this.hasMod()) {
			try {
				Class item = this.getMod().getItemClass();
				Field pick = item.getField("pickaxe");
				Field hammer = item.getField("hammer");
				idpick = ((Item)pick.get(null));
				idhammer = ((Item)hammer.get(null));
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

		pickID = idpick;
		hammerID = idhammer;
	}

	public static TinkerToolHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return pickID != null && hammerID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.TINKERER;
	}

	public boolean isItemInfiTool(ItemStack is) {
		return is.getUnlocalizedName().startsWith("Items.InfiTool");
	}

	public boolean isPick(ItemStack is) {
		if (this.isItemInfiTool(is)) {
			String stackName = is.getUnlocalizedName();
			if (stackName.equals(pickID.getUnlocalizedName()))
				return true;
		}
		return false;
	}

	public boolean isHammer(ItemStack is) {
		if (this.isItemInfiTool(is)) {
			String stackName = is.getUnlocalizedName();
			if (stackName.equals(hammerID.getUnlocalizedName()))
				return true;
		}
		return false;
	}

	public int getHarvestLevel(ItemStack is) {
		if (is.stackTagCompound == null)
			return 0;
		NBTTagCompound tag = is.stackTagCompound.getCompoundTag("InfiTool");
		return tag.getInteger("HarvestLevel");
	}

	public boolean isStoneOrBetter(ItemStack is) {
		return this.getHarvestLevel(is) >= 1;
	}

	public boolean isIronOrBetter(ItemStack is) {
		return this.getHarvestLevel(is) >= 2;
	}

	public boolean isDiamondOrBetter(ItemStack is) {
		return this.getHarvestLevel(is) >= 3;
	}

}

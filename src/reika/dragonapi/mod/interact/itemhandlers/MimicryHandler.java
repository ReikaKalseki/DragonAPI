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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.base.ModHandlerBase;
import reika.dragonapi.libraries.registry.ReikaItemHelper;

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

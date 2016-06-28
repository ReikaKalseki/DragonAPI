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
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.base.ModHandlerBase;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.dragonapi.mod.registry.ModOreList;

public class TransitionalOreHandler extends ModHandlerBase {

	private static final TransitionalOreHandler instance = new TransitionalOreHandler();

	public final Block magmaID;
	public final Block cobaltID;

	private TransitionalOreHandler() {
		super();
		Block idore = null;
		Block idcobalt = null;

		if (this.hasMod()) {
			try {
				Class trans = ModList.TRANSITIONAL.getBlockClass();
				Field magma = trans.getField("MagmaniteOreID");
				idore = (Block)magma.get(null);
				Field cobalt = trans.getField("CobaltOreID");
				idcobalt = (Block)cobalt.get(null);
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

		magmaID = idore;
		cobaltID = idcobalt;
		OreDictionary.registerOre("oreCobalt", new ItemStack(cobaltID, 1, 0));
		ModOreList.COBALT.initialize();
	}

	public static TransitionalOreHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return magmaID != null && cobaltID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.TRANSITIONAL;
	}

	public boolean isMagmaniteOre(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return ReikaItemHelper.matchStackWithBlock(block, magmaID);
	}

}

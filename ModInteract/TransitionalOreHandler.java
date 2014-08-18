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
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

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

		magmaID = idore;
		cobaltID = idcobalt;
		ReikaOreHelper.addOreForReference(new ItemStack(cobaltID, 1, 0));
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

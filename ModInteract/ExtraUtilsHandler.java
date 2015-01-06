/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ExtraUtilsHandler extends ModHandlerBase {

	public final int darkID;
	public final Block decoID;

	public final int edgedBrick = 0;
	public final int enderObsidian = 1;
	public final int burntQuartz = 2;
	public final int frostedStone = 3;
	public final int borderStone = 4;
	public final int unstableBlock = 5;
	public final int gravelBricks = 6;
	public final int borderStoneAlt = 7;
	public final int magicWood = 8;
	public final int sandyGlass = 9;

	private static final ExtraUtilsHandler instance = new ExtraUtilsHandler();

	private ExtraUtilsHandler() {
		super();
		int iddark = -1;
		Block iddeco = null;
		if (this.hasMod()) {
			try {
				Class c = this.getMod().getBlockClass();
				Field dim = c.getField("dimID");
				iddark = dim.getInt(null);

				Field deco = c.getField("decorative1");
				iddeco = ((Block)deco.get(null));
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

		darkID = iddark;
		decoID = iddeco;
	}

	public static ExtraUtilsHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return darkID != -1 && decoID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.EXTRAUTILS;
	}

}

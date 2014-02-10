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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ForestryHandler extends ModHandlerBase {

	public final int apatiteID;
	public final int saplingID;

	private static final ForestryHandler instance = new ForestryHandler();

	private ForestryHandler() {
		super();
		int idapatite = -1;
		int idsapling = -1;
		if (this.hasMod()) {
			try {
				Class forest = this.getMod().getItemClass();
				Field apa = forest.getField("apatite"); //is enum object now
				Object entry = apa.get(null);
				Method get = forest.getMethod("item");
				Item item = (Item)get.invoke(entry);
				idapatite = item.itemID;

				Class blocks = this.getMod().getBlockClass();
				Field sapling = blocks.getField("saplingGE");
				Block s = (Block)sapling.get(null);
				idsapling = s.blockID;
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (NoSuchMethodException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" method not found! "+e.getMessage());
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
			catch (InvocationTargetException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Invocation target exception for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}

		apatiteID = idapatite;
		saplingID = idsapling;
	}

	public static ForestryHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return apatiteID != -1 && saplingID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.FORESTRY;
	}

}

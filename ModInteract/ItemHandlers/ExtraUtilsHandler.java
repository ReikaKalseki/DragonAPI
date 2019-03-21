/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;

public class ExtraUtilsHandler extends ModHandlerBase {

	public final int darkID;

	public final Block decoID;
	public final Block deco2ID;
	public final Block etherealBlockID;

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

	public final int ethereal = 0;
	public final int ineffable = 1;
	public final int darkethereal = 2;
	public final int invethereal = 3;
	public final int invineffable = 4;
	public final int darkinvethereal = 5;

	public final Item soulID;

	private static final ExtraUtilsHandler instance = new ExtraUtilsHandler();

	private ExtraUtilsHandler() {
		super();
		int iddark = -100;
		Block iddeco = null;
		Block iddeco2 = null;
		Block idether = null;
		Item idsoul = null;
		if (this.hasMod()) {
			try {
				Class c = this.getMod().getBlockClass();
				iddark = this.getDimID(c);

				Field f = c.getField("decorative1");
				iddeco = ((Block)f.get(null));

				f = c.getField("decorative2");
				iddeco2 = ((Block)f.get(null));

				f = c.getField("etheralBlock"); //NOT A TYPO
				idether = ((Block)f.get(null));

				c = this.getMod().getItemClass();
				f = c.getField("soul");
				idsoul = ((Item)f.get(null));
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

		darkID = iddark;
		decoID = iddeco;
		deco2ID = iddeco2;
		etherealBlockID = idether;
		soulID = idsoul;
	}

	private int getDimID(Class c) throws NoSuchFieldException, IllegalAccessException {
		try {
			Field dim = c.getField("dimID");
			return dim.getInt(null);
		}
		catch (Exception e) {
			Field dim = c.getField("underdarkDimID");
			return dim.getInt(null);
		}

	}

	public static ExtraUtilsHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return decoID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.EXTRAUTILS;
	}

}

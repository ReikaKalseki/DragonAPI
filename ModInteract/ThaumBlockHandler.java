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
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ThaumBlockHandler extends ModHandlerBase {

	private static final ThaumBlockHandler instance = new ThaumBlockHandler();

	public final int totemID;
	public final int plantID;
	public final int crystalID;

	public final int shimmerMeta = 2;
	public final int cinderMeta = 3;
	public final int etherealMeta = 4;

	public final int taintBiomeID;
	public final int eerieBiomeID;

	private ThaumBlockHandler() {
		super();
		int idtile = -1;
		int idplant = -1;
		int idcrystal = -1;
		int idtaint = -1;
		int ideerie = -1;

		if (this.hasMod()) {
			Class thaum = ModList.THAUMCRAFT.getBlockClass();

			idtile = this.loadBlockID(thaum, "blockCosmeticSolid");
			idplant = this.loadBlockID(thaum, "blockCustomPlant");
			idcrystal = this.loadBlockID(thaum, "blockCrystal");

			try {
				Class config = Class.forName("thaumcraft.common.config.Config");
				Field biome = config.getField("biomeTaintID");
				idtaint = biome.getInt(null);

				biome = config.getField("biomeEerieID");
				ideerie = biome.getInt(null);
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load ThaumCraft config class!");
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load field from ThaumCraft config class!");
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Could not read field from ThaumCraft config class!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Could not read field from ThaumCraft config class!");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}

		totemID = idtile;
		plantID = idplant;
		crystalID = idcrystal;
		taintBiomeID = idtaint;
		eerieBiomeID = ideerie;
	}

	public static ThaumBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return totemID != -1 && plantID != -1 && crystalID != -1 && taintBiomeID != -1 && eerieBiomeID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.THAUMCRAFT;
	}

	public boolean isTotemBlock(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return block.itemID == totemID && block.getItemDamage() < 2;
	}

	/** Tries both instance and ID storage */
	private int loadBlockID(Class c, String fieldName) {
		int id = -1;
		Exception e1 = null;
		Exception e2 = null;
		try {
			Field block = c.getField(fieldName);
			id = ((Block)block.get(null)).blockID;
		}
		catch (Exception e) {
			e1 = e;
		}
		if (id != -1) {
			try {
				Field number = c.getField(fieldName+"Id");
				id = number.getInt(null);
			}
			catch (Exception e) {
				e2 = e;
			}
		}
		if (id == -1) {
			e1.printStackTrace();
			e2.printStackTrace();
		}
		return id;
	}

	public boolean isCrystalCluster(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return block.itemID == crystalID;
	}

}

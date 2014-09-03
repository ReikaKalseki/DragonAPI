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

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ThaumBlockHandler extends ModHandlerBase {

	private static final ThaumBlockHandler instance = new ThaumBlockHandler();

	public final Block totemID;
	public final Block plantID;
	public final Block crystalID;

	public final int shimmerMeta = 2;
	public final int cinderMeta = 3;
	public final int etherealMeta = 4;

	public final int taintBiomeID;
	public final int eerieBiomeID;
	public final int magicBiomeID;

	private ThaumBlockHandler() {
		super();
		Block idtile = null;
		Block idplant = null;
		Block idcrystal = null;
		int idtaint = -1;
		int ideerie = -1;
		int idmagic = -1;

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

				biome = config.getField("biomeMagicalForestID");
				idmagic = biome.getInt(null);
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
		magicBiomeID = idmagic;
	}

	public static ThaumBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return totemID != null && plantID != null && crystalID != null && taintBiomeID != -1 && eerieBiomeID != -1 && magicBiomeID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.THAUMCRAFT;
	}

	public boolean isTotemBlock(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return ReikaItemHelper.matchStackWithBlock(block, totemID) && block.getItemDamage() < 2;
	}

	/** Tries both instance and ID storage */
	private Block loadBlockID(Class c, String fieldName) {
		Block id = null;
		Exception e1 = null;
		Exception e2 = null;
		try {
			Field block = c.getField(fieldName);
			id = ((Block)block.get(null));
		}
		catch (Exception e) {
			e1 = e;
		}
		return id;
	}

	public boolean isCrystalCluster(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return ReikaItemHelper.matchStackWithBlock(block, crystalID);
	}

}

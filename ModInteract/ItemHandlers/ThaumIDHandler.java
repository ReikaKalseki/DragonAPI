/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;
import java.util.HashSet;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;

public class ThaumIDHandler extends ModHandlerBase {

	private static final HashSet<Integer> biomeSet = new HashSet();
	private static final HashSet<Integer> potionSet = new HashSet();

	private static final ThaumIDHandler instance = new ThaumIDHandler();

	public final int dimensionID;

	private ThaumIDHandler() {
		super();

		int dim = 0;

		if (this.hasMod()) {
			Class thaum = ModList.THAUMCRAFT.getBlockClass();

			try {
				Class config = Class.forName("thaumcraft.common.config.Config");

				try {
					Field dimid = config.getField("dimensionOuterId");
					dim = dimid.getInt(null);
				}
				catch (NoSuchFieldException e) {
					DragonAPICore.logError("Could not load field from ThaumCraft config class!");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (IllegalArgumentException e) {
					DragonAPICore.logError("Could not read field from ThaumCraft config class!");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (IllegalAccessException e) {
					DragonAPICore.logError("Could not read field from ThaumCraft config class!");
					e.printStackTrace();
					this.logFailure(e);
				}

				for (int i = 0; i < Biomes.list.length; i++) {
					Biomes p = Biomes.list[i];
					try {
						Field f = config.getField(p.tag);
						p.ID = f.getInt(null);
						biomeSet.add(p.ID);
					}
					catch (NoSuchFieldException e) {
						DragonAPICore.logError("Could not load field from ThaumCraft config class!");
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (IllegalArgumentException e) {
						DragonAPICore.logError("Could not read field from ThaumCraft config class!");
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (IllegalAccessException e) {
						DragonAPICore.logError("Could not read field from ThaumCraft config class!");
						e.printStackTrace();
						this.logFailure(e);
					}
				}

				for (int i = 0; i < Potions.list.length; i++) {
					Potions p = Potions.list[i];
					try {
						Field f = config.getField(p.tag);
						p.ID = f.getInt(null);
						potionSet.add(p.ID);
					}
					catch (NoSuchFieldException e) {
						DragonAPICore.logError("Could not load field from ThaumCraft config class!");
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (IllegalArgumentException e) {
						DragonAPICore.logError("Could not read field from ThaumCraft config class!");
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (IllegalAccessException e) {
						DragonAPICore.logError("Could not read field from ThaumCraft config class!");
						e.printStackTrace();
						this.logFailure(e);
					}
				}
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError("Could not load ThaumCraft config class!");
				e.printStackTrace();
				this.logFailure(e);
			}
		}
		else {
			this.noMod();
		}
		dimensionID = dim;
	}

	public static enum Potions {
		FLUXTAINT("TaintPoison"),
		FLUXFLU("VisExhaust"),
		WARPWARD("WarpWard"),
		BLURREDVISION("Blurred"),
		DEATHGAZE("DeathGaze"),
		SUNSCORNED("SunScorned"),
		THAUMARHIA("Thaumarhia"),
		VISPHAGE("InfVisExhaust"),
		//SOULSHATTER("SoulShatter"), in localization only
		HUNGER("UnHunger");

		private final String tag;

		private int ID = -1;

		public static final Potions[] list = values();

		private Potions(String s) {
			tag = "potion"+s+"ID";
		}

		public int getID() {
			return ID;
		}

		public boolean isWarpRelated() {
			switch(this) {
				case FLUXTAINT:
				case WARPWARD:
					return false;
				default:
					return true;
			}
		}
	}

	public static enum Biomes {
		EERIE("Eerie"),
		TAINT("Taint"),
		MAGICFOREST("MagicalForest"),
		ELDRITCH("Eldritch"); //Dimension

		private final String tag;

		private int ID = -1;

		private static final Biomes[] list = values();

		private Biomes(String s) {
			tag = "biome"+s+"ID";
		}

		public int getID() {
			return ID;
		}
	}

	public static ThaumIDHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return biomeSet.size() == Biomes.list.length && potionSet.size() == Potions.list.length && dimensionID != 0;
	}

	@Override
	public ModList getMod() {
		return ModList.THAUMCRAFT;
	}

}

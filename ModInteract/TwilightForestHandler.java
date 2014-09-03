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

public class TwilightForestHandler extends ModHandlerBase {

	public final Block rootID;
	public final Block towerMachineID;
	public final Block towerWoodID;
	public final Block mazeStoneID;
	public final Block treeCoreID;
	public final Block shieldID;
	public final Block portalID;

	public final int breakerMeta;

	public final int dimensionID;

	private static final TwilightForestHandler instance = new TwilightForestHandler();

	private TwilightForestHandler() {
		super();
		Block idroot = null;
		Block idmachine = null;
		Block idtowerwood = null;
		int metabreaker = -1;
		Block idmaze = null;
		Block idcore = null;
		Block idshield = null;
		int dim = 7;
		Block idportal = null;

		if (this.hasMod()) {
			try {
				Class twilight = this.getMod().getBlockClass();
				Class devices = Class.forName("twilightforest.block.BlockTFTowerDevice");
				Class mod = Class.forName("twilightforest.TwilightForestMod");
				Field root = twilight.getField("root");
				Field machine = twilight.getField("towerDevice");
				Field towerwood = twilight.getField("towerWood");
				Field core = twilight.getField("magicLogSpecial");
				Field maze = twilight.getField("mazestone");
				Field shield = twilight.getField("shield");
				Field breaker = devices.getField("META_ANTIBUILDER");
				Field dimension = mod.getField("dimensionID");
				Field portal = twilight.getField("portal");
				idroot = ((Block)root.get(null));
				idmachine = ((Block)machine.get(null));
				idtowerwood = ((Block)towerwood.get(null));
				idmaze = ((Block)maze.get(null));
				idcore = ((Block)core.get(null));
				idshield = ((Block)shield.get(null));
				idportal = ((Block)portal.get(null));
				metabreaker = breaker.getInt(null);
				dim = dimension.getInt(null);
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Twilight Forest class not found! Cannot read its contents!");
				e.printStackTrace();
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

		rootID = idroot;
		towerMachineID = idmachine;
		towerWoodID = idtowerwood;
		breakerMeta = metabreaker;
		mazeStoneID = idmaze;
		treeCoreID = idcore;
		shieldID = idshield;
		dimensionID = dim;
		portalID = idportal;
	}

	public static TwilightForestHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return rootID != null && towerMachineID != null && towerWoodID != null && breakerMeta != -1 && mazeStoneID != null && treeCoreID != null && shieldID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.TWILIGHT;
	}

	public ItemStack getRoot() {
		if (!this.initializedProperly())
			return null;
		return new ItemStack(rootID, 1, 0);
	}

	public ItemStack getTowerMachine() {
		if (!this.initializedProperly())
			return null;
		return new ItemStack(towerMachineID, 1, 0);
	}

	public boolean isTowerWood(Block b) {
		if (!this.initializedProperly())
			return false;
		return b == towerWoodID;
	}

	public boolean isMazeStone(Block b) {
		if (!this.initializedProperly())
			return false;
		return b == mazeStoneID;
	}

}

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

public class TwilightForestHandler extends ModHandlerBase {

	public final int rootID;
	public final int towerMachineID;
	public final int towerWoodID;
	public final int mazeStoneID;
	public final int treeCoreID;
	public final int shieldID;

	public final int breakerMeta;

	public final int dimensionID;

	private static final TwilightForestHandler instance = new TwilightForestHandler();

	private TwilightForestHandler() {
		super();
		int idroot = -1;
		int idmachine = -1;
		int idtowerwood = -1;
		int metabreaker = -1;
		int idmaze = -1;
		int idcore = -1;
		int idshield = -1;
		int dim = 7;

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
				idroot = ((Block)root.get(null)).blockID;
				idmachine = ((Block)machine.get(null)).blockID;
				idtowerwood = ((Block)towerwood.get(null)).blockID;
				idmaze = ((Block)maze.get(null)).blockID;
				idcore = ((Block)core.get(null)).blockID;
				idshield = ((Block)shield.get(null)).blockID;
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
	}

	public static TwilightForestHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return rootID != -1 && towerMachineID != -1 && towerWoodID != -1 && breakerMeta != -1 && mazeStoneID != -1 && treeCoreID != -1 && shieldID != -1;
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
		return b.blockID == towerWoodID;
	}

	public boolean isMazeStone(Block b) {
		if (!this.initializedProperly())
			return false;
		return b.blockID == mazeStoneID;
	}

}

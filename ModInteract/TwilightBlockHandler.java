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
import Reika.DragonAPI.Auxiliary.APIRegistry;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class TwilightBlockHandler extends ModHandlerBase {

	public final int rootID;
	public final int towerMachineID;
	public final int towerWoodID;
	public final int mazeStoneID;

	public final int breakerMeta;

	private static final TwilightBlockHandler instance = new TwilightBlockHandler();

	private TwilightBlockHandler() {
		super();
		int idroot = -1;
		int idmachine = -1;
		int idtowerwood = -1;
		int metabreaker = -1;
		int idmaze = -1;

		if (this.hasMod()) {
			try {
				Class twilight = Class.forName("twilightforest.block.TFBlocks");
				Class devices = Class.forName("twilightforest.block.BlockTFTowerDevice");
				Field root = twilight.getField("root");
				Field machine = twilight.getField("towerDevice");
				Field towerwood = twilight.getField("towerWood");
				Field maze = twilight.getField("mazestone");
				Field breaker = devices.getField("META_ANTIBUILDER");
				idroot = ((Block)root.get(null)).blockID;
				idmachine = ((Block)machine.get(null)).blockID;
				idtowerwood = ((Block)towerwood.get(null)).blockID;
				idmaze = ((Block)maze.get(null)).blockID;
				metabreaker = breaker.getInt(null);
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.spamConsole("DRAGONAPI: Twilight Forest class not found! Cannot read its contents!");
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.spamConsole("DRAGONAPI: Twilight Forest block field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.spamConsole("DRAGONAPI: Cannot read Twilight Forest class (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.spamConsole("DRAGONAPI: Illegal argument for reading Twilight Forest class!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.spamConsole("DRAGONAPI: Illegal access exception for reading Twilight Forest class!");
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

	}

	public static TwilightBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return rootID != -1 && towerMachineID != -1 && towerWoodID != -1 && breakerMeta != -1 && mazeStoneID != -1;
	}

	@Override
	public APIRegistry getMod() {
		return APIRegistry.TWILIGHT;
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

}

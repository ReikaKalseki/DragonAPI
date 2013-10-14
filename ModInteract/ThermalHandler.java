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
import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Auxiliary.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ThermalHandler extends ModHandlerBase {

	private static final ThermalHandler instance = new ThermalHandler();

	public static enum Types {
		LIQUID(),
		POWER();
	}

	public final int liquiductID;

	private ThermalHandler() {
		super();
		int idpipe = -1;
		if (this.hasMod()) {
			try {
				Class blocks = Class.forName("thermalexpansion.block.TEBlocks");
				Field pipe = blocks.getField("blockConduit");
				idpipe = ((Block)pipe.get(null)).blockID;
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Thermal Expansion class not found! Cannot read its contents!");
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Thermal Expansion tankBlock field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read Thermal Expansion class (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal argument for reading Thermal Expansion class!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading Thermal Expansion class!");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}

		liquiductID = idpipe;
	}

	public static ThermalHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return liquiductID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.THERMALEXPANSION;
	}

	public Types getConduitType(TileEntity te) {
		return Types.values()[te.getBlockMetadata()];
	}

}

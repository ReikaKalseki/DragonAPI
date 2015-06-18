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

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ThermalHandler extends ModHandlerBase {

	private static final ThermalHandler instance = new ThermalHandler();

	public static enum Types {
		LIQUID(),
		POWER();
	}

	public final Block ductID;
	public final Block enderID;

	private ThermalHandler() {
		super();
		Block idpipe = null;
		Block idender = null;
		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();
				//Field pipe = blocks.getField("blockConduit");
				//idpipe = ((Block)pipe.get(null));
				Field ender = blocks.getField("blockFluidEnder");
				idender = ((Block)ender.get(null));
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal argument for reading "+this.getMod()+"!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NullPointerException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
				this.logFailure(e);
			}
		}
		else {
			this.noMod();
		}

		ductID = idpipe;
		enderID = idender;
	}

	public static ThermalHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return ductID != null && enderID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.THERMALFOUNDATION;
	}

	public Types getConduitType(TileEntity te) {
		return Types.values()[te.getBlockMetadata()];
	}

}

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

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;

public class MFRHandler extends ModHandlerBase {

	private static final MFRHandler instance = new MFRHandler();

	public final Block cableID;

	private MFRHandler() {
		super();
		Block idcable = null;

		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();
				Field wire = blocks.getField("rednetCableBlock");
				idcable = ((Block)wire.get(null));
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

		cableID = idcable;
	}

	public static MFRHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return cableID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.MINEFACTORY;
	}

}

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

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import net.minecraft.block.Block;

public class NaturaBlockHandler extends ModHandlerBase {

	private static final NaturaBlockHandler instance = new NaturaBlockHandler();

	public final Block cloudID;

	private NaturaBlockHandler() {
		super();
		Block idcloud = null;
		if (this.hasMod()) {
			Class blocks = this.getMod().getBlockClass();
			Class items = this.getMod().getItemClass();
			try {
				Field f = blocks.getField("cloud");
				idcloud = (Block)f.get(null);
			}
			catch (NoSuchFieldException e) {
				DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
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
		cloudID = idcloud;
	}

	public static NaturaBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return cloudID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.NATURA;
	}

}

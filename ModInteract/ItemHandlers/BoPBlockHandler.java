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
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;

public class BoPBlockHandler extends ModHandlerBase {

	private static final BoPBlockHandler instance = new BoPBlockHandler();

	public final Block coral1;
	public final Block coral2;

	private BoPBlockHandler() {
		super();
		Block idcoral1 = null;
		Block idcoral2 = null;

		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();
				Field c1 = blocks.getField("coral1");
				idcoral1 = ((Block)c1.get(null));

				Field c2 = blocks.getField("coral2");
				idcoral2 = ((Block)c2.get(null));
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

		coral1 = idcoral1;
		coral2 = idcoral2;
	}

	public static BoPBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return coral1 != null && coral2 != null;
	}

	@Override
	public ModList getMod() {
		return ModList.BOP;
	}

	public boolean isCoral(Block id) {
		return id == coral1 || id == coral2;
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.itemhandlers;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.base.ModHandlerBase;

public class BCMachineHandler extends ModHandlerBase {

	public final Block tankID;

	private static final BCMachineHandler instance = new BCMachineHandler();

	private BCMachineHandler() {
		super();
		Block idtank = null;
		if (this.hasMod()) {
			try {
				Class factory = this.getMod().getBlockClass();
				Field tank = factory.getField("tankBlock");
				idtank = ((Block)tank.get(null));
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

		tankID = idtank;
	}

	public static BCMachineHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return tankID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.BCFACTORY;
	}

	public ItemStack getTank() {
		if (!this.initializedProperly())
			return null;
		return new ItemStack(tankID, 1, 0);
	}

}

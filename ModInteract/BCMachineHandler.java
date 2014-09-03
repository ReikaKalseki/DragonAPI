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

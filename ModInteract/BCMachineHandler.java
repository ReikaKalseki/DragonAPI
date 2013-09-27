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
import Reika.DragonAPI.Auxiliary.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class BCMachineHandler extends ModHandlerBase {

	public final int tankID;

	private static final BCMachineHandler instance = new BCMachineHandler();

	private BCMachineHandler() {
		super();
		int idtank = -1;
		if (this.hasMod()) {
			try {
				Class factory = Class.forName("buildcraft.BuildCraftFactory");
				Field tank = factory.getField("tankBlock");
				idtank = ((Block)tank.get(null)).blockID;
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.spamConsole("DRAGONAPI: BuildCraft Factory class not found! Cannot read its contents!");
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.spamConsole("DRAGONAPI: BuildCraft Factory tankBlock field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.spamConsole("DRAGONAPI: Cannot read BuildCraft Factory class (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.spamConsole("DRAGONAPI: Illegal argument for reading BuildCraft Factory class!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.spamConsole("DRAGONAPI: Illegal access exception for reading BuildCraft Factory class!");
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
		return tankID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.BUILDCRAFTFACTORY;
	}

	public ItemStack getTank() {
		if (!this.initializedProperly())
			return null;
		return new ItemStack(tankID, 1, 0);
	}

}

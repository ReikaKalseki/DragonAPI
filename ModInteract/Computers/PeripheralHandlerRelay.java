/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Computers;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;

import dan200.computercraft.api.ComputerCraftAPI;
import li.cil.oc.api.Driver;


public class PeripheralHandlerRelay {

	@ModDependent(ModList.COMPUTERCRAFT)
	public static void registerCCHandler() {
		ComputerCraftAPI.registerPeripheralProvider(new PeripheralHandlerCC());
	}

	@ModDependent(ModList.OPENCOMPUTERS)
	public static void registerOCHandler() {
		Driver.add(new PeripheralHandlerOC());
	}

}

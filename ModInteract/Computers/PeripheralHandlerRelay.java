package Reika.DragonAPI.ModInteract.Computers;

import li.cil.oc.api.Driver;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import dan200.computercraft.api.ComputerCraftAPI;


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

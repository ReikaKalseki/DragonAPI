package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Method;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Base.TileEntityBase;

import forestry.api.multiblock.IMultiblockController;

public class ForestryMultiblockControllerHandling {

	private static Class controllerBase;
	private static Method tickMethod;

	static {
		if (ModList.FORESTRY.isLoaded()) {
			try {
				controllerBase = Class.forName("forestry.core.multiblock.MultiblockControllerBase");
				tickMethod = controllerBase.getDeclaredMethod("updateMultiblockEntity");
				tickMethod.setAccessible(true);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not fetch multiblock controller internal methods!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
			}
		}

	}

	public static boolean isMultiblockController(IMultiblockController imc) {
		return controllerBase.isAssignableFrom(imc.getClass());
	}

	public static void tickMultiblock(IMultiblockController imc, Object caller) {
		try {
			tickMethod.invoke(imc);
		}
		catch (Exception e) {
			e.printStackTrace();
			if (caller instanceof TileEntityBase)
				((TileEntityBase)caller).writeError(e);
		}
	}
}

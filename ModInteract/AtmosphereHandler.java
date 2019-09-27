package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.OxygenHooks;
import zmaster587.advancedRocketry.api.IAtmosphere;

public class AtmosphereHandler {

	private static Class handler;
	private static Method lookup;
	private static Method get;

	@ModDependent(ModList.ADVROCKET)
	public static IAtmosphere getAtmo(World world, int x, int y, int z) {
		if (!ModList.ADVROCKET.isLoaded())
			return null;
		try {
			Object o = lookup.invoke(null, world.provider.dimensionId);
			return (IAtmosphere)get.invoke(o, x, y, z);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isNoAtmo(World world, int x, int y, int z, Block b, boolean needsO2) {
		if (ModList.ADVROCKET.isLoaded()) {
			IAtmosphere atmo = getAtmo(world, x, y, z);
			if (atmo != null && !atmo.allowsCombustion())
				return true;
		}
		if (ModList.GALACTICRAFT.isLoaded() && InterfaceCache.IGALACTICWORLD.instanceOf(world.provider)) {
			if (needsO2) {
				if (OxygenHooks.noAtmosphericCombustion(world.provider)) {
					if (!(OxygenHooks.inOxygenBubble(world, x+0.5, y+0.5, z+0.5) && OxygenHooks.checkTorchHasOxygen(world, b, x, y, z)))
						return true;
				}
			}
			else {
				IGalacticraftWorldProvider igw = (IGalacticraftWorldProvider)world.provider;
				if (igw.getSoundVolReductionAmount() > 1)
					return true;
			}
		}
		return false;
	}

	static {
		if (ModList.ADVROCKET.isLoaded()) {
			try {
				handler = Class.forName("zmaster587.advancedRocketry.atmosphere.AtmosphereHandler");
				lookup = handler.getMethod("getOxygenHandler", int.class);
				get = handler.getDeclaredMethod("getAtmosphereType", int.class, int.class, int.class);
			}
			catch (Exception e) {
				e.printStackTrace();
				DragonAPICore.logError("Could not initialize AdvR atmo handling!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.ADVROCKET, e);
			}
		}
	}

}

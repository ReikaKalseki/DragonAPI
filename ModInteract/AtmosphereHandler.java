package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.ModInteract.DeepInteract.PlanetDimensionHandler;

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.OxygenHooks;
import zmaster587.advancedRocketry.api.IAtmosphere;

public class AtmosphereHandler {

	private static Class handler;
	private static Method lookup;
	private static Method get;

	private static DamageSource vacuum;
	private static DamageSource o2Toxicity;

	@ModDependent(ModList.ADVROCKET)
	private static IAtmosphere getAtmo(World world, int x, int y, int z) {
		try {
			Object o = lookup.invoke(null, world.provider.dimensionId);
			if (o == null) {
				DragonAPICore.logError("World #"+world.provider.dimensionId+" / "+world+" / "+world.provider+" had a null atmo handler!");
				return null;
			}
			return (IAtmosphere)get.invoke(o, x, y, z);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/** Note: Advanced Rocketry does not seal the actual blocks, only the areas bounded by them,
	 * so give not tiles' location but at the input or output location */
	public static boolean isNoAtmo(World world, int x, int y, int z, Block b, boolean needsO2) {
		if (PlanetDimensionHandler.isAdvRWorld(world)) {
			IAtmosphere atmo = getAtmo(world, x, y, z);
			if (atmo != null && !atmo.allowsCombustion())
				return true;
		}
		else if (PlanetDimensionHandler.isGalacticWorld(world)) {
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

	public static float getAtmoDensity(World world) {
		if (PlanetDimensionHandler.isAdvRWorld(world)) {

		}
		else if (PlanetDimensionHandler.isGalacticWorld(world)) {
			IGalacticraftWorldProvider igw = (IGalacticraftWorldProvider)world.provider;
			return 1F/igw.getSoundVolReductionAmount();
		}
		return 1;
	}

	public static boolean isAtmoBreathabilityDamage(DamageSource src) {
		return src == vacuum || src == o2Toxicity;
	}

	static {
		if (ModList.ADVROCKET.isLoaded()) {
			try {
				handler = Class.forName("zmaster587.advancedRocketry.atmosphere.AtmosphereHandler");
				lookup = handler.getMethod("getOxygenHandler", int.class);
				get = handler.getDeclaredMethod("getAtmosphereType", int.class, int.class, int.class);

				Field f = handler.getDeclaredField("vacuumDamage");
				f.setAccessible(true);
				vacuum = (DamageSource)f.get(null);
				f = handler.getDeclaredField("oxygenToxicityDamage");
				f.setAccessible(true);
				o2Toxicity = (DamageSource)f.get(null);
			}
			catch (Exception e) {
				e.printStackTrace();
				DragonAPICore.logError("Could not initialize AdvR atmo handling!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.ADVROCKET, e);
			}
		}
	}

}

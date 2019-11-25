package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.world.World;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;

public class PlanetDimensionHandler {

	private static HashMap<Integer, Object> advRDimensionData;

	public static boolean isOtherWorld(World world) {
		return isGalacticWorld(world) || isAdvRWorld(world);
	}

	//@ModDependent(ModList.GALACTICRAFT)
	public static boolean isGalacticWorld(World world) {
		return ModList.GALACTICRAFT.isLoaded() && InterfaceCache.IGALACTICWORLD.instanceOf(world.provider);
	}

	//@ModDependent(ModList.ADVROCKET)
	public static boolean isAdvRWorld(World world) {
		return ModList.ADVROCKET.isLoaded() && (InterfaceCache.IPLANETWORLD.instanceOf(world.provider) || (advRDimensionData != null && advRDimensionData.keySet().contains(world.provider.dimensionId)));
	}

	public static float getExtraGravity(World world) {
		if (isGalacticWorld(world)) {
			return ((IGalacticraftWorldProvider)world.provider).getGravity();
		}
		else if (isAdvRWorld(world)) {

		}
		return 0;
	}

	public static float getWindFactor(World world) {
		if (isGalacticWorld(world)) {
			return ((IGalacticraftWorldProvider)world.provider).getWindLevel();
		}
		else if (isAdvRWorld(world)) {

		}
		return 1;
	}

	static {
		if (ModList.GALACTICRAFT.isLoaded()) {
			try {

			}
			catch (Exception e) {
				DragonAPICore.logError("Could not load GalacticCraft dimension manager!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.GALACTICRAFT, e);
			}
		}
		if (ModList.ADVROCKET.isLoaded()) {
			try {
				Class c = Class.forName("zmaster587.advancedRocketry.dimension.DimensionManager");
				Field inst = c.getDeclaredField("instance");
				inst.setAccessible(true);
				Object ref = inst.get(null);
				Field f = c.getDeclaredField("dimensionList");
				f.setAccessible(true);
				advRDimensionData = (HashMap<Integer, Object>)f.get(ref);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not load AdvR dimension manager!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.ADVROCKET, e);
			}
		}
	}

}

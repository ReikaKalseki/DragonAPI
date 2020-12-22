package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.Calendar;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Interfaces.WinterBiomeStrengthControl;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModInteract.DeepInteract.PlanetDimensionHandler;

public class SpecialDayTracker {

	public static final SpecialDayTracker instance = new SpecialDayTracker();

	private final Calendar calendar;

	private final SimplexNoiseGenerator weatherNoise = new SimplexNoiseGenerator(System.currentTimeMillis());

	private SpecialDayTracker() {
		calendar = Calendar.getInstance();
	}

	public boolean loadAprilTextures() {
		if (!DragonOptions.APRIL.getState())
			return false;
		return calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DAY_OF_MONTH) <= 2;
	}

	public boolean loadXmasTextures() {
		return (calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) >= 18) || (calendar.get(Calendar.MONTH) == Calendar.JANUARY && calendar.get(Calendar.DAY_OF_MONTH) <= 5);
	}

	public float getXmasWeatherStrength(World world) {
		if (!this.isWinterEnabled())
			return 0;
		if (world.provider.hasNoSky || PlanetDimensionHandler.isOtherWorld(world))
			return 0;
		double val = weatherNoise.getValue(world.getTotalWorldTime()/6000D, world.provider.dimensionId*200);
		float norm = (float)Math.sqrt(ReikaMathLibrary.normalizeToBounds(val, 0, 1));

		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		BiomeGenBase biome = world.getBiomeGenForCoords(MathHelper.floor_double(ep.posX), MathHelper.floor_double(ep.posZ));
		if (biome instanceof WinterBiomeStrengthControl)
			norm *= ((WinterBiomeStrengthControl)biome).getStrength(world, ep);

		return MathHelper.clamp_float(norm*1.1F, 0, 1);
	}

	public boolean isWinterEnabled() {
		return this.loadXmasTextures();
	}

	public boolean isHalloween() {
		return calendar.get(Calendar.MONTH) == Calendar.OCTOBER && calendar.get(Calendar.DAY_OF_MONTH) >= 30;
	}

}

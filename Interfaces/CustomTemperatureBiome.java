package Reika.DragonAPI.Interfaces;

import net.minecraft.world.World;

public interface CustomTemperatureBiome {

	int getBaseAmbientTemperature();

	float getSeasonStrength();

	int getSurfaceTemperatureModifier(World world, int x, int y, int z, float temp, float sun);

	int getAltitudeTemperatureModifier(World world, int x, int y, int z, float temp, int dy);

	float getNoiseVariationStrength(World world, int x, int y, int z, float orig);

}

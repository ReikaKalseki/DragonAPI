package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.PositionEventBase;
import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;


public class LightVisualBrightnessEvent extends WorldPositionEvent {

	public final int lightLevel;
	public final float originalValue;
	public float brightness;

	public LightVisualBrightnessEvent(World world, int x, int y, int z, int light) {
		super(world, x, y, z);
		originalValue = this.getBrightnessFor(light);
		brightness = originalValue;
		lightLevel = light;
	}

	public float getBrightnessFor(int light) {
		return world.provider.lightBrightnessTable[light];
	}

	/*
	public static float fire(float orig, World world, int x, int y, int z) {
		LightVisualBrightnessEvent evt = new LightVisualBrightnessEvent(world, x, y, z, orig);
		return evt.brightness;
	}
	 */

	public static float fire(World world, int x, int y, int z) {
		int light = world.getBlockLightValue(x, y, z);
		LightVisualBrightnessEvent evt = new LightVisualBrightnessEvent(world, x, y, z, light);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.brightness;
	}

	public static int fireMixed(int orig, IBlockAccess world, int x, int y, int z, int sky, int block) {
		LightMixedBrightnessEvent evt = new LightMixedBrightnessEvent(world, x, y, z, block, sky, orig);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.value;
	}

	public static class LightMixedBrightnessEvent extends PositionEventBase {

		public final int blockLight;
		public final int skyLight;
		public final int originalValue;
		public int value;

		public LightMixedBrightnessEvent(IBlockAccess world, int x, int y, int z, int bl, int skyl, int orig) {
			super(world, x, y, z);
			blockLight = bl;
			skyLight = skyl;
			originalValue = orig;
			value = orig;
		}

		public int getBrightnessFor(int block, int sky) {
			return sky << 20 | block << 4;
		}

	}

}

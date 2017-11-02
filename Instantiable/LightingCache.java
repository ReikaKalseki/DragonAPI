package Reika.DragonAPI.Instantiable;

import java.util.HashMap;

import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class LightingCache {

	private BlockArray blocks = new BlockArray();
	private final HashMap<Coordinate, CachedBrightness> brightness = new HashMap();
	private final int maxUpdateRate;
	//private final HashSet<Coordinate> queuedUpdates = new HashSet();

	public LightingCache() {
		this(1);
	}

	public LightingCache(int maxr) {
		maxUpdateRate = maxr;
	}

	public void update(World world) {
		brightness.clear();
		//queuedUpdates.clear();
		for (Coordinate c : blocks.keySet()) {
			this.calcBrightness(world, c);
		}
	}

	public void update(World world, int x, int y, int z) {
		Coordinate c = new Coordinate(x, y, z);
		this.calcBrightness(world, c);
		//for (Coordinate c2 : queuedUpdates) {
		//	this.calcBrightness(world, c2);
		//}
	}

	private void calcBrightness(World world, Coordinate c) {
		CachedBrightness cb = brightness.get(c);
		if (cb == null) {
			cb = new CachedBrightness();
			brightness.put(c, cb);
		}
		long time = world.getTotalWorldTime();
		if (time >= cb.lastCalculation+maxUpdateRate) {
			cb.brightness = c.getBlock(world).getMixedBrightnessForBlock(world, c.xCoord, c.yCoord, c.zCoord);
			cb.lastCalculation = time;
		}
		//else if (maxUpdateRate > 1) {
		//	queuedUpdates.add(c);
		//}
	}

	public void setArray(BlockArray b) {
		blocks = b;
	}

	public int getCachedBrightness(World world, Coordinate c) {
		CachedBrightness get = brightness.get(c);
		if (get == null) {
			this.calcBrightness(world, c);
			get = brightness.get(c);
		}
		return get.brightness;
	}

	private static class CachedBrightness {

		private int brightness;
		private long lastCalculation;

	}

}

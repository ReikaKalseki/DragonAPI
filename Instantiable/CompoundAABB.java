package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.command.IEntitySelector;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class CompoundAABB {

	private final Random rand = new Random();

	private final Collection<AxisAlignedBB> boxes = new ArrayList();
	private final WeightedRandom<AxisAlignedBB> boxVolumes = new WeightedRandom();

	private double minX = Integer.MAX_VALUE;
	private double minY = Integer.MAX_VALUE;
	private double minZ = Integer.MAX_VALUE;
	private double maxX = Integer.MIN_VALUE;
	private double maxY = Integer.MIN_VALUE;
	private double maxZ = Integer.MIN_VALUE;

	public CompoundAABB() {

	}

	public CompoundAABB(BlockArray arr) {
		for (BlockArray b : arr.splitToRectangles()) {
			this.addAABB(b.asAABB());
		}
	}

	public CompoundAABB addAABB(AxisAlignedBB box) {
		boxes.add(box);
		boxVolumes.addEntry(box, ReikaAABBHelper.getVolume(box));
		minX = Math.min(minX, box.minX);
		minY = Math.min(minY, box.minY);
		minZ = Math.min(minZ, box.minZ);
		maxX = Math.max(maxX, box.maxX);
		maxY = Math.max(maxY, box.maxY);
		maxZ = Math.max(maxZ, box.maxZ);
		return this;
	}

	public ArrayList getEntitiesWithinAABB(Class c, World world) {
		return this.selectEntitiesWithinAABB(c, world, null);
	}

	public ArrayList selectEntitiesWithinAABB(Class c, World world, IEntitySelector sel) {
		HashSet set = new HashSet();

		for (AxisAlignedBB box : boxes) {
			set.addAll(world.selectEntitiesWithinAABB(c, box, sel));
		}

		return new ArrayList(set);
	}

	public AxisAlignedBB getRandomComponentBox(boolean proportionToSize) {
		return proportionToSize ? boxVolumes.getRandomEntry() : ReikaJavaLibrary.getRandomCollectionEntry(rand, boxes);
	}

	public double getVolume() {
		double ret = 0;
		for (AxisAlignedBB box : boxes) {
			ret += ReikaAABBHelper.getVolume(box);
		}
		return ret;
	}

	public double minX() {
		return minX;
	}

	public double minY() {
		return minY;
	}

	public double minZ() {
		return minZ;
	}

	public double maxX() {
		return maxX;
	}

	public double maxY() {
		return maxY;
	}

	public double maxZ() {
		return maxZ;
	}

}

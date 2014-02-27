/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.world.World;

public class StructuredBlockArray extends BlockArray {

	private final HashMap<List<Integer>, List<Integer>> data = new HashMap();

	private int minX = Integer.MAX_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxY = Integer.MIN_VALUE;
	private int maxZ = Integer.MIN_VALUE;

	public final World world;

	public StructuredBlockArray(World world) {
		this.world = world;
	}

	@Override
	public int[] getNextBlock() {
		if (data.isEmpty())
			return null;
		int[] a = new int[3];
		List<Integer> li = data.keySet().iterator().next();
		for (int i = 0; i < 3; i++)
			a[i] = li.get(i);
		return a;
	}

	@Override
	public int[] getNthBlock(int n) {
		if (data.isEmpty())
			return null;
		int[] arr = new int[3];
		int a = 0;
		for (List<Integer> li : data.keySet()) {
			if (a == n) {
				for (int i = 0; i < 3; i++)
					arr[i] = li.get(i);
			}
			a++;
		}
		return arr;
	}

	@Override
	public int[] getNextAndMoveOn() {
		if (data.isEmpty())
			return null;
		int[] a = new int[3];
		List<Integer> li = data.keySet().iterator().next();
		for (int i = 0; i < 3; i++)
			a[i] = li.get(i);
		data.remove(li);
		return a;
	}

	@Override
	public int getSize() {
		return data.size();
	}

	@Override
	public boolean addBlockCoordinate(int x, int y, int z) {
		if (overflow)
			return false;
		if (this.hasBlock(x, y, z))
			return false;
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		data.put(Arrays.asList(x, y, z), Arrays.asList(id, meta));
		if (minX > x)
			minX = x;
		if (maxX < x)
			maxX = x;
		if (minY > y)
			minY = y;
		if (maxY < y)
			maxY = y;
		if (minZ > z)
			minZ = z;
		if (maxZ < z)
			maxZ = z;
		return true;
	}

	public int getIDRelativeToMinXYZ(int dx, int dy, int dz) {
		int x = dx+minX;
		int y = dy+minY;
		int z = dz+minZ;
		return this.hasBlock(x, y, z) ? data.get(Arrays.asList(x, y, z)).get(0) : -1;
	}

	public int getMetaRelativeToMinXYZ(int dx, int dy, int dz) {
		int x = dx+minX;
		int y = dy+minY;
		int z = dz+minZ;
		return this.hasBlock(x, y, z) ? data.get(Arrays.asList(x, y, z)).get(1) : -1;
	}

	public List<Integer> getBlockRelativeToMinXYZ(int dx, int dy, int dz) {
		int x = dx+minX;
		int y = dy+minY;
		int z = dz+minZ;
		return this.hasBlock(x, y, z) ? data.get(Arrays.asList(x, y, z)) : null;
	}

	@Override
	public boolean hasBlock(int x, int y, int z) {
		return data.keySet().contains(Arrays.asList(x, y, z));
	}

	public int getNumberOf(int id, int meta) {
		int count = 0;
		for (List<Integer> li : data.keySet()) {
			List<Integer> block = data.get(li);
			if (id == block.get(0) && meta == block.get(1))
				count++;
		}
		return count;
	}

	public int getMinX() {
		return minX;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMinY() {
		return minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMinZ() {
		return minZ;
	}

	public int getMaxZ() {
		return maxZ;
	}

	public int getSizeX() {
		return maxX-minX+1;
	}

	public int getSizeY() {
		return maxY-minY+1;
	}

	public int getSizeZ() {
		return maxZ-minZ+1;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}

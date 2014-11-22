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

import net.minecraft.block.Block;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.BlockKey;

public class StructuredBlockArray extends BlockArray {

	private final HashMap<List<Integer>, BlockKey> data = new HashMap();

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
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		data.put(Arrays.asList(x, y, z), new BlockKey(b, meta));
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

	public Block getIDRelativeToMinXYZ(int dx, int dy, int dz) {
		int x = dx+minX;
		int y = dy+minY;
		int z = dz+minZ;
		return this.hasBlock(x, y, z) ? data.get(Arrays.asList(x, y, z)).blockID : null;
	}

	public int getMetaRelativeToMinXYZ(int dx, int dy, int dz) {
		int x = dx+minX;
		int y = dy+minY;
		int z = dz+minZ;
		return this.hasBlock(x, y, z) ? data.get(Arrays.asList(x, y, z)).metadata : -1;
	}

	public BlockKey getBlockRelativeToMinXYZ(int dx, int dy, int dz) {
		int x = dx+minX;
		int y = dy+minY;
		int z = dz+minZ;
		return this.hasBlock(x, y, z) ? data.get(Arrays.asList(x, y, z)) : null;
	}

	@Override
	public boolean hasBlock(int x, int y, int z) {
		return data.keySet().contains(Arrays.asList(x, y, z));
	}

	public int getNumberOf(Block id, int meta) {
		int count = 0;
		for (List<Integer> li : data.keySet()) {
			BlockKey block = data.get(li);
			if (block.match(id, meta))
				count++;
		}
		return count;
	}

	@Override
	public int getMinX() {
		return minX;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}

	@Override
	public int getMinY() {
		return minY;
	}

	@Override
	public int getMaxY() {
		return maxY;
	}

	@Override
	public int getMinZ() {
		return minZ;
	}

	@Override
	public int getMaxZ() {
		return maxZ;
	}

	public int getMidX() {
		return this.getMinX()+this.getSizeX()/2;
	}

	public int getMidY() {
		return this.getMinY()+this.getSizeY()/2;
	}

	public int getMidZ() {
		return this.getMinZ()+this.getSizeZ()/2;
	}

	@Override
	public int getSizeX() {
		return maxX-minX+1;
	}

	@Override
	public int getSizeY() {
		return maxY-minY+1;
	}

	@Override
	public int getSizeZ() {
		return maxZ-minZ+1;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.BlockKey;

public class StructuredBlockArray extends BlockArray {

	private final HashMap<Coordinate, BlockKey> data = new HashMap();

	public final World world;

	public StructuredBlockArray(World world) {
		this.world = world;
	}

	@Override
	public int[] getNextBlock() {
		if (data.isEmpty())
			return null;
		int[] a = new int[3];
		Coordinate li = data.keySet().iterator().next();
		a[0] = li.xCoord;
		a[1] = li.yCoord;
		a[2] = li.zCoord;
		return a;
	}

	@Override
	public int[] getNthBlock(int n) {
		if (data.isEmpty())
			return null;
		int[] arr = new int[3];
		int a = 0;
		for (Coordinate li : data.keySet()) {
			if (a == n) {
				arr[0] = li.xCoord;
				arr[1] = li.yCoord;
				arr[2] = li.zCoord;
			}
			a++;
		}
		return arr;
	}

	@Override
	public Collection<Coordinate> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	@Override
	public int[] getNextAndMoveOn() {
		if (data.isEmpty())
			return null;
		int[] a = new int[3];
		Coordinate li = data.keySet().iterator().next();
		a[0] = li.xCoord;
		a[1] = li.yCoord;
		a[2] = li.zCoord;
		data.remove(li);
		return a;
	}

	@Override
	public boolean addBlockCoordinate(int x, int y, int z) {
		if (overflow)
			return false;
		if (this.hasBlock(x, y, z))
			return false;
		super.addBlockCoordinate(x, y, z);
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		data.put(new Coordinate(x, y, z), new BlockKey(b, meta));
		return true;
	}

	public BlockKey getBlockKeyRelativeToMinXYZ(int dx, int dy, int dz) {
		int x = dx+this.getMinX();
		int y = dy+this.getMinY();
		int z = dz+this.getMinZ();
		return this.hasBlock(x, y, z) ? data.get(new Coordinate(x, y, z)) : null;
	}

	public Block getBlockRelativeToMinXYZ(int dx, int dy, int dz) {
		int x = dx+this.getMinX();
		int y = dy+this.getMinY();
		int z = dz+this.getMinZ();
		return this.hasBlock(x, y, z) ? data.get(new Coordinate(x, y, z)).blockID : null;
	}

	public int getMetaRelativeToMinXYZ(int dx, int dy, int dz) {
		int x = dx+this.getMinX();
		int y = dy+this.getMinY();
		int z = dz+this.getMinZ();
		return this.hasBlock(x, y, z) ? data.get(new Coordinate(x, y, z)).metadata : -1;
	}

	public BlockKey getBlockKeyAt(int x, int y, int z) {
		return this.hasBlock(x, y, z) ? data.get(new Coordinate(x, y, z)) : null;
	}

	public Block getBlockAt(int x, int y, int z) {
		return this.hasBlock(x, y, z) ? data.get(new Coordinate(x, y, z)).blockID : null;
	}

	public int getMetaAt(int x, int y, int z) {
		return this.hasBlock(x, y, z) ? data.get(new Coordinate(x, y, z)).metadata : -1;
	}

	public int getNumberOf(Block id, int meta) {
		int count = 0;
		for (Coordinate li : data.keySet()) {
			BlockKey block = data.get(li);
			if (block.match(id, meta))
				count++;
		}
		return count;
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
	public String toString() {
		return data.toString();
	}
}

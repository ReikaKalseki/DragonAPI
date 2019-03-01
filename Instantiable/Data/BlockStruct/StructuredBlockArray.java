/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;

public class StructuredBlockArray extends BlockArray {

	private final HashMap<Coordinate, BlockKey> data = new HashMap();

	public final World world;

	public StructuredBlockArray(World world) {
		this.world = world;
	}

	@Override
	public Coordinate getNextBlock() {
		if (data.isEmpty())
			return null;
		Coordinate li = data.keySet().iterator().next();
		return li;
	}

	@Override
	public Coordinate getNthBlock(int n) {
		/*
		if (data.isEmpty())
			return null;
		int a = 0;
		for (Coordinate li : data.keySet()) {
			if (a == n) {
				return li;
			}
			a++;
		}
		return null;
		return blocks.get(n);*/
		return super.getNthBlock(n);
	}

	@Override
	public Collection<Coordinate> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	@Override
	public Coordinate getNextAndMoveOn() {
		if (data.isEmpty())
			return null;
		Coordinate li = data.keySet().iterator().next();
		data.remove(li);
		super.removeKey(li);
		return li;
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
		Coordinate c = new Coordinate(x, y, z);
		data.put(c, new BlockKey(b, meta));
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

	public final boolean hasNonAirBlock(int x, int y, int z) {
		Block b = this.getBlockAt(x, y, z);
		return b != null && b != Blocks.air && b.getMaterial() != Material.air && !(b instanceof BlockAir);
	}

	@Override
	public Coordinate getRandomBlock() {
		Coordinate a = super.getRandomBlock();
		return a;
	}

	@Override
	public void remove(int x, int y, int z) {
		super.remove(x, y, z);
		data.remove(new Coordinate(x, y, z));
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

	@Override
	public BlockArray offset(int x, int y, int z) {
		super.offset(x, y, z);
		HashMap<Coordinate, BlockKey> map = new HashMap();
		for (Coordinate c : data.keySet()) {
			map.put(c.offset(x, y, z), data.get(c));
		}
		data.clear();
		data.putAll(map);
		return this;
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
		return data.size()+": "+data.toString();
	}

	@Override
	protected BlockArray instantiate() {
		return new StructuredBlockArray(world);
	}

	@Override
	public void copyTo(BlockArray copy) {
		super.copyTo(copy);
		if (copy instanceof StructuredBlockArray) {
			((StructuredBlockArray)copy).data.putAll(data);
		}
	}

	@Override
	public void addAll(BlockArray arr) {
		super.addAll(arr);
		if (arr instanceof StructuredBlockArray) {
			data.putAll(((StructuredBlockArray)arr).data);
		}
	}

	public ItemHashMap<Integer> getItems() {
		ItemHashMap<Integer> map = new ItemHashMap();
		for (Coordinate c : data.keySet()) {
			BlockKey bk = data.get(c);
			if (bk.blockID instanceof BlockAir)
				continue;
			if (Item.getItemFromBlock(bk.blockID) == null)
				continue;
			ItemStack is = bk.asItemStack();
			Integer get = map.get(is);
			int amt = get != null ? get.intValue() : 0;
			map.put(is, amt+1);
		}
		return map;
	}

	@Override
	public BlockArray rotate90Degrees(int ox, int oz, boolean left) {
		StructuredBlockArray b = (StructuredBlockArray)super.rotate90Degrees(ox, oz, left);
		for (Coordinate c : data.keySet()) {
			BlockKey bc = data.get(c);
			Coordinate c2 = c.rotate90About(ox, oz, left);
			b.data.put(c2, bc);
		}
		return b;
	}

	@Override
	public BlockArray rotate180Degrees(int ox, int oz) {
		StructuredBlockArray b = (StructuredBlockArray)super.rotate180Degrees(ox, oz);
		for (Coordinate c : data.keySet()) {
			BlockKey bc = data.get(c);
			Coordinate c2 = c.rotate180About(ox, oz);
			b.data.put(c2, bc);
		}
		return b;
	}

	@Override
	public void clear() {
		super.clear();
		data.clear();
	}

	@Override
	public BlockArray flipX() {
		StructuredBlockArray b = (StructuredBlockArray)super.flipX();
		for (Coordinate c : data.keySet()) {
			BlockKey bc = data.get(c);
			Coordinate c2 = new Coordinate(-c.xCoord, c.yCoord, c.zCoord);
			b.data.put(c2, bc);
		}
		return b;
	}

	@Override
	public BlockArray flipZ() {
		StructuredBlockArray b = (StructuredBlockArray)super.flipZ();
		for (Coordinate c : data.keySet()) {
			BlockKey bc = data.get(c);
			Coordinate c2 = new Coordinate(c.xCoord, c.yCoord, -c.zCoord);
			b.data.put(c2, bc);
		}
		return b;
	}
}

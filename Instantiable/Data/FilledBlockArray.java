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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.BlockMap.BlockCheck;
import Reika.DragonAPI.Instantiable.Data.BlockMap.BlockKey;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class FilledBlockArray extends StructuredBlockArray {

	private final HashMap<Coordinate, BlockCheck> data = new HashMap();

	public FilledBlockArray(World world) {
		super(world);
	}

	public void loadBlock(int x, int y, int z) {
		this.setBlock(x, y, z, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
	}

	public void setBlock(int x, int y, int z, Block id) {
		this.setBlock(x, y, z , new BlockKey(id));
	}

	public void setBlock(int x, int y, int z, Block id, int meta) {
		this.setBlock(x, y, z , new BlockKey(id, meta));
	}

	public void setBlock(int x, int y, int z, BlockKey bk) {
		super.addBlockCoordinate(x, y, z);
		data.put(new Coordinate(x, y, z), bk);
	}

	public void setEmpty(int x, int y, int z, boolean soft, boolean nonsolid, Block... exceptions) {
		super.addBlockCoordinate(x, y, z);
		data.put(new Coordinate(x, y, z), new EmptyCheck(soft, nonsolid, exceptions));
	}

	public void addBlock(int x, int y, int z, Block id) {
		this.addBlock(x, y, z , new BlockKey(id));
	}

	public void addBlock(int x, int y, int z, Block id, int meta) {
		this.addBlock(x, y, z , new BlockKey(id, meta));
	}

	public void addBlock(int x, int y, int z, BlockKey bk) {
		super.addBlockCoordinate(x, y, z);
		this.addBlockToCoord(new Coordinate(x, y, z), bk);
	}

	private void addBlockToCoord(Coordinate c, BlockKey bk) {
		BlockCheck bc = data.get(c);
		if (bc == null) {
			MultiKey mk = new MultiKey();
			mk.add(bk);
			data.put(c, mk);
		}
		else if (bc instanceof BlockKey) {
			MultiKey mk = new MultiKey();
			mk.add((BlockKey)bc);
			data.put(c, mk);
		}
		else {
			((MultiKey)bc).add(bk);
		}
	}

	private BlockCheck getBlockKey(int x, int y, int z) {
		return data.get(new Coordinate(x, y, z));
	}

	/*
	public Block getBlock(int x, int y, int z) {
		return this.getBlockKey(x, y, z).blockID;
	}

	public int getBlockMetadata(int x, int y, int z) {
		return Math.max(0, this.getBlockKey(x, y, z).metadata);
	}
	 */

	public void place() {
		for (Coordinate c : data.keySet()) {
			//Block b = this.getBlock(x, y, z);
			//int meta = this.getBlockMetadata(x, y, z);
			//world.setBlock(x, y, z, b, meta, 3);
			data.get(c).place(world, c.xCoord, c.yCoord, c.zCoord);
		}
	}

	public boolean hasBlockAt(int x, int y, int z, Block b, int meta) {
		BlockCheck bc = this.getBlockKey(x, y, z);
		return bc != null ? bc.match(world, x, y, z) : false;
	}

	public boolean matchInWorld() {
		for (Coordinate c : data.keySet()) {
			int x = c.xCoord;
			int y = c.yCoord;
			int z = c.zCoord;
			BlockCheck bk = this.getBlockKey(x, y, z);
			if (!bk.match(world, x, y, z)) {
				//ReikaJavaLibrary.pConsole(x+","+y+","+z+" > "+bk+" & "+b+":"+meta);
				//bk.place(world, x, y, z);
				//world.setBlock(x, y+1, z, Blocks.brick_block);
				return false;
			}
		}
		return true;
	}

	@Override
	public void remove(int x, int y, int z) {
		super.remove(x, y, z);
		data.remove(new Coordinate(x, y, z));
	}

	@Override
	public BlockArray offset(int x, int y, int z) {
		HashMap map = new HashMap();
		for (Coordinate key : data.keySet()) {
			int dx = key.xCoord;
			int dy = key.yCoord;
			int dz = key.zCoord;
			dx += x;
			dy += y;
			dz += z;
			map.put(new Coordinate(dx, dy, dz), data.get(key));
		}
		data.clear();
		data.putAll(map);
		return this;
	}

	public void populateBlockData() {
		for (int i = 0; i < this.getSize(); i++) {
			int[] xyz = this.getNthBlock(i);
			int x = xyz[0];
			int y = xyz[1];
			int z = xyz[2];
			Block b = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			this.setBlock(x, y, z, b, meta);
		}
	}

	private static class MultiKey implements BlockCheck {

		private ArrayList<BlockKey> keys = new ArrayList();

		private void add(BlockKey key) {
			if (!keys.contains(key))
				keys.add(key);
		}

		@Override
		public boolean match(World world, int x, int y, int z) {
			return keys.contains(new BlockKey(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z)));
		}

		public void place(World world, int x, int y, int z) {
			world.setBlock(x, y, z, this.getBlock(), this.getMeta(), 3);
		}

		private int getMeta() {
			return keys.isEmpty() ? 0 : keys.get(0).hasMetadata() ? keys.get(0).metadata : 0;
		}

		private Block getBlock() {
			return keys.isEmpty() ? Blocks.air : keys.get(0).blockID;
		}

		@Override
		public String toString() {
			return keys.toString();
		}

	}

	private static class EmptyCheck implements BlockCheck {

		public final boolean allowNonSolid;
		public final boolean allowSoft;
		private final Collection<Block> exceptions;

		private EmptyCheck(boolean soft, boolean nonsolid, Block... exc) {
			allowNonSolid = nonsolid;
			allowSoft = soft;
			exceptions = ReikaJavaLibrary.makeListFromArray(exc);
		}

		@Override
		public boolean match(World world, int x, int y, int z) {
			Block b = world.getBlock(x, y, z);
			if (exceptions.contains(b))
				return false;
			if (b == Blocks.air || b.isAir(world, x, y, z))
				return true;
			if (allowSoft && ReikaWorldHelper.softBlocks(world, x, y, z))
				return true;
			if (allowNonSolid && b.getCollisionBoundingBoxFromPool(world, x, y, z) == null)
				return true;
			return false;
		}

		@Override
		public void place(World world, int x, int y, int z) {
			world.setBlock(x, y, z, Blocks.air);
		}

		@Override
		public String toString() {
			return "[Empty]";
		}

	}

}

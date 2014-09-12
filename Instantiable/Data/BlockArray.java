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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Auxiliary.BlockArrayComputer;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.BlockMap.BlockKey;
import Reika.DragonAPI.Interfaces.SemiTransparent;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class BlockArray {

	protected final List<List<Integer>> blocks = new ArrayList();
	protected Material liquidMat;
	protected boolean overflow = false;
	protected World refWorld;

	private int minX = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int maxY = Integer.MIN_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxZ = Integer.MIN_VALUE;

	public int maxDepth = Integer.MAX_VALUE;

	private final BlockArrayComputer computer;

	public BlockArray() {
		computer = new BlockArrayComputer(this);
	}

	public BlockArray setWorld(World world) {
		refWorld = world;
		return this;
	}

	public boolean addBlockCoordinate(int x, int y, int z) {
		if (overflow)
			return false;
		if (this.hasBlock(x, y, z))
			return false;
		blocks.add(Arrays.asList(x, y, z));
		this.setLimits(x, y, z);
		//ReikaJavaLibrary.pConsole("Adding "+x+", "+y+", "+z);
		return true;
	}

	public boolean addBlockCoordinateIf(World world, int x, int y, int z, Block b, int meta) {
		return this.addBlockCoordinateIf(world, x, y, z, new BlockKey(b, meta));
	}

	public boolean addBlockCoordinateIf(World world, int x, int y, int z, BlockKey bk) {
		if (bk.match(world, x, y, z)) {
			return this.addBlockCoordinate(x, y, z);
		}
		return false;
	}

	public boolean addBlockCoordinateIf(World world, int x, int y, int z, Collection<BlockKey> bk) {
		if (bk.contains(BlockKey.getAt(world, x, y, z))) {
			return this.addBlockCoordinate(x, y, z);
		}
		return false;
	}

	public void remove(int x, int y, int z) {
		blocks.remove(Arrays.asList(x, y, z));
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

	public int getVolume() {
		return this.getSizeX()*this.getSizeY()*this.getSizeZ();
	}

	private void setLimits(int x, int y, int z) {
		if (x < minX)
			minX = x;
		if (x > maxX)
			maxX = x;
		if (y < minY)
			minY = y;
		if (y > maxY)
			maxY = y;
		if (z < minZ)
			minZ = z;
		if (z > maxZ)
			maxZ = z;
	}

	public int[] getNextBlock() {
		if (this.isEmpty())
			return null;
		return this.getReturnArray(0);
	}

	public int[] getNthBlock(int n) {
		if (this.isEmpty())
			return null;
		return this.getReturnArray(n);
	}

	private int[] getReturnArray(int index) {
		List<Integer> arr = blocks.get(index);
		int[] ret = new int[3];
		for (int i = 0; i < 3; i++)
			ret[i] = arr.get(i);
		return ret;
	}

	public int[] getNextAndMoveOn() {
		if (this.isEmpty())
			return null;
		int[] next = this.getNextBlock();
		blocks.remove(0);
		if (this.isEmpty())
			overflow = false;
		return next;
	}

	public int getSize() {
		return blocks.size();
	}

	public void clear() {
		blocks.clear();
		overflow = false;
	}

	public boolean isEmpty() {
		return blocks.size() == 0;
	}

	public boolean hasBlock(int x, int y, int z) {
		return blocks.contains(Arrays.asList(x, y, z));
	}

	/** Recursively adds a contiguous area of one block type, akin to a fill tool.
	 * Args: World, start x, start y, start z, id to follow */
	public void recursiveAdd(World world, int x, int y, int z, Block id) {
		this.recursiveAdd(world, x, y, z, id, 0, new HashMap());
	}

	private void recursiveAdd(World world, int x, int y, int z, Block id, int depth, HashMap<List<Integer>, Integer> map) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (world.getBlock(x, y, z) != id)
			return;
		if (this.hasBlock(x, y, z) && depth >= map.get(Arrays.asList(x, y, z)))
			return;
		this.addBlockCoordinate(x, y, z);
		map.put(Arrays.asList(x, y, z), depth);
		try {
			this.recursiveAdd(world, x+1, y, z, id, depth+1, map);
			this.recursiveAdd(world, x-1, y, z, id, depth+1, map);
			this.recursiveAdd(world, x, y+1, z, id, depth+1, map);
			this.recursiveAdd(world, x, y-1, z, id, depth+1, map);
			this.recursiveAdd(world, x, y, z+1, id, depth+1, map);
			this.recursiveAdd(world, x, y, z-1, id, depth+1, map);
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	/** Recursively adds a contiguous area of one block type, akin to a fill tool.
	 * Args: World, start x, start y, start z, id to follow, metadata to follow */
	public void recursiveAddWithMetadata(World world, int x, int y, int z, Block id, int meta) {
		this.recursiveAddWithMetadata(world, x, y, z, id, meta, 0, new HashMap());
	}

	private void recursiveAddWithMetadata(World world, int x, int y, int z, Block id, int meta, int depth, HashMap<List<Integer>, Integer> map) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (world.getBlock(x, y, z) != id)
			return;
		if (world.getBlockMetadata(x, y, z) != meta)
			return;
		if (this.hasBlock(x, y, z) && depth >= map.get(Arrays.asList(x, y, z)))
			return;
		this.addBlockCoordinate(x, y, z);
		map.put(Arrays.asList(x, y, z), depth);
		try {
			this.recursiveAddWithMetadata(world, x+1, y, z, id, meta, depth+1, map);
			this.recursiveAddWithMetadata(world, x-1, y, z, id, meta, depth+1, map);
			this.recursiveAddWithMetadata(world, x, y+1, z, id, meta, depth+1, map);
			this.recursiveAddWithMetadata(world, x, y-1, z, id, meta, depth+1, map);
			this.recursiveAddWithMetadata(world, x, y, z+1, id, meta, depth+1, map);
			this.recursiveAddWithMetadata(world, x, y, z-1, id, meta, depth+1, map);
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	/** Like the ordinary recursive add but with a bounded volume. Args: World, x, y, z,
	 * id to replace, min x,y,z, max x,y,z */
	public void recursiveAddWithBounds(World world, int x, int y, int z, Block id, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.recursiveAddWithBounds(world, x, y, z, id, x1, y1, z1, x2, y2, z2, 0);
	}

	private void recursiveAddWithBounds(World world, int x, int y, int z, Block id, int x1, int y1, int z1, int x2, int y2, int z2, int depth) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		if (world.getBlock(x, y, z) != id) {
			return;
		}
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);
		try {
			this.recursiveAddWithBounds(world, x+1, y, z, id, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddWithBounds(world, x-1, y, z, id, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddWithBounds(world, x, y+1, z, id, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddWithBounds(world, x, y-1, z, id, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddWithBounds(world, x, y, z+1, id, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddWithBounds(world, x, y, z-1, id, x1, y1, z1, x2, y2, z2, depth+1);
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	/** Like the ordinary recursive add but with a bounded volume. Args: World, x, y, z,
	 * id to replace, min x,y,z, max x,y,z */
	public void recursiveAddWithBoundsRanged(World world, int x, int y, int z, Block id, int x1, int y1, int z1, int x2, int y2, int z2, int r) {
		this.recursiveAddWithBoundsRanged(world, x, y, z, id, x1, y1, z1, x2, y2, z2, r, 0);
	}

	private void recursiveAddWithBoundsRanged(World world, int x, int y, int z, Block id, int x1, int y1, int z1, int x2, int y2, int z2, int r, int depth) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		if (world.getBlock(x, y, z) != id) {
			return;
		}
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);
		try {
			for (int i = -r; i <= r; i++) {
				for (int j = -r; j <= r; j++) {
					for (int k = -r; k <= r; k++) {
						this.recursiveAddWithBoundsRanged(world, x+i, y+j, z+k, id, x1, y1, z1, x2, y2, z2, r, depth+1);
					}
				}
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void recursiveAddMultipleWithBounds(World world, int x, int y, int z, List<Block> id, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.recursiveAddMultipleWithBounds(world, x, y, z, id, x1, y1, z1, x2, y2, z2, 0);
	}

	private void recursiveAddMultipleWithBounds(World world, int x, int y, int z, List<Block> id, int x1, int y1, int z1, int x2, int y2, int z2, int depth) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		boolean flag = false;
		for (int i = 0; i < id.size(); i++) {
			if (world.getBlock(x, y, z) == id.get(i)) {
				flag = true;
			}
		}
		if (!flag)
			return;
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);
		try {
			this.recursiveAddMultipleWithBounds(world, x+1, y, z, id, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddMultipleWithBounds(world, x-1, y, z, id, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddMultipleWithBounds(world, x, y+1, z, id, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddMultipleWithBounds(world, x, y-1, z, id, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddMultipleWithBounds(world, x, y, z+1, id, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddMultipleWithBounds(world, x, y, z-1, id, x1, y1, z1, x2, y2, z2, depth+1);
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void recursiveMultiAddWithBounds(World world, int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2, Block... ids) {
		this.recursiveMultiAddWithBounds(world, x, y, z, x1, y1, z1, x2, y2, z2, 0, ids);
	}

	/** Like the ordinary recursive add but with a bounded volume and tolerance for multiple IDs. Args: World, x, y, z,
	 * id to replace, min x,y,z, max x,y,z */
	private void recursiveMultiAddWithBounds(World world, int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2, int depth, Block... ids) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		boolean flag = false;
		for (int i = 0; i < ids.length; i++) {
			if (world.getBlock(x, y, z) == ids[i]) {
				flag = true;
			}
		}
		if (!flag)
			return;
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);
		try {
			this.recursiveMultiAddWithBounds(world, x+1, y, z, x1, y1, z1, x2, y2, z2, depth+1, ids);
			this.recursiveMultiAddWithBounds(world, x-1, y, z, x1, y1, z1, x2, y2, z2, depth+1, ids);
			this.recursiveMultiAddWithBounds(world, x, y+1, z, x1, y1, z1, x2, y2, z2, depth+1, ids);
			this.recursiveMultiAddWithBounds(world, x, y-1, z, x1, y1, z1, x2, y2, z2, depth+1, ids);
			this.recursiveMultiAddWithBounds(world, x, y, z+1, x1, y1, z1, x2, y2, z2, depth+1, ids);
			this.recursiveMultiAddWithBounds(world, x, y, z-1, x1, y1, z1, x2, y2, z2, depth+1, ids);
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void recursiveAddWithBoundsMetadata(World world, int x, int y, int z, Block id, int meta, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.recursiveAddWithBoundsMetadata(world, x, y, z, id, meta, x1, y1, z1, x2, y2, z2, 0);
	}

	private void recursiveAddWithBoundsMetadata(World world, int x, int y, int z, Block id, int meta, int x1, int y1, int z1, int x2, int y2, int z2, int depth) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		if (world.getBlock(x, y, z) != id || world.getBlockMetadata(x, y, z) != meta) {
			return;
		}
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);
		try {
			this.recursiveAddWithBoundsMetadata(world, x+1, y, z, id, meta, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddWithBoundsMetadata(world, x-1, y, z, id, meta, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddWithBoundsMetadata(world, x, y+1, z, id, meta, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddWithBoundsMetadata(world, x, y-1, z, id, meta, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddWithBoundsMetadata(world, x, y, z+1, id, meta, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddWithBoundsMetadata(world, x, y, z-1, id, meta, x1, y1, z1, x2, y2, z2, depth+1);
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void setLiquid(Material mat) {
		liquidMat = mat;
	}

	public void recursiveAddLiquidWithBounds(World world, int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.recursiveAddLiquidWithBounds(world, x, y, z, x1, y1, z1, x2, y2, z2, 0);
	}

	/** Like the ordinary recursive add but with a bounded volume. Args: World, x, y, z,
	 * id to replace, min x,y,z, max x,y,z */
	private void recursiveAddLiquidWithBounds(World world, int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2, int depth) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		//ReikaJavaLibrary.pConsole(liquidID+" and "+world.getBlock(x, y, z));;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		if (ReikaWorldHelper.getMaterial(world, x, y, z) != liquidMat) {
			//ReikaJavaLibrary.pConsole("Could not match id "+world.getBlock(x, y, z)+" to "+liquidID);
			return;
		}
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);
		try {
			this.recursiveAddLiquidWithBounds(world, x+1, y, z, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddLiquidWithBounds(world, x-1, y, z, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddLiquidWithBounds(world, x, y+1, z, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddLiquidWithBounds(world, x, y-1, z, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddLiquidWithBounds(world, x, y, z+1, x1, y1, z1, x2, y2, z2, depth+1);
			this.recursiveAddLiquidWithBounds(world, x, y, z-1, x1, y1, z1, x2, y2, z2, depth+1);
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	/** Like the ordinary recursive add but with a spherical bounded volume. Args: World, x, y, z,
	 * id to replace, origin x,y,z, max radius */
	private void recursiveAddWithinSphere(World world, int x, int y, int z, Block id, int x0, int y0, int z0, double r, int depth) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (world.getBlock(x, y, z) != id)
			return;
		if (this.hasBlock(x, y, z))
			return;
		if (ReikaMathLibrary.py3d(x-x0, y-y0, z-z0) > r)
			return;
		this.addBlockCoordinate(x, y, z);
		try {
			this.recursiveAddWithinSphere(world, x+1, y, z, id, x0, y0, z0, r, depth+1);
			this.recursiveAddWithinSphere(world, x-1, y, z, id, x0, y0, z0, r, depth+1);
			this.recursiveAddWithinSphere(world, x, y+1, z, id, x0, y0, z0, r, depth+1);
			this.recursiveAddWithinSphere(world, x, y-1, z, id, x0, y0, z0, r, depth+1);
			this.recursiveAddWithinSphere(world, x, y, z+1, id, x0, y0, z0, r, depth+1);
			this.recursiveAddWithinSphere(world, x, y, z-1, id, x0, y0, z0, r, depth+1);
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void sortBlocksByHeight() { //O(n^2)
		List<List<Integer>> newList = new ArrayList();
		for (int i = 0; i < blocks.size(); i++) {
			List<Integer> a = blocks.get(i);
			int y = a.get(1);
			//ReikaJavaLibrary.pConsole("List Size: "+newList.size());
			if (newList.size() == 0) {
				newList.add(a);
				//ReikaJavaLibrary.pConsole("Adding ["+a[0]+","+a[1]+","+a[2]+"] at 0");
			}
			else {
				for (int k = 0; k < newList.size(); k++) {
					int y2 = newList.get(k).get(1);
					if (y < y2) {
						newList.add(k, a);
						//ReikaJavaLibrary.pConsole("Adding ["+a[0]+","+a[1]+","+a[2]+"] at position "+k+" (y="+y+", y2="+y2);
						break;
					}
					else if (k == newList.size()+1) {
						newList.add(a);
						//ReikaJavaLibrary.pConsole("Adding ["+a[0]+","+a[1]+","+a[2]+"] at end of list");
						break;
					}
				}
			}
		}
		blocks.clear();
		blocks.addAll(newList);
	}

	public void reverseBlockOrder() {
		List<List<Integer>> newList = new ArrayList();
		for (int i = 0; i < blocks.size(); i++) {
			newList.add(blocks.get(blocks.size()-1-i));
		}
		blocks.clear();
		blocks.addAll(newList);
	}

	@Override
	public String toString() {
		if (this.isEmpty())
			return "Empty[]";
		StringBuilder list = new StringBuilder();
		for (int i = 0; i < this.getSize(); i++) {
			int[] xyz = this.getReturnArray(i);
			if (refWorld != null) {
				Block id = refWorld.getBlock(xyz[0], xyz[1], xyz[2]);
				int meta = refWorld.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
				list.append(id+":"+meta+" @ ");
			}
			list.append(Arrays.toString(xyz));
			if (i != this.getSize()-1)
				list.append(";");
		}
		return list.toString();
	}

	public void addLineOfClear(World world, int x, int y, int z, int range, int stepx, int stepy, int stepz) {
		if (stepx == 0 && stepy == 0 && stepz == 0)
			throw new MisuseException("The addLineOfClear() method requires a specified direction!");
		if (stepx != 0) {
			if (stepy != 0 || stepz != 0)
				throw new MisuseException("The addLineOfClear() method is only designed for 1D lines!");
			if (stepx != -1 && stepx != 1)
				throw new MisuseException("The addLineOfClear() method is only designed for solid lines!");
			if (stepx == 1) {
				for (int i = x+1; i <= x+range; i++) {
					if (!this.addIfClear(world, i, y, z))
						return;
				}
			}
			else {
				for (int i = x-1; i >= x-range; i--) {
					if (!this.addIfClear(world, i, y, z))
						return;
				}
			}
		}
		else if (stepy != 0) {
			if (stepx != 0 || stepz != 0)
				throw new MisuseException("The addLineOfClear() method is only designed for 1D lines!");
			if (stepy != -1 && stepy != 1)
				throw new MisuseException("The addLineOfClear() method is only designed for solid lines!");
			if (stepy == 1) {
				for (int i = y+1; i <= y+range; i++) {
					if (!this.addIfClear(world, x, i, z))
						return;
				}
			}
			else {
				for (int i = y-1; i >= y-range; i--) {
					if (!this.addIfClear(world, x, i, z))
						return;
				}
			}
		}
		else if (stepz != 0) {
			if (stepy != 0 || stepx != 0)
				throw new MisuseException("The addLineOfClear() method is only designed for 1D lines!");
			if (stepz != -1 && stepz != 1)
				throw new MisuseException("The addLineOfClear() method is only designed for solid lines!");
			if (stepz == 1) {
				for (int i = z+1; i <= z+range; i++) {
					if (!this.addIfClear(world, x, y, i))
						return;
				}
			}
			else {
				for (int i = z-1; i >= z-range; i--) {
					if (!this.addIfClear(world, x, y, i))
						return;
				}
			}
		}
	}

	public boolean addIfClear(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		if (id == Blocks.air) {
			this.addBlockCoordinate(x, y, z);
			return true;
		}
		if (!id.canCollideCheck(world.getBlockMetadata(x, y, z), false) && !BlockLiquid.class.isAssignableFrom(id.getClass())) {
			this.addBlockCoordinate(x, y, z);
			return true;
		}
		if (id instanceof SemiTransparent) {
			int meta = world.getBlockMetadata(x, y, z);
			SemiTransparent b = (SemiTransparent)id;
			if (b.isOpaque(meta))
				return false;
		}
		if (!id.isOpaqueCube()) //do not block but do not add
			return true;
		return false;
	}

	public void addSphere(World world, int x, int y, int z, Block id, double r) {
		if (r == 0)
			return;
		try {
			this.recursiveAddWithinSphere(world, x+1, y, z, id, x, y, z, r, 0);
			this.recursiveAddWithinSphere(world, x, y+1, z, id, x, y, z, r, 0);
			this.recursiveAddWithinSphere(world, x, y, z+1, id, x, y, z, r, 0);
			this.recursiveAddWithinSphere(world, x-1, y, z, id, x, y, z, r, 0);
			this.recursiveAddWithinSphere(world, x, y-1, z, id, x, y, z, r, 0);
			this.recursiveAddWithinSphere(world, x, y, z-1, id, x, y, z, r, 0);
		}
		catch (StackOverflowError e) {
			this.throwOverflow(0);
			e.printStackTrace();
		}
	}

	protected void throwOverflow(int depth) {
		overflow = true;
		ReikaJavaLibrary.pConsole("Stack overflow at depth "+depth+"/"+maxDepth+"!");
	}

	public int[] getRandomBlock() {
		Random r = new Random();
		int s = this.getSize();
		return this.getNthBlock(r.nextInt(s));
	}

	public void remove(int index) {
		blocks.remove(index);
	}

	public boolean isOverflowing() {
		return overflow;
	}

	public boolean hasWorldReference() {
		return refWorld != null;
	}

	public final BlockArray offset(ForgeDirection dir, int dist) {
		return this.offset(dir.offsetX*dist, dir.offsetY*dist, dir.offsetZ*dist);
	}

	public BlockArray offset(int x, int y, int z) {
		for (int i = 0; i < blocks.size(); i++) {
			List<Integer> xyz = blocks.get(i);
			int dx = xyz.get(0)+x;
			int dy = xyz.get(1)+y;
			int dz = xyz.get(2)+z;
			blocks.set(i, Arrays.asList(dx, dy, dz));
		}
		return this;
	}

	public void sink(World world) {
		boolean canSink = true;
		while (canSink) {
			for (int i = 0; i < blocks.size(); i++) {
				int[] xyz = this.getReturnArray(i);
				int x = xyz[0];
				int y = xyz[1];
				int z = xyz[2];
				if (!ReikaWorldHelper.softBlocks(world, x, y-1, z)) {
					canSink = false;
				}
			}
			if (canSink)
				this.offset(0, -1, 0);
		}
	}

	public void sink(World world, Blocks... overrides) {
		boolean canSink = true;
		while (canSink) {
			for (int i = 0; i < blocks.size(); i++) {
				int[] xyz = this.getReturnArray(i);
				int x = xyz[0];
				int y = xyz[1];
				int z = xyz[2];
				Block idy = world.getBlock(x, y-1, z);
				if (!ReikaWorldHelper.softBlocks(world, x, y-1, z) && !ReikaArrayHelper.contains(overrides, idy)) {
					canSink = false;
				}
			}
			if (canSink)
				this.offset(0, -1, 0);
		}
	}

	public void sink(World world, Material... overrides) {
		boolean canSink = true;
		while (canSink) {
			for (int i = 0; i < blocks.size(); i++) {
				int[] xyz = this.getReturnArray(i);
				int x = xyz[0];
				int y = xyz[1];
				int z = xyz[2];
				if (minY <= 0 || y <= 0) {
					canSink = false;
					break;
				}
				Material idy = ReikaWorldHelper.getMaterial(world, x, y-1, z);
				if (!ReikaWorldHelper.softBlocks(world, x, y-1, z) && !ReikaArrayHelper.contains(overrides, idy) && idy.isSolid()) {
					canSink = false;
					break;
				}
			}
			if (canSink)
				this.offset(0, -1, 0);
		}
	}

	/** Pre-collates them into forced stacks to help with FPS. Will not attempt to stack NBT-sensitive items. */
	public final ArrayList<ItemStack> getAllDroppedItems(World world, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		ArrayList<ItemStack> nbt = new ArrayList();
		ItemHashMap<Integer> map = new ItemHashMap();
		for (int i = 0; i < blocks.size(); i++) {
			int[] xyz = this.getReturnArray(i);
			int x = xyz[0];
			int y = xyz[1];
			int z = xyz[2];
			Block b = world.getBlock(x, y, z);
			if (b != null && b != Blocks.air) {
				int metadata = world.getBlockMetadata(x, y, z);
				ArrayList<ItemStack> drop = b.getDrops(world, x, y, z, metadata, fortune);
				for (int k = 0; k < drop.size(); k++) {
					ItemStack is = drop.get(k);
					if (is.stackTagCompound != null) {
						nbt.add(is);
					}
					else {
						if (map.containsKey(is)) {
							int cur = map.get(is);
							cur += is.stackSize;
							map.put(is, cur);
						}
						else {
							map.put(is, is.stackSize);
						}
					}
				}
			}
		}
		for (ItemStack is : map.keySet()) {
			int count = map.get(is);
			int max = is.getMaxStackSize();
			if (count > max) {
				while (count > 0) {
					int add = Math.min(count, max);
					li.add(ReikaItemHelper.getSizedItemStack(is, add));
					count -= add;
				}
			}
			else {
				li.add(ReikaItemHelper.getSizedItemStack(is, count));
			}
		}
		li.addAll(nbt);
		return li;
	}

	public BlockArray copy() {
		BlockArray copy = new BlockArray();
		copy.refWorld = refWorld;
		copy.liquidMat = liquidMat;
		copy.overflow = overflow;
		copy.blocks.clear();
		copy.blocks.addAll(blocks);
		return copy;
	}

	public final boolean isAtLeastXPercentNot(World world, double percent, Block id, int meta) {
		double s = this.getSize();
		int c = 0;
		for (int i = 0; i < this.getSize(); i++) {
			int[] xyz = this.getNthBlock(i);
			int x = xyz[0];
			int y = xyz[1];
			int z = xyz[2];
			Block id2 = world.getBlock(x, y, z);
			int meta2 = world.getBlockMetadata(x, y, z);
			if (id2 != id || meta2 != meta) {
				c++;
			}
		}
		return c/s*100D >= percent;
	}

	public final boolean isAtLeastXPercent(World world, double percent, Block id) {
		return this.isAtLeastXPercent(world, percent, id, -1);
	}

	public final boolean isAtLeastXPercent(World world, double percent, Block id, int meta) {
		double s = this.getSize();
		int c = 0;
		for (int i = 0; i < this.getSize(); i++) {
			int[] xyz = this.getNthBlock(i);
			int x = xyz[0];
			int y = xyz[1];
			int z = xyz[2];
			Block id2 = world.getBlock(x, y, z);
			int meta2 = world.getBlockMetadata(x, y, z);
			if (id2 == id && (meta == -1 || meta2 == meta)) {
				c++;
			}
		}
		return c/s*100D >= percent;
	}

	public final boolean isAtLeastXPercentSolid(World world, double percent) {
		return this.isAtLeastXPercentNot(world, percent, Blocks.air, 0);
	}

	public void setTo(Block b) {
		this.setTo(b, 0);
	}

	public void setTo(Block b, int meta) {
		if (refWorld != null) {
			for (int i = 0; i < this.getSize(); i++) {
				int[] xyz = this.getNthBlock(i);
				refWorld.setBlock(xyz[0], xyz[1], xyz[2], b, meta, 3);
			}
		}
		else {
			throw new MisuseException("Cannot apply operations to a null world!");
		}
	}

	public void clearArea() {
		this.setTo(Blocks.air);
	}

	public void writeToNBT(String label, NBTTagCompound NBT) {
		NBTTagList li = new NBTTagList();
		for (int i = 0; i < this.getSize(); i++) {
			NBTTagCompound tag = new NBTTagCompound();
			int[] xyz = this.getNthBlock(i);
			tag.setInteger("x", xyz[0]);
			tag.setInteger("y", xyz[1]);
			tag.setInteger("z", xyz[2]);
			li.appendTag(tag);
		}
		NBT.setTag(label, li);
	}

	public void readFromNBT(String label, NBTTagCompound NBT) {
		this.clear();
		NBTTagList tag = NBT.getTagList(label, NBT.getId());
		if (tag == null || tag.tagCount() == 0)
			return;
		for (int i = 0; i < tag.tagCount(); i++) {
			NBTTagCompound coord = tag.getCompoundTagAt(i);
			int x = coord.getInteger("x");
			int y = coord.getInteger("y");
			int z = coord.getInteger("z");
			this.addBlockCoordinate(x, y, z);
		}
	}

	public void shaveToCube() {
		//TODO
	}

	public AxisAlignedBB asAABB() {
		return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX+1, maxY+1, maxZ+1);
	}

	public BlockBox asBlockBox() {
		return new BlockBox(minX, minY, minZ, maxX, maxY, maxZ);
	}
}

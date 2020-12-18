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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.BlockArrayComputer;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.AbstractSearch.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Interfaces.Block.SemiTransparent;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class BlockArray implements Iterable<Coordinate> {

	private static final int DEPTH_LIMIT = getMaxDepth();

	protected static final Random rand = new Random();

	private final ArrayList<Coordinate> blocks = new ArrayList();
	private final HashSet<Coordinate> keys = new HashSet();
	protected boolean overflow = false;
	protected World refWorld;

	private int minX = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int maxY = Integer.MIN_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxZ = Integer.MIN_VALUE;

	public int maxDepth = DEPTH_LIMIT;
	public boolean extraSpread = false;
	public boolean taxiCabDistance = false;

	private final BlockArrayComputer computer;

	public BlockBox bounds = BlockBox.infinity();

	public BlockArray() {
		this(null);
	}

	private static int getMaxDepth() {
		int get = ReikaJavaLibrary.getMaximumRecursiveDepth();
		return get > 1000 ? get-250 : Integer.MAX_VALUE;
	}

	public BlockArray(Collection<Coordinate> li) {
		computer = new BlockArrayComputer(this);
		if (li != null) {
			for (Coordinate c : li) {
				this.addBlockCoordinate(c.xCoord, c.yCoord, c.zCoord);
			}
		}
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
		if (!bounds.isBlockInside(x, y, z))
			return false;
		Coordinate c = new Coordinate(x, y, z);
		this.addKey(c);
		this.setLimits(x, y, z);
		//DragonAPICore.log("Adding "+x+", "+y+", "+z);
		return true;
	}

	protected void addKey(Coordinate c) {
		blocks.add(c);
		keys.add(c);
	}

	public boolean addBlockCoordinateIf(World world, int x, int y, int z, Block b, int meta) {
		return this.addBlockCoordinateIf(world, x, y, z, new BlockKey(b, meta));
	}

	public boolean addBlockCoordinateIf(World world, int x, int y, int z, Block b) {
		return this.addBlockCoordinateIf(world, x, y, z, new BlockKey(b));
	}

	public boolean addBlockCoordinateIf(World world, int x, int y, int z, BlockKey bk) {
		if (bk.matchInWorld(world, x, y, z)) {
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
		Coordinate c = new Coordinate(x, y, z);
		this.removeKey(c);
		if (this.isEdge(x, y, z)) {
			this.recalcLimits();
		}
	}

	protected void removeKey(Coordinate c) {
		blocks.remove(c);
		keys.remove(c);
	}

	protected boolean containsKey(Coordinate c) {
		return keys.contains(c);
	}

	public void recalcLimits() {
		this.resetLimits();
	}

	private void resetLimits() {
		//DragonAPICore.log(minX+","+minY+","+minZ+" > "+maxX+","+maxY+","+maxZ, Side.SERVER);
		minX = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;
		minY = Integer.MAX_VALUE;
		maxY = Integer.MIN_VALUE;
		minZ = Integer.MAX_VALUE;
		maxZ = Integer.MIN_VALUE;
		for (Coordinate c : blocks) {
			this.setLimits(c.xCoord, c.yCoord, c.zCoord);
		}
		//DragonAPICore.log(minX+","+minY+","+minZ+" > "+maxX+","+maxY+","+maxZ, Side.SERVER);
	}

	public final boolean isEdge(int x, int y, int z) {
		return this.isEdgeX(x) || this.isEdgeY(y) || this.isEdgeZ(z);
	}

	public final boolean isEdgeX(int x) {
		return x == minX || x == maxX;
	}

	public final boolean isEdgeY(int y) {
		return y == minY || y == maxY;
	}

	public final boolean isEdgeZ(int z) {
		return z == minZ || z == maxZ;
	}

	public final int getMinX() {
		return minX;
	}

	public final int getMaxX() {
		return maxX;
	}

	public final int getMinY() {
		return minY;
	}

	public final int getMaxY() {
		return maxY;
	}

	public final int getMinZ() {
		return minZ;
	}

	public final int getMaxZ() {
		return maxZ;
	}

	public final int getSizeX() {
		return this.isEmpty() ? 0 : maxX-minX+1;
	}

	public final int getSizeY() {
		return this.isEmpty() ? 0 : maxY-minY+1;
	}

	public final int getSizeZ() {
		return this.isEmpty() ? 0 : maxZ-minZ+1;
	}

	public final int getVolume() {
		return this.getSizeX()*this.getSizeY()*this.getSizeZ();
	}

	private final void setLimits(int x, int y, int z) {
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

	public Coordinate getNextBlock() {
		if (this.isEmpty())
			return null;
		return blocks.get(0);
	}

	public Coordinate getNthBlock(int n) {
		if (this.isEmpty())
			return null;
		return blocks.get(n);
	}

	public Set<Coordinate> keySet() {
		return Collections.unmodifiableSet(keys);
	}

	public List<Coordinate> list() {
		return Collections.unmodifiableList(blocks);
	}

	public Coordinate getNextAndMoveOn() {
		if (this.isEmpty())
			return null;
		Coordinate next = this.getNextBlock();
		this.remove(0);
		if (this.isEmpty())
			overflow = false;
		return next;
	}

	public final int getBottomBlockAtXZ(int x, int z) {
		int minY = Integer.MAX_VALUE;
		for (Coordinate c : this.keySet()) {
			if (c.yCoord < minY)
				minY = c.yCoord;
		}
		return minY;
	}

	public final int getSize() {
		return blocks.size();
	}

	public void clear() {
		blocks.clear();
		keys.clear();
		overflow = false;
	}

	public final boolean isEmpty() {
		return blocks.isEmpty();
	}

	public final boolean hasBlock(int x, int y, int z) {
		return this.containsKey(new Coordinate(x, y, z));
	}

	public final boolean hasBlock(Coordinate c) {
		return this.containsKey(c);
	}

	/** Recursively adds a contiguous area of one block type, akin to a fill tool.
	 * Args: World, start x, start y, start z, id to follow */
	public void recursiveAdd(IBlockAccess world, int x, int y, int z, Block id) {
		this.recursiveAdd(world, x, y, z, x, y, z, id, 0, new HashMap());
	}

	private void recursiveAdd(IBlockAccess world, int x0, int y0, int z0, int x, int y, int z, Block id, int depth, HashMap<Coordinate, Integer> map) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (taxiCabDistance && Math.abs(x-x0)+Math.abs(y-y0)+Math.abs(z-z0) > maxDepth)
			return;
		if (world.getBlock(x, y, z) != id)
			return;
		Coordinate c = new Coordinate(x, y, z);
		if (map.containsKey(c) && depth >= map.get(c))
			return;
		this.addBlockCoordinate(x, y, z);
		map.put(c, depth);
		try {
			if (extraSpread) {
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						for (int k = -1; k <= 1; k++)
							this.recursiveAdd(world, x0, y0, z0, x+i, y+j, z+k, id, depth+1, map);
			}
			else {
				this.recursiveAdd(world, x0, y0, z0, x+1, y, z, id, depth+1, map);
				this.recursiveAdd(world, x0, y0, z0, x-1, y, z, id, depth+1, map);
				this.recursiveAdd(world, x0, y0, z0, x, y+1, z, id, depth+1, map);
				this.recursiveAdd(world, x0, y0, z0, x, y-1, z, id, depth+1, map);
				this.recursiveAdd(world, x0, y0, z0, x, y, z+1, id, depth+1, map);
				this.recursiveAdd(world, x0, y0, z0, x, y, z-1, id, depth+1, map);
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	/** Recursively adds a contiguous area of one block type, akin to a fill tool.
	 * Args: World, start x, start y, start z, id to follow, metadata to follow */
	public void recursiveAddWithMetadata(IBlockAccess world, int x, int y, int z, Block id, int meta) {
		this.recursiveAddWithMetadata(world, x, y, z, x, y, z, id, meta, 0, new HashMap());
	}

	private void recursiveAddWithMetadata(IBlockAccess world, int x0, int y0, int z0, int x, int y, int z, Block id, int meta, int depth, HashMap<Coordinate, Integer> map) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (taxiCabDistance && Math.abs(x-x0)+Math.abs(y-y0)+Math.abs(z-z0) > maxDepth)
			return;
		if (world.getBlock(x, y, z) != id)
			return;
		if (world.getBlockMetadata(x, y, z) != meta)
			return;
		Coordinate c = new Coordinate(x, y, z);
		if (map.containsKey(c) && depth >= map.get(c))
			return;
		this.addBlockCoordinate(x, y, z);
		map.put(c, depth);
		try {
			if (extraSpread) {
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						for (int k = -1; k <= 1; k++)
							this.recursiveAddWithMetadata(world, x0, y0, z0, x+i, y+j, z+k, id, meta, depth+1, map);
			}
			else {
				this.recursiveAddWithMetadata(world, x0, y0, z0, x+1, y, z, id, meta, depth+1, map);
				this.recursiveAddWithMetadata(world, x0, y0, z0, x-1, y, z, id, meta, depth+1, map);
				this.recursiveAddWithMetadata(world, x0, y0, z0, x, y+1, z, id, meta, depth+1, map);
				this.recursiveAddWithMetadata(world, x0, y0, z0, x, y-1, z, id, meta, depth+1, map);
				this.recursiveAddWithMetadata(world, x0, y0, z0, x, y, z+1, id, meta, depth+1, map);
				this.recursiveAddWithMetadata(world, x0, y0, z0, x, y, z-1, id, meta, depth+1, map);
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	/** Like the ordinary recursive add but with a bounded volume. Args: World, x, y, z,
	 * id to replace, min x,y,z, max x,y,z */
	public void recursiveAddWithBounds(IBlockAccess world, int x, int y, int z, Block id, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.recursiveAddWithBounds(world, x, y, z, x, y, z, id, x1, y1, z1, x2, y2, z2, 0);
	}

	private void recursiveAddWithBounds(IBlockAccess world, int x0, int y0, int z0, int x, int y, int z, Block id, int x1, int y1, int z1, int x2, int y2, int z2, int depth) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (taxiCabDistance && Math.abs(x-x0)+Math.abs(y-y0)+Math.abs(z-z0) > maxDepth)
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
			if (extraSpread) {
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						for (int k = -1; k <= 1; k++)
							this.recursiveAddWithBounds(world, x0, y0, z0, x+i, y+j, z+k, id, x1, y1, z1, x2, y2, z2, depth+1);
			}
			else {
				this.recursiveAddWithBounds(world, x0, y0, z0, x+1, y, z, id, x1, y1, z1, x2, y2, z2, depth+1);
				this.recursiveAddWithBounds(world, x0, y0, z0, x-1, y, z, id, x1, y1, z1, x2, y2, z2, depth+1);
				this.recursiveAddWithBounds(world, x0, y0, z0, x, y+1, z, id, x1, y1, z1, x2, y2, z2, depth+1);
				this.recursiveAddWithBounds(world, x0, y0, z0, x, y-1, z, id, x1, y1, z1, x2, y2, z2, depth+1);
				this.recursiveAddWithBounds(world, x0, y0, z0, x, y, z+1, id, x1, y1, z1, x2, y2, z2, depth+1);
				this.recursiveAddWithBounds(world, x0, y0, z0, x, y, z-1, id, x1, y1, z1, x2, y2, z2, depth+1);
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	/** Like the ordinary recursive add but with a bounded volume; specifically excludes fluid source (meta == 0) blocks. Args: World, x, y, z,
	 * id to replace, min x,y,z, max x,y,z */
	public void recursiveAddWithBoundsNoFluidSource(IBlockAccess world, int x, int y, int z, Block id, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.recursiveAddWithBoundsNoFluidSource(world, x, y, z, x, y, z, id, x1, y1, z1, x2, y2, z2, 0);
	}

	private void recursiveAddWithBoundsNoFluidSource(IBlockAccess world, int x0, int y0, int z0, int x, int y, int z, Block id, int x1, int y1, int z1, int x2, int y2, int z2, int depth) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (taxiCabDistance && Math.abs(x-x0)+Math.abs(y-y0)+Math.abs(z-z0) > maxDepth)
			return;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		if (world.getBlock(x, y, z) != id) {
			return;
		}
		if (world.getBlockMetadata(x, y, z) == 0)
			return;
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);
		try {
			if (extraSpread) {
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						for (int k = -1; k <= 1; k++)
							this.recursiveAddWithBoundsNoFluidSource(world, x0, y0, z0, x+i, y+j, z+k, id, x1, y1, z1, x2, y2, z2, depth+1);
			}
			else {
				this.recursiveAddWithBoundsNoFluidSource(world, x0, y0, z0, x+1, y, z, id, x1, y1, z1, x2, y2, z2, depth+1);
				this.recursiveAddWithBoundsNoFluidSource(world, x0, y0, z0, x-1, y, z, id, x1, y1, z1, x2, y2, z2, depth+1);
				this.recursiveAddWithBoundsNoFluidSource(world, x0, y0, z0, x, y+1, z, id, x1, y1, z1, x2, y2, z2, depth+1);
				this.recursiveAddWithBoundsNoFluidSource(world, x0, y0, z0, x, y-1, z, id, x1, y1, z1, x2, y2, z2, depth+1);
				this.recursiveAddWithBoundsNoFluidSource(world, x0, y0, z0, x, y, z+1, id, x1, y1, z1, x2, y2, z2, depth+1);
				this.recursiveAddWithBoundsNoFluidSource(world, x0, y0, z0, x, y, z-1, id, x1, y1, z1, x2, y2, z2, depth+1);
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	/** Like the ordinary recursive add but with a bounded volume. Args: World, x, y, z,
	 * id to replace, min x,y,z, max x,y,z */
	public void recursiveAddWithBoundsRanged(IBlockAccess world, int x, int y, int z, Block id, int x1, int y1, int z1, int x2, int y2, int z2, int r) {
		this.recursiveAddWithBoundsRanged(world, x, y, z, x, y, z, id, x1, y1, z1, x2, y2, z2, r, 0);
	}

	private void recursiveAddWithBoundsRanged(IBlockAccess world, int x0, int y0, int z0, int x, int y, int z, Block id, int x1, int y1, int z1, int x2, int y2, int z2, int r, int depth) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (taxiCabDistance && Math.abs(x-x0)+Math.abs(y-y0)+Math.abs(z-z0) > maxDepth)
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
						this.recursiveAddWithBoundsRanged(world, x0, y0, z0, x+i, y+j, z+k, id, x1, y1, z1, x2, y2, z2, r, depth+1);
					}
				}
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void recursiveAddMultipleWithBounds(IBlockAccess world, int x, int y, int z, Set<BlockKey> ids, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.recursiveAddMultipleWithBounds(world, x, y, z, x, y, z, ids, x1, y1, z1, x2, y2, z2, 0, new HashMap());
	}

	private void recursiveAddMultipleWithBounds(IBlockAccess world, int x0, int y0, int z0, int x, int y, int z, Set<BlockKey> ids, int x1, int y1, int z1, int x2, int y2, int z2, int depth, HashMap<Coordinate, Integer> map) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (taxiCabDistance && Math.abs(x-x0)+Math.abs(y-y0)+Math.abs(z-z0) > maxDepth)
			return;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		boolean flag = false;
		BlockKey bk = BlockKey.getAt(world, x, y, z);
		if (ids.contains(bk)) {
			flag = true;
		}
		if (!flag)
			return;
		if (this.hasBlock(x, y, z))
			;//return;
		Coordinate c = new Coordinate(x, y, z);
		if (map.containsKey(c) && depth >= map.get(c)) {
			return;
		}
		this.addBlockCoordinate(x, y, z);
		map.put(c, depth);
		try {
			if (extraSpread) {
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						for (int k = -1; k <= 1; k++)
							this.recursiveAddMultipleWithBounds(world, x0, y0, z0, x+i, y+j, z+k, ids, x1, y1, z1, x2, y2, z2, depth+1, map);
			}
			else {
				this.recursiveAddMultipleWithBounds(world, x0, y0, z0, x+1, y, z, ids, x1, y1, z1, x2, y2, z2, depth+1, map);
				this.recursiveAddMultipleWithBounds(world, x0, y0, z0, x-1, y, z, ids, x1, y1, z1, x2, y2, z2, depth+1, map);
				this.recursiveAddMultipleWithBounds(world, x0, y0, z0, x, y+1, z, ids, x1, y1, z1, x2, y2, z2, depth+1, map);
				this.recursiveAddMultipleWithBounds(world, x0, y0, z0, x, y-1, z, ids, x1, y1, z1, x2, y2, z2, depth+1, map);
				this.recursiveAddMultipleWithBounds(world, x0, y0, z0, x, y, z+1, ids, x1, y1, z1, x2, y2, z2, depth+1, map);
				this.recursiveAddMultipleWithBounds(world, x0, y0, z0, x, y, z-1, ids, x1, y1, z1, x2, y2, z2, depth+1, map);
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void recursiveMultiAddWithBounds(IBlockAccess world, int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2, Block... ids) {
		this.recursiveMultiAddWithBounds(world, x, y, z, x, y, z, x1, y1, z1, x2, y2, z2, 0, ids);
	}

	/** Like the ordinary recursive add but with a bounded volume and tolerance for multiple IDs. Args: World, x, y, z,
	 * id to replace, min x,y,z, max x,y,z */
	private void recursiveMultiAddWithBounds(IBlockAccess world, int x0, int y0, int z0, int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2, int depth, Block... ids) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (taxiCabDistance && Math.abs(x-x0)+Math.abs(y-y0)+Math.abs(z-z0) > maxDepth)
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
			if (extraSpread) {
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						for (int k = -1; k <= 1; k++)
							this.recursiveMultiAddWithBounds(world, x0, y0, z0, x+i, y+j, z+k, x1, y1, z1, x2, y2, z2, depth+1, ids);
			}
			else {
				this.recursiveMultiAddWithBounds(world, x0, y0, z0, x+1, y, z, x1, y1, z1, x2, y2, z2, depth+1, ids);
				this.recursiveMultiAddWithBounds(world, x0, y0, z0, x-1, y, z, x1, y1, z1, x2, y2, z2, depth+1, ids);
				this.recursiveMultiAddWithBounds(world, x0, y0, z0, x, y+1, z, x1, y1, z1, x2, y2, z2, depth+1, ids);
				this.recursiveMultiAddWithBounds(world, x0, y0, z0, x, y-1, z, x1, y1, z1, x2, y2, z2, depth+1, ids);
				this.recursiveMultiAddWithBounds(world, x0, y0, z0, x, y, z+1, x1, y1, z1, x2, y2, z2, depth+1, ids);
				this.recursiveMultiAddWithBounds(world, x0, y0, z0, x, y, z-1, x1, y1, z1, x2, y2, z2, depth+1, ids);
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void recursiveAddWithBoundsMetadata(IBlockAccess world, int x, int y, int z, Block id, int meta, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.recursiveAddWithBoundsMetadata(world, x, y, z, x, y, z, id, meta, x1, y1, z1, x2, y2, z2, 0, new HashMap());
	}

	private void recursiveAddWithBoundsMetadata(IBlockAccess world, int x0, int y0, int z0, int x, int y, int z, Block id, int meta, int x1, int y1, int z1, int x2, int y2, int z2, int depth, HashMap<Coordinate, Integer> map) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (taxiCabDistance && Math.abs(x-x0)+Math.abs(y-y0)+Math.abs(z-z0) > maxDepth)
			return;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		if (world.getBlock(x, y, z) != id || world.getBlockMetadata(x, y, z) != meta) {
			return;
		}
		if (this.hasBlock(x, y, z))
			;//return;
		Coordinate c = new Coordinate(x, y, z);
		if (map.containsKey(c) && depth >= map.get(c)) {
			return;
		}
		this.addBlockCoordinate(x, y, z);
		map.put(c, depth);
		try {
			if (extraSpread) {
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						for (int k = -1; k <= 1; k++)
							this.recursiveAddWithBoundsMetadata(world, x0, y0, z0, x+i, y+j, z+k, id, meta, x1, y1, z1, x2, y2, z2, depth+1, map);
			}
			else {
				this.recursiveAddWithBoundsMetadata(world, x0, y0, z0, x+1, y, z, id, meta, x1, y1, z1, x2, y2, z2, depth+1, map);
				this.recursiveAddWithBoundsMetadata(world, x0, y0, z0, x-1, y, z, id, meta, x1, y1, z1, x2, y2, z2, depth+1, map);
				this.recursiveAddWithBoundsMetadata(world, x0, y0, z0, x, y+1, z, id, meta, x1, y1, z1, x2, y2, z2, depth+1, map);
				this.recursiveAddWithBoundsMetadata(world, x0, y0, z0, x, y-1, z, id, meta, x1, y1, z1, x2, y2, z2, depth+1, map);
				this.recursiveAddWithBoundsMetadata(world, x0, y0, z0, x, y, z+1, id, meta, x1, y1, z1, x2, y2, z2, depth+1, map);
				this.recursiveAddWithBoundsMetadata(world, x0, y0, z0, x, y, z-1, id, meta, x1, y1, z1, x2, y2, z2, depth+1, map);
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void recursiveAddCallbackWithBounds(World world, int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2, PropagationCondition f) {
		this.recursiveAddCallbackWithBounds(world, x, y, z, x, y, z, x1, y1, z1, x2, y2, z2, f, 0, new HashMap());
	}

	private void recursiveAddCallbackWithBounds(World world, int x0, int y0, int z0, int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2, PropagationCondition f, int depth, HashMap<Coordinate, Integer> map) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (taxiCabDistance && Math.abs(x-x0)+Math.abs(y-y0)+Math.abs(z-z0) > maxDepth)
			return;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		if (!f.isValidLocation(world, x, y, z, new Coordinate(x0, y0, z0))) {
			return;
		}
		if (this.hasBlock(x, y, z))
			;//return;
		Coordinate c = new Coordinate(x, y, z);
		if (map.containsKey(c) && depth >= map.get(c)) {
			return;
		}
		this.addBlockCoordinate(x, y, z);
		map.put(c, depth);
		try {
			if (extraSpread) {
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						for (int k = -1; k <= 1; k++)
							this.recursiveAddCallbackWithBounds(world, x0, y0, z0, x+i, y+j, z+k, x1, y1, z1, x2, y2, z2, f, depth+1, map);
			}
			else {
				this.recursiveAddCallbackWithBounds(world, x0, y0, z0, x+1, y, z, x1, y1, z1, x2, y2, z2, f, depth+1, map);
				this.recursiveAddCallbackWithBounds(world, x0, y0, z0, x-1, y, z, x1, y1, z1, x2, y2, z2, f, depth+1, map);
				this.recursiveAddCallbackWithBounds(world, x0, y0, z0, x, y+1, z, x1, y1, z1, x2, y2, z2, f, depth+1, map);
				this.recursiveAddCallbackWithBounds(world, x0, y0, z0, x, y-1, z, x1, y1, z1, x2, y2, z2, f, depth+1, map);
				this.recursiveAddCallbackWithBounds(world, x0, y0, z0, x, y, z+1, x1, y1, z1, x2, y2, z2, f, depth+1, map);
				this.recursiveAddCallbackWithBounds(world, x0, y0, z0, x, y, z-1, x1, y1, z1, x2, y2, z2, f, depth+1, map);
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void recursiveAddLiquidWithBounds(IBlockAccess world, int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2, Fluid liquid) {
		this.recursiveAddLiquidWithBounds(world, x, y, z, x, y, z, x1, y1, z1, x2, y2, z2, 0, liquid);
	}

	/** Like the ordinary recursive add but with a bounded volume. Args: World, x, y, z,
	 * id to replace, min x,y,z, max x,y,z */
	private void recursiveAddLiquidWithBounds(IBlockAccess world, int x0, int y0, int z0, int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2, int depth, Fluid liquid) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (taxiCabDistance && Math.abs(x-x0)+Math.abs(y-y0)+Math.abs(z-z0) > maxDepth)
			return;
		//DragonAPICore.log(liquidID+" and "+world.getBlock(x, y, z));;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		Fluid f2 = FluidRegistry.lookupFluidForBlock(world.getBlock(x, y, z));
		if (f2 == null)
			return;
		if (liquid != null && f2 != liquid) {
			//DragonAPICore.log("Could not match id "+world.getBlock(x, y, z)+" to "+liquidID);
			return;
		}
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);
		try {
			if (extraSpread) {
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						for (int k = -1; k <= 1; k++)
							this.recursiveAddLiquidWithBounds(world, x0, y0, z0, x+i, y+j, z+k, x1, y1, z1, x2, y2, z2, depth+1, liquid);
			}
			else {
				this.recursiveAddLiquidWithBounds(world, x0, y0, z0, x+1, y, z, x1, y1, z1, x2, y2, z2, depth+1, liquid);
				this.recursiveAddLiquidWithBounds(world, x0, y0, z0, x-1, y, z, x1, y1, z1, x2, y2, z2, depth+1, liquid);
				this.recursiveAddLiquidWithBounds(world, x0, y0, z0, x, y+1, z, x1, y1, z1, x2, y2, z2, depth+1, liquid);
				this.recursiveAddLiquidWithBounds(world, x0, y0, z0, x, y-1, z, x1, y1, z1, x2, y2, z2, depth+1, liquid);
				this.recursiveAddLiquidWithBounds(world, x0, y0, z0, x, y, z+1, x1, y1, z1, x2, y2, z2, depth+1, liquid);
				this.recursiveAddLiquidWithBounds(world, x0, y0, z0, x, y, z-1, x1, y1, z1, x2, y2, z2, depth+1, liquid);
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	/** Like the ordinary recursive add but with a spherical bounded volume. Args: World, x, y, z,
	 * id to replace, origin x,y,z, max radius */
	private void recursiveAddWithinSphere(IBlockAccess world, int x0, int y0, int z0, int x, int y, int z, Block id, int dx, int dy, int dz, double r, int depth) {
		if (overflow)
			return;
		if (depth > maxDepth)
			return;
		if (taxiCabDistance && Math.abs(x-x0)+Math.abs(y-y0)+Math.abs(z-z0) > maxDepth)
			return;
		if (world.getBlock(x, y, z) != id)
			return;
		if (this.hasBlock(x, y, z))
			return;
		if (ReikaMathLibrary.py3d(x-x0, y-y0, z-z0) > r)
			return;
		this.addBlockCoordinate(x, y, z);
		try {
			if (extraSpread) {
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						for (int k = -1; k <= 1; k++)
							this.recursiveAddWithinSphere(world, x0, y0, z0, x+i, y+j, z+k, id, x0, y0, z0, r, depth+1);
			}
			else {
				this.recursiveAddWithinSphere(world, x0, y0, z0, x+1, y, z, id, dx, dy, dz, r, depth+1);
				this.recursiveAddWithinSphere(world, x0, y0, z0, x-1, y, z, id, dx, dy, dz, r, depth+1);
				this.recursiveAddWithinSphere(world, x0, y0, z0, x, y+1, z, id, dx, dy, dz, r, depth+1);
				this.recursiveAddWithinSphere(world, x0, y0, z0, x, y-1, z, id, dx, dy, dz, r, depth+1);
				this.recursiveAddWithinSphere(world, x0, y0, z0, x, y, z+1, id, dx, dy, dz, r, depth+1);
				this.recursiveAddWithinSphere(world, x0, y0, z0, x, y, z-1, id, dx, dy, dz, r, depth+1);
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		if (this.isEmpty())
			return "Empty[]";
		StringBuilder list = new StringBuilder();
		list.append(this.getSize()+": ");
		for (int i = 0; i < this.getSize(); i++) {
			Coordinate c = this.getNthBlock(i);
			if (refWorld != null) {
				Block id = c.getBlock(refWorld);
				int meta = c.getBlockMetadata(refWorld);
				list.append(id+":"+meta+" @ ");
			}
			list.append(c.toString());
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
			this.recursiveAddWithinSphere(world, x, y, z, x+1, y, z, id, x, y, z, r, 0);
			this.recursiveAddWithinSphere(world, x, y, z, x, y+1, z, id, x, y, z, r, 0);
			this.recursiveAddWithinSphere(world, x, y, z, x, y, z+1, id, x, y, z, r, 0);
			this.recursiveAddWithinSphere(world, x, y, z, x-1, y, z, id, x, y, z, r, 0);
			this.recursiveAddWithinSphere(world, x, y, z, x, y-1, z, id, x, y, z, r, 0);
			this.recursiveAddWithinSphere(world, x, y, z, x, y, z-1, id, x, y, z, r, 0);
		}
		catch (StackOverflowError e) {
			this.throwOverflow(0);
			e.printStackTrace();
		}
	}

	protected void throwOverflow(int depth) {
		overflow = true;
		DragonAPICore.logError("Stack overflow at depth "+depth+"/"+maxDepth+"!");
	}

	public Coordinate getRandomBlock() {
		return this.isEmpty() ? null : this.getNthBlock(rand.nextInt(this.getSize()));
	}

	private void remove(int index) {
		Coordinate c = blocks.remove(index);
		keys.remove(c);
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
		Collection<Coordinate> temp = new ArrayList(blocks);
		keys.clear();
		blocks.clear();
		for (Coordinate c : temp) {
			Coordinate c2 = c.offset(x, y, z);
			blocks.add(c2);
			keys.add(c2);
		}

		/*
		minX += x;
		maxX += x;
		minY += y;
		maxY += y;
		minZ += z;
		maxZ += z;
		 */
		this.resetLimits();

		return this;
	}

	public final int sink(World world) {
		boolean canSink = true;
		int n = 0;
		while (canSink) {
			for (int i = 0; i < blocks.size(); i++) {
				Coordinate c = this.getNthBlock(i);
				int x = c.xCoord;
				int y = c.yCoord;
				int z = c.zCoord;
				if (!ReikaWorldHelper.softBlocks(world, x, y-1, z)) {
					canSink = false;
				}
			}
			if (canSink) {
				this.offset(0, -1, 0);
				n++;
			}
		}
		return n;
	}

	public final int sink(World world, Blocks... overrides) {
		boolean canSink = true;
		int n = 0;
		while (canSink) {
			for (int i = 0; i < blocks.size(); i++) {
				Coordinate c = this.getNthBlock(i);
				int x = c.xCoord;
				int y = c.yCoord;
				int z = c.zCoord;
				Block idy = world.getBlock(x, y-1, z);
				if (!ReikaWorldHelper.softBlocks(world, x, y-1, z) && !ReikaArrayHelper.contains(overrides, idy)) {
					canSink = false;
				}
			}
			if (canSink) {
				this.offset(0, -1, 0);
				n++;
			}
		}
		return n;
	}

	public final int sink(World world, Material... overrides) {
		boolean canSink = true;
		int n = 0;
		while (canSink) {
			for (int i = 0; i < blocks.size(); i++) {
				Coordinate c = this.getNthBlock(i);
				int x = c.xCoord;
				int y = c.yCoord;
				int z = c.zCoord;
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
			if (canSink) {
				this.offset(0, -1, 0);
				n++;
			}
		}
		return n;
	}

	/** Pre-collates them into forced stacks to help with FPS. Will not attempt to stack NBT-sensitive items. */
	public final ArrayList<ItemStack> getAllDroppedItems(World world, int fortune, EntityPlayer ep) {
		ArrayList<ItemStack> li = new ArrayList();
		ArrayList<ItemStack> nbt = new ArrayList();
		ItemHashMap<Integer> map = new ItemHashMap().enableNBT();
		for (int i = 0; i < blocks.size(); i++) {
			Coordinate c = this.getNthBlock(i);
			int x = c.xCoord;
			int y = c.yCoord;
			int z = c.zCoord;
			Block b = world.getBlock(x, y, z);
			if (b != null && b != Blocks.air) {
				int metadata = world.getBlockMetadata(x, y, z);
				ArrayList<ItemStack> drop = b.getDrops(world, x, y, z, metadata, fortune);
				HarvestDropsEvent evt = new HarvestDropsEvent(x, y, z, world, b, metadata, fortune, 1F, drop, ep, false);
				MinecraftForge.EVENT_BUS.post(evt);
				drop = evt.drops;
				for (ItemStack is : drop) {
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

	public final BlockArray copy() {
		BlockArray copy = this.instantiate();
		this.copyTo(copy);
		return copy;
	}

	protected BlockArray instantiate() {
		return new BlockArray();
	}

	public void copyTo(BlockArray copy) {
		copy.refWorld = refWorld;
		copy.overflow = overflow;
		copy.blocks.clear();
		copy.blocks.addAll(blocks);
		copy.keys.clear();
		copy.keys.addAll(keys);
		copy.recalcLimits();
		/*
		copy.minX = minX;
		copy.minY = minY;
		copy.minZ = minZ;
		copy.maxX = maxX;
		copy.maxY = maxY;
		copy.maxZ = maxZ;*/
	}

	public void addAll(BlockArray arr) {
		for (Coordinate c : arr.blocks) {
			if (!keys.contains(c)) {
				this.addBlockCoordinate(c.xCoord, c.yCoord, c.zCoord);
			}
		}
	}

	public void addAll(BlockBox box) {
		for (int x = box.minX; x <= box.maxX; x++) {
			for (int z = box.minZ; z <= box.maxZ; z++) {
				for (int y = box.minY; y <= box.maxY; y++) {
					this.addBlockCoordinate(x, y, z);
				}
			}
		}
	}

	public final boolean isAtLeastXPercentNot(World world, double percent, Block id, int meta) {
		double s = this.getSize();
		int ct = 0;
		for (int i = 0; i < this.getSize(); i++) {
			Coordinate c = this.getNthBlock(i);
			int x = c.xCoord;
			int y = c.yCoord;
			int z = c.zCoord;
			Block id2 = world.getBlock(x, y, z);
			int meta2 = world.getBlockMetadata(x, y, z);
			if (id2 != id || meta2 != meta) {
				ct++;
			}
		}
		return ct/s*100D >= percent;
	}

	public final boolean isAtLeastXPercent(World world, double percent, Block id) {
		return this.isAtLeastXPercent(world, percent, id, -1);
	}

	public final boolean isAtLeastXPercent(World world, double percent, Block id, int meta) {
		double s = this.getSize();
		int ct = 0;
		for (int i = 0; i < this.getSize(); i++) {
			Coordinate c = this.getNthBlock(i);
			int x = c.xCoord;
			int y = c.yCoord;
			int z = c.zCoord;
			Block id2 = world.getBlock(x, y, z);
			int meta2 = world.getBlockMetadata(x, y, z);
			if (id2 == id && (meta == -1 || meta2 == meta)) {
				ct++;
			}
		}
		return ct/s*100D >= percent;
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
				Coordinate c = this.getNthBlock(i);
				c.setBlock(refWorld, b, meta);
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
			Coordinate c = this.getNthBlock(i);
			tag.setInteger("x", c.xCoord);
			tag.setInteger("y", c.yCoord);
			tag.setInteger("z", c.zCoord);
			li.appendTag(tag);
		}
		NBT.setTag(label, li);
		NBTTagCompound limit = new NBTTagCompound();
		limit.setInteger("minx", minX);
		limit.setInteger("miny", minY);
		limit.setInteger("minz", minZ);
		limit.setInteger("maxx", maxX);
		limit.setInteger("maxy", maxY);
		limit.setInteger("maxz", maxZ);
		NBT.setTag(label+"_lim", limit);
	}

	public void readFromNBT(String label, NBTTagCompound NBT) {
		this.clear();
		NBTTagList tag = NBT.getTagList(label, NBTTypes.COMPOUND.ID);
		if (tag == null || tag.tagCount() == 0)
			return;
		for (int i = 0; i < tag.tagCount(); i++) {
			NBTTagCompound coord = tag.getCompoundTagAt(i);
			int x = coord.getInteger("x");
			int y = coord.getInteger("y");
			int z = coord.getInteger("z");
			this.addBlockCoordinate(x, y, z);
		}
		NBTTagCompound limit = NBT.getCompoundTag(label+"_lim");
		minX = limit.getInteger("minx");
		minY = limit.getInteger("miny");
		minZ = limit.getInteger("minz");
		maxX = limit.getInteger("maxx");
		maxY = limit.getInteger("maxy");
		maxZ = limit.getInteger("maxz");
	}

	public void shaveToCube() {
		if (this.isEmpty())
			return;
		boolean changed = false;
		do {
			int s1 = this.getSize();

			Collection<Coordinate> set = new HashSet(blocks);
			for (Coordinate c : set) {
				int n = this.countNeighbors(c);
				if (n < 11) {
					this.removeKey(c);
				}
			}

			changed = this.getSize() != s1;
		} while(changed);

		this.resetLimits();
	}

	private int countNeighbors(Coordinate c) {
		int n = 0;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (keys.contains(c.offset(i, j, k)))
						n++;
				}
			}
		}
		return n;
	}

	public void XORWith(BlockArray b) {
		HashSet<Coordinate> set = new HashSet();
		set.addAll(blocks);
		set.addAll(b.blocks);
		this.clear();
		for (Coordinate c : set) {
			if (keys.contains(c) ^ b.keys.contains(c)) {
				this.addKey(c);
			}
		}
	}

	public static BlockArray getXORBox(BlockArray b1, BlockArray b2) {
		BlockArray b = b1.instantiate();
		HashSet<Coordinate> set = new HashSet();
		set.addAll(b1.blocks);
		set.addAll(b2.blocks);
		for (Coordinate c : set) {
			if (b2.keys.contains(c) ^ b1.keys.contains(c)) {
				b.addKey(c);
			}
		}
		return b;
	}

	public void intersectWith(BlockArray b) {
		Iterator<Coordinate> it = blocks.iterator();
		while (it.hasNext()) {
			Coordinate c = it.next();
			if (!b.keys.contains(c)) {
				it.remove();
				keys.remove(c);
			}
		}
		this.resetLimits();
	}

	public static BlockArray getIntersectedBox(BlockArray b1, BlockArray b2) {
		BlockArray b = b1.instantiate();
		for (Coordinate c : b1.blocks) {
			if (b2.keys.contains(c)) {
				b.addKey(c);
			}
		}
		return b;
	}

	public void unifyWith(BlockArray b) {
		for (Coordinate c : b.blocks) {
			this.addKey(c);
		}
		this.resetLimits();
	}

	public static BlockArray getUnifiedBox(BlockArray b1, BlockArray b2) {
		BlockArray b = b1.instantiate();
		for (Coordinate c : b1.blocks) {
			b.addKey(c);
		}
		for (Coordinate c : b2.blocks) {
			b.addKey(c);
		}
		return b;
	}

	public final AxisAlignedBB asAABB() {
		return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX+1, maxY+1, maxZ+1);
	}

	public final BlockBox asBlockBox() {
		return new BlockBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public void reverseBlockOrder() {
		Collections.reverse(blocks);
	}

	public void sortBlocksByHeight(boolean reverse) {
		this.sort(reverse ? heightComparator2 : heightComparator);
	}

	public void sortBlocksByDistance(Coordinate loc) {
		this.sort(new InwardsComparator(loc));
	}

	public void sort(Comparator<Coordinate> comparator) {
		Collections.sort(blocks, comparator);
	}

	public BlockArray rotate90Degrees(int ox, int oz, boolean left) {
		BlockArray b = this.instantiate();
		for (Coordinate c : blocks) {
			Coordinate c2 = c.rotate90About(ox, oz, left);
			b.addBlockCoordinate(c2.xCoord, c2.yCoord, c2.zCoord);
		}
		return b;
	}

	public BlockArray rotate180Degrees(int ox, int oz) {
		BlockArray b = this.instantiate();
		for (Coordinate c : blocks) {
			Coordinate c2 = c.rotate180About(ox, oz);
			b.addBlockCoordinate(c2.xCoord, c2.yCoord, c2.zCoord);
		}
		return b;
	}

	public BlockArray flipX() {
		BlockArray b = this.instantiate();
		for (Coordinate c : blocks) {
			Coordinate c2 = new Coordinate(-c.xCoord, c.yCoord, c.zCoord);
			b.addBlockCoordinate(c2.xCoord, c2.yCoord, c2.zCoord);
		}
		return b;
	}

	public BlockArray flipZ() {
		BlockArray b = this.instantiate();
		for (Coordinate c : blocks) {
			Coordinate c2 = new Coordinate(c.xCoord, c.yCoord, -c.zCoord);
			b.addBlockCoordinate(c2.xCoord, c2.yCoord, c2.zCoord);
		}
		return b;
	}

	public static BlockArray fromBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		BlockArray b = new BlockArray();
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					b.addBlockCoordinate(x, y, z);
				}
			}
		}
		return b;
	}

	public void expand(int amt, boolean rounded) {
		HashSet<Coordinate> set = new HashSet();
		for (Coordinate c : blocks) {
			if (rounded) {
				for (int i = -amt; i <= amt; i++) {
					for (int j = -amt; j <= amt; j++) {
						for (int k = -amt; k <= amt; k++) {
							Coordinate c2 = c.offset(i, j, k);
							if (!keys.contains(c2)) {
								set.add(c2);
							}
						}
					}
				}
			}
			else {
				for (int i = 0; i < 6; i++) {
					for (int k = 1; k <= amt; k++) {
						Coordinate c2 = c.offset(ForgeDirection.VALID_DIRECTIONS[i], k);
						if (!keys.contains(c2)) {
							set.add(c2);
						}
					}
				}
			}
		}
		for (Coordinate c : set) {
			this.addKey(c);
		}
	}

	public Collection<BlockArray> splitToRectangles() {
		ArrayList<BlockArray> li = new ArrayList();
		HashSet<Coordinate> locs = new HashSet(keys);
		while (!locs.isEmpty()) {
			ArrayList<Coordinate> locList = new ArrayList(locs);
			int idx = rand.nextInt(locs.size());
			Coordinate c = locList.remove(idx);
			locs.remove(c);
			ArrayList<Coordinate> block = new ArrayList();
			block.add(c);
			ArrayList<ForgeDirection> dirs = ReikaDirectionHelper.getRandomOrderedDirections(true);
			while (!dirs.isEmpty()) {
				ForgeDirection dir = dirs.remove(0);
				int d = 1;
				boolean flag = true;
				while (flag) {
					ArrayList<Coordinate> add = new ArrayList();
					for (Coordinate in : block) {
						Coordinate offset = in.offset(dir, d);
						if (!block.contains(offset)) {
							if (!locs.contains(offset)) {
								//ReikaJavaLibrary.pConsole("Failed to expand "+block+" "+dir+" due to bounds @ "+offset);
								flag = false;
								break;
							}
							else {
								add.add(offset);
							}
						}
					}
					if (flag) {
						//ReikaJavaLibrary.pConsole("Adding "+add+" to "+block);
						for (Coordinate in : add) {
							block.add(in);
							locs.remove(in);
						}
						//d++;
					}
				}
			}
			li.add(new BlockArray(block));
		}
		return li;
	}

	private static final Comparator<Coordinate> heightComparator = new HeightComparator(false);
	private static final Comparator<Coordinate> heightComparator2 = new HeightComparator(true);

	private static class HeightComparator implements Comparator<Coordinate> {

		private final boolean reverse;

		private HeightComparator(boolean rev) {
			reverse = rev;
		}

		@Override
		public int compare(Coordinate o1, Coordinate o2) {
			return reverse ? o2.yCoord - o1.yCoord : o1.yCoord - o2.yCoord;
		}

	}

	private class InwardsComparator implements Comparator<Coordinate> {

		private final Coordinate location;

		private InwardsComparator(Coordinate c) {
			location = c;
		}

		@Override
		public int compare(Coordinate o1, Coordinate o2) {
			return (int)Math.signum(o2.getDistanceTo(location)-o1.getDistanceTo(location));
		}

	}

	public static abstract class BlockTypePrioritizer implements Comparator<Coordinate> {

		private final World world;

		protected BlockTypePrioritizer(World world) {
			this.world = world;
		}

		public final int compare(Coordinate c1, Coordinate c2) {
			BlockKey b1 = c1.getBlockKey(world);
			BlockKey b2 = c2.getBlockKey(world);
			return this.compare(b1, b2);
		}

		protected abstract int compare(BlockKey b1, BlockKey b2);

	}

	@Override
	public Iterator<Coordinate> iterator() {
		return new BlockArrayIterator();
	}

	private final class BlockArrayIterator implements Iterator<Coordinate> {

		private int index;

		private BlockArrayIterator() {

		}

		@Override
		public boolean hasNext() {
			return blocks.size() > index+1;
		}

		@Override
		public Coordinate next() {
			Coordinate c = blocks.get(index);
			index++;
			return c;
		}

		@Override
		public void remove() {
			BlockArray.this.remove(index);
		}

	}
}

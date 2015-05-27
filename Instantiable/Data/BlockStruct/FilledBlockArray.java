/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Interfaces.BlockCheck;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class FilledBlockArray extends StructuredBlockArray {

	private final HashMap<Coordinate, BlockCheck> data = new HashMap();

	public FilledBlockArray(World world) {
		super(world);
	}

	public void loadBlock(int x, int y, int z) {
		this.setBlock(x, y, z, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
	}

	public void loadBlockTo(int x, int y, int z, int xt, int yt, int zt) {
		this.setBlock(xt, yt, zt, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
	}

	public void setBlock(int x, int y, int z, Block id) {
		this.setBlock(x, y, z , new BlockKey(id));
	}

	public void setBlock(int x, int y, int z, Block id, int meta) {
		this.setBlock(x, y, z , new BlockKey(id, meta));
	}

	public void setFluid(int x, int y, int z, Fluid f) {
		super.addBlockCoordinate(x, y, z);
		data.put(new Coordinate(x, y, z), new FluidCheck(f));
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

	private void addBlock(int x, int y, int z, BlockKey bk) {
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
		this.placeExcept(null);
	}

	public void placeExcept(Coordinate e) {
		for (Coordinate c : data.keySet()) {
			//Block b = this.getBlock(x, y, z);
			//int meta = this.getBlockMetadata(x, y, z);
			//world.setBlock(x, y, z, b, meta, 3);
			if (!c.equals(e))
				data.get(c).place(world, c.xCoord, c.yCoord, c.zCoord);
		}
	}

	public ItemStack getDisplayAt(int x, int y, int z) {
		BlockCheck bk = this.getBlockKey(x, y, z);
		return bk != null ? bk.getDisplay() : null;
	}

	public boolean hasBlockAt(int x, int y, int z, Block b) {
		return this.hasBlockAt(x, y, z, b, -1);
	}

	public boolean hasBlockAt(int x, int y, int z, Block b, int meta) {
		BlockCheck bc = this.getBlockKey(x, y, z);
		return bc != null ? bc.match(b, meta) : false;
	}

	public boolean matchInWorld() {
		for (Coordinate c : data.keySet()) {
			int x = c.xCoord;
			int y = c.yCoord;
			int z = c.zCoord;
			BlockCheck bk = this.getBlockKey(x, y, z);
			//ReikaJavaLibrary.pConsole(x+","+y+","+z+" > "+bk+" & "+world.getBlock(x, y, z)+":"+world.getBlockMetadata(x, y, z));
			if (!world.isRemote && !bk.matchInWorld(world, x, y, z)) {
				//ReikaJavaLibrary.pConsole(x+","+y+","+z+" > "+bk.getClass()+":"+bk+" & "+world.getBlock(x, y, z)+":"+world.getBlockMetadata(x, y, z));
				//bk.place(world, x, y, z);
				//world.setBlock(x, y, z, Blocks.brick_block);
				return false;
			}
		}
		return true;
	}

	public ItemHashMap<Integer> tally() {
		ItemHashMap<Integer> map = new ItemHashMap();
		for (BlockCheck bc : data.values()) {
			ItemStack key = bc.asItemStack();
			if (this.count(key)) {
				Integer get = map.get(key);
				int has = get != null ? get.intValue() : 0;
				map.put(key, has+1);
			}
		}
		return map;
	}

	private boolean count(ItemStack is) {
		if (is == null)
			return false;
		Item it = is.getItem();
		if (it == null)
			return false;
		if (it instanceof ItemBlock) {
			Block b = Block.getBlockFromItem(it);
			if (b instanceof BlockLiquid || b instanceof BlockFluidBase) {
				if (is.getItemDamage() > 0)
					return false;
			}
			if (b != null && b.getMaterial() == Material.air)
				return false;
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
			Coordinate c = this.getNthBlock(i);
			int x = c.xCoord;
			int y = c.yCoord;
			int z = c.zCoord;
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
		public boolean matchInWorld(World world, int x, int y, int z) {
			return this.match(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
		}

		@Override
		public boolean match(Block b, int meta) {
			return keys.contains(new BlockKey(b, meta));
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

		@Override
		public ItemStack asItemStack() {
			Block b = this.getBlock();
			return b != null && b != Blocks.air ? new ItemStack(b, 1, this.getMeta()) : null;
		}

		public ItemStack getDisplay() {
			return this.asItemStack();
		}

	}

	private static class FluidCheck implements BlockCheck {

		public final Fluid fluid;

		private FluidCheck(Fluid f) {
			if (!f.canBePlacedInWorld())
				throw new MisuseException("You cannot require non-placeable fluids!");
			fluid = f;
		}

		@Override
		public boolean matchInWorld(World world, int x, int y, int z) {
			return this.match(world.getBlock(x, y, z), 0);
		}

		@Override
		public boolean match(Block b, int meta) {
			return b instanceof BlockFluidBase && ((BlockFluidBase)b).getFluid() == fluid || FluidRegistry.lookupFluidForBlock(b) == fluid;
		}

		@Override
		public void place(World world, int x, int y, int z) {
			world.setBlock(x, y, z, this.getBlock());
		}

		private Block getBlock() {
			return fluid.getBlock();
		}

		@Override
		public ItemStack asItemStack() {
			ItemStack is = ReikaItemHelper.getContainerForFluid(fluid);
			return is != null ? is : new ItemStack(this.getBlock());
		}

		public ItemStack getDisplay() {
			return new ItemStack(this.getBlock());
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
		public boolean matchInWorld(World world, int x, int y, int z) {
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
		public boolean match(Block b, int meta) {
			if (exceptions.contains(b))
				return false;
			if (b == Blocks.air || b instanceof BlockAir)
				return true;
			if (allowSoft && ReikaWorldHelper.softBlocks(b))
				return true;
			if (allowNonSolid && b.getMaterial().blocksMovement())
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

		@Override
		public ItemStack asItemStack() {
			return null;
		}

		public ItemStack getDisplay() {
			return null;
		}

	}

	@Override
	public BlockArray copy() {
		FilledBlockArray copy = new FilledBlockArray(world);
		copy.refWorld = refWorld;
		copy.liquidMat = liquidMat;
		copy.overflow = overflow;
		copy.blocks.clear();
		copy.blocks.addAll(blocks);
		copy.recalcLimits();
		copy.data.putAll(data);
		return copy;
	}

}

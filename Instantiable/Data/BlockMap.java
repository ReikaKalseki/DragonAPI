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
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public final class BlockMap<V> {

	private final HashMap<BlockKey, V> data = new HashMap();

	public BlockMap() {

	}

	private V get(BlockKey bk) {
		//ReikaJavaLibrary.pConsole(bk+" >> "+data.keySet());
		return data.get(bk);
	}

	public V get(Block b, int meta) {
		return this.get(new BlockKey(b, meta));
	}

	public V get(Block b) {
		return this.get(new BlockKey(b, -1));
	}

	public V get(IBlockAccess world, int x, int y, int z) {
		return this.get(BlockKey.getAt(world, x, y, z));
	}

	private V put(BlockKey bk, V obj) {
		return data.put(bk, obj);
	}

	public V put(Block b, V obj) {
		return this.put(new BlockKey(b, -1), obj);
	}

	public V put(Block b, int meta, V obj) {
		return this.put(new BlockKey(b, meta), obj);
	}

	public V put(IBlockAccess world, int x, int y, int z, V obj) {
		return this.put(BlockKey.getAt(world, x, y, z), obj);
	}

	private boolean containsKey(BlockKey bk) {
		return data.containsKey(bk);
	}

	public boolean containsKey(Block b, int meta) {
		return this.containsKey(new BlockKey(b, meta));
	}

	public boolean containsKey(IBlockAccess world, int x, int y, int z) {
		return this.containsKey(BlockKey.getAt(world, x, y, z));
	}

	@Override
	public String toString() {
		return data.toString();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof BlockMap ? data.equals(((BlockMap)o).data) : false;
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	public ArrayList<V> getForBlock(Block b) {
		ArrayList<V> li = new ArrayList();
		for (BlockKey bk : data.keySet()) {
			if (bk.blockID == b) {
				li.add(this.get(bk));
			}
		}
		return li;
	}

	public ArrayList<V> getForMeta(int meta) {
		ArrayList<V> li = new ArrayList();
		for (BlockKey bk : data.keySet()) {
			if (bk.metadata == meta) {
				li.add(this.get(bk));
			}
		}
		return li;
	}

	public int size() {
		return data.size();
	}

	public Set<BlockKey> keySet() {
		return Collections.unmodifiableSet(this.data.keySet());
	}

	public static final class BlockKey implements BlockCheck {

		public final Block blockID;
		public final int metadata;

		public BlockKey(Block b) {
			this(b, -1);
		}

		public BlockKey(Block b, int meta) {
			metadata = meta;
			blockID = b;
		}

		public static BlockKey getAt(IBlockAccess world, int x, int y, int z) {
			return new BlockKey(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
		}

		public static BlockKey getAtNoMeta(IBlockAccess world, int x, int y, int z) {
			return new BlockKey(world.getBlock(x, y, z), -1);
		}

		@Override
		public int hashCode() {
			return blockID.hashCode() + metadata << 24;
		}

		@Override
		public boolean equals(Object o) {
			//ReikaJavaLibrary.pConsole(this+" & "+o);
			if (o instanceof BlockKey) {
				BlockKey b = (BlockKey)o;
				return b.blockID == blockID && (!this.hasMetadata() || !b.hasMetadata() || b.metadata == metadata);
			}
			return false;
		}

		@Override
		public String toString() {
			return blockID+":"+metadata;
		}

		public boolean hasMetadata() {
			return metadata >= 0 && metadata != OreDictionary.WILDCARD_VALUE;
		}

		public ItemStack asItemStack() {
			return new ItemStack(blockID, 1, metadata);
		}

		public boolean match(Block b, int meta) {
			return b == this.blockID && (!this.hasMetadata() || meta == this.metadata);
		}

		public boolean match(World world, int x, int y, int z) {
			return this.match(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
		}

		@Override
		public void place(World world, int x, int y, int z) {
			world.setBlock(x, y, z, blockID, this.hasMetadata() ? this.metadata : 0, 3);
		}

	}

	public static interface BlockCheck {

		public boolean match(Block b, int meta);
		public void place(World world, int x, int y, int z);
	}

}

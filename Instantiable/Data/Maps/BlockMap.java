/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;

public final class BlockMap<V> {

	private final HashMap<BlockKey, V> data = new HashMap();

	public BlockMap() {

	}

	public V get(BlockKey bk) {
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

	public V put(BlockKey bk, V obj) {
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

	public void clear() {
		data.clear();
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

	public Collection<V> values() {
		return Collections.unmodifiableCollection(data.values());
	}

}

package Reika.DragonAPI.Instantiable.Data;

import Reika.DragonAPI.Instantiable.Data.BlockMap.BlockKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class FilledBlockArray extends StructuredBlockArray {

	private HashMap<List<Integer>, BlockKey> data = new HashMap();

	public FilledBlockArray(World world) {
		super(world);
	}

	public void setBlock(int x, int y, int z, Block id) {
		this.setBlock(x, y, z , new BlockKey(id));
	}

	public void setBlock(int x, int y, int z, Block id, int meta) {
		this.setBlock(x, y, z , new BlockKey(id, meta));
	}

	public void setBlock(int x, int y, int z, BlockKey bk) {
		super.addBlockCoordinate(x, y, z);
		data.put(Arrays.asList(x, y, z), bk);
	}

	public BlockKey getBlockKey(int x, int y, int z) {
		return data.get(Arrays.asList(x, y, z));
	}

	public Block getBlock(int x, int y, int z) {
		return this.getBlockKey(x, y, z).blockID;
	}

	public int getBlockMetadata(int x, int y, int z) {
		return Math.max(0, this.getBlockKey(x, y, z).metadata);
	}

	public void place() {
		for (List<Integer> c : data.keySet()) {
			int x = c.get(0);
			int y = c.get(1);
			int z = c.get(2);
			Block b = this.getBlock(x, y, z);
			int meta = this.getBlockMetadata(x, y, z);
			world.setBlock(x, y, z, b, meta, 3);
		}
	}

	public boolean matchInWorld() {
		for (List<Integer> c : data.keySet()) {
			int x = c.get(0);
			int y = c.get(1);
			int z = c.get(2);
			BlockKey bk = this.getBlockKey(x, y, z);
			Block b = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			if (!bk.match(b, meta))
				return false;
		}
		return true;
	}

}

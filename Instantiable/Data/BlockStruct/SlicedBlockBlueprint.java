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
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray.BlockMatchFailCallback;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray.EmptyCheck;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class SlicedBlockBlueprint {

	private int width;
	private int ySize;

	private final ArrayList<Block[][]> IDs = new ArrayList();
	private final ArrayList<int[][]> metadatas = new ArrayList();

	private final ArrayList<Block[][]> antiIDs = new ArrayList();
	private final ArrayList<int[][]> antiMetadatas = new ArrayList();

	private final HashMap<Character, BlockKey> mappings = new HashMap();
	private final HashMap<Character, BlockKey> antiMappings = new HashMap();

	public void addMapping(char c, Block id) {
		this.addMapping(c, id, -1);
	}

	public void addMapping(char c, Block id, int meta) {
		this.verifyArg(c);
		mappings.put(c, new BlockKey(id, meta));
	}

	public void addAntiMapping(char c, Block id) {
		this.addAntiMapping(c, id, -1);
	}

	public void addAntiMapping(char c, Block id, int meta) {
		this.verifyArg(c);
		antiMappings.put(c, new BlockKey(id, meta));
	}

	private void verifyArg(char c) {
		if (c == 'x')
			throw new MisuseException("Character 'x' is reserved for \"don't care\"!");
		if (c == '-')
			throw new MisuseException("Character '-' is reserved for empty space!");
	}

	public void addSlice(String... array) {
		int l = array.length;
		for (int i = 0; i < l; i++) {
			if (i > 0)
				if (array[i].length() != array[i-1].length())
					throw new MisuseException("You must only register properly shaped slices!");
		}
		int w = array[0].length();

		if (l > ySize)
			ySize = l;
		if (w > width)
			width = w;

		Block[][] ids = new Block[l][w];
		int[][] metas = new int[l][w];

		Block[][] antiids = new Block[l][w];
		int[][] antimetas = new int[l][w];

		for (int i = 0; i < l; i++) {
			String s = array[i];
			char[] cs = s.toCharArray();
			for (int k = 0; k < cs.length; k++) { //cs.length == w
				char c = cs[k];
				if (c == '-') {
					ids[i][k] = Blocks.air;
					metas[i][k] = 0;
					antiids[i][k] = null;
					antimetas[i][k] = -1;
				}
				else if (c == 'x') {
					ids[i][k] = null;
					metas[i][k] = -1;
					antiids[i][k] = null;
					antimetas[i][k] = -1;
				}
				else {
					if (!this.mapBlock(c, ids, metas, antiids, antimetas, i, k)) {
						if (!this.antimapBlock(c, ids, metas, antiids, antimetas, i, k)) {
							throw new MisuseException("Unspecified mapping '"+c+"'!");
						}
					}
				}
			}
		}
		IDs.add(ids);
		metadatas.add(metas);
		antiIDs.add(antiids);
		antiMetadatas.add(antimetas);
	}

	private boolean mapBlock(char c, Block[][] ids, int[][] metas, Block[][] antiids, int[][] antimetas, int i, int k) {
		BlockKey block = mappings.get(c);
		if (block == null)
			return false;
		Block id = block.blockID;
		int meta = block.metadata;
		ids[i][k] = id;
		metas[i][k] = meta;
		//ReikaJavaLibrary.pConsole(c+" Maps>> "+id+":"+meta);
		return true;
	}

	private boolean antimapBlock(char c, Block[][] ids, int[][] metas, Block[][] antiids, int[][] antimetas, int i, int k) {
		BlockKey block = antiMappings.get(c);
		if (block == null)
			return false;
		Block id = block.blockID;
		int meta = block.metadata;
		antiids[i][k] = id;
		antimetas[i][k] = meta;
		ids[i][k] = null;
		metas[i][k] = -1;
		//ReikaJavaLibrary.pConsole(c+" Antimaps>> "+id+":"+meta);
		return true;
	}

	public int getLength() {
		return IDs.size();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return ySize;
	}

	public int getWidth(int slice) {
		return IDs.get(slice).length;
	}

	public int getHeight(int slice) {
		return IDs.get(slice)[0].length;
	}

	public String getString(int slice) {
		StringBuilder sb = new StringBuilder();
		Block[][] ids = IDs.get(slice);
		int[][] metas = metadatas.get(slice);
		Block[][] antiids = antiIDs.get(slice);
		int[][] antimetas = antiMetadatas.get(slice);

		for (int k = 0; k < ids.length; k++) {
			sb.append("[ ");
			for (int m = 0; m < ids[k].length; m++) {
				Block id = ids[k][m];
				int meta = metas[k][m];
				Block antiid = antiids[k][m];
				int antimeta = antimetas[k][m];
				sb.append(id+":"+meta);
				//sb.append(" X ");
				//sb.append(antiid+":"+antimeta);
				sb.append("\t");
			}
			sb.append(" ]\n");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Size "+this.getLength()+"x"+this.getWidth()+"x"+this.getHeight()+":\n");
		for (int i = 0; i < IDs.size(); i++) {
			sb.append("{\n");
			sb.append(this.getString(i));
			sb.append("}\n");
		}
		return sb.toString();
	}

	public boolean checkAgainst(World world, int x, int y, int z, int xref, int yref, ForgeDirection plane, int slice, BlockMatchFailCallback call) {
		//ReikaJavaLibrary.pConsole(slice+": "+this.getString(slice));
		Block[][] ids = IDs.get(slice);
		int[][] metas = metadatas.get(slice);
		Block[][] antiids = antiIDs.get(slice);
		int[][] antimetas = antiMetadatas.get(slice);
		for (int i = 0; i < ids.length; i++) {
			for (int k = 0; k < ids[i].length; k++) {
				int dx = plane.offsetX == 0 ? x-xref+i : x;
				int dz = plane.offsetZ == 0 ? z-xref+i : z;
				int dy = y+yref-k;
				Block id = ids[k][i];
				int meta = metas[k][i];
				Block id2 = world.getBlock(dx, dy, dz);
				int meta2 = world.getBlockMetadata(dx, dy, dz);
				if (id == null) {
					id = antiids[k][i];
					meta = antimetas[k][i];
					if (id != null) {
						if (id == id2) {
							//ReikaJavaLibrary.pConsole(slice+" w aID: "+id+"&"+id2+" @ "+i+", "+k+" >> "+dx+","+dy+","+dz);
							if (call != null)
								call.onBlockFailure(world, dx, dy, dz, new EmptyCheck(false, false));
							return false;
						}
						if (meta != -1) {
							if (meta == meta2) {
								//ReikaJavaLibrary.pConsole(slice+" w ameta: "+meta+"%"+meta2+" @ "+i+", "+k+" >> "+dx+","+dy+","+dz);
								if (call != null)
									call.onBlockFailure(world, dx, dy, dz, new EmptyCheck(false, false));
								return false;
							}
						}
					}
				}
				else {
					if (id != id2) {
						//ReikaJavaLibrary.pConsole(slice+" w ID: "+id+"&"+id2+" @ "+i+", "+k+" >> "+dx+","+dy+","+dz);
						//world.setBlock(dx, dy, dz, 49);
						if (call != null)
							call.onBlockFailure(world, dx, dy, dz, new BlockKey(id, meta));
						return false;
					}
					if (meta != -1) {
						if (meta != meta2) {
							//ReikaJavaLibrary.pConsole(slice+" w meta: "+meta+"%"+meta2+" @ "+i+", "+k+" >> "+dx+","+dy+","+dz);
							//world.setBlock(dx, dy, dz, 49);
							if (call != null)
								call.onBlockFailure(world, dx, dy, dz, new BlockKey(id, meta));
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public SlicedBlockBlueprint copy() {
		SlicedBlockBlueprint cp = new SlicedBlockBlueprint();
		cp.width = width;
		cp.ySize = ySize;
		cp.IDs.addAll(IDs);
		cp.metadatas.addAll(metadatas);
		cp.mappings.putAll(mappings);
		cp.antiIDs.addAll(antiIDs);
		cp.antiMetadatas.addAll(antiMetadatas);
		cp.antiMappings.putAll(antiMappings);
		return cp;
	}

	public void putInto(FilledBlockArray array, int x, int y, int z, ForgeDirection dir) {
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		for (int slice = 0; slice < IDs.size(); slice++) {
			int dx = x+dir.offsetX*slice;
			Block[][] ids = IDs.get(slice);
			int[][] metas = metadatas.get(slice);

			for (int k = 0; k < ids.length; k++) {
				int dz = z+left.offsetZ*k;
				for (int m = 0; m < ids[k].length; m++) {
					int dy = y+m;
					Block id = ids[k][m];
					if (id != null) { //"don't care"
						int meta = metas[k][m];
						array.setBlock(dx, dy, dz, id, meta);
					}
				}
			}
		}
	}
}

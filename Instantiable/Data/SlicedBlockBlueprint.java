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
import java.util.HashMap;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Exception.MisuseException;

public class SlicedBlockBlueprint {

	private int width;
	private int ySize;

	private final ArrayList<int[][]> IDs = new ArrayList();
	private final ArrayList<int[][]> metadatas = new ArrayList();

	private final ArrayList<int[][]> antiIDs = new ArrayList();
	private final ArrayList<int[][]> antiMetadatas = new ArrayList();

	private final HashMap<Character, List<Integer>> mappings = new HashMap();
	private final HashMap<Character, List<Integer>> antiMappings = new HashMap();

	public void addMapping(char c, int id) {
		this.addMapping(c, id, -1);
	}

	public void addMapping(char c, int id, int meta) {
		this.verifyArg(c);
		mappings.put(c, Arrays.asList(id, meta));
	}

	public void addAntiMapping(char c, int id) {
		this.addAntiMapping(c, id, -1);
	}

	public void addAntiMapping(char c, int id, int meta) {
		this.verifyArg(c);
		antiMappings.put(c, Arrays.asList(id, meta));
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

		int[][] ids = new int[l][w];
		int[][] metas = new int[l][w];

		int[][] antiids = new int[l][w];
		int[][] antimetas = new int[l][w];

		for (int i = 0; i < l; i++) {
			String s = array[i];
			char[] cs = s.toCharArray();
			for (int k = 0; k < cs.length; k++) { //cs.length == w
				char c = cs[k];
				if (c == '-') {
					ids[i][k] = 0;
					metas[i][k] = 0;
					antiids[i][k] = -1;
					antimetas[i][k] = -1;
				}
				else if (c == 'x') {
					ids[i][k] = -1;
					metas[i][k] = -1;
					antiids[i][k] = -1;
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

	private boolean mapBlock(char c, int[][] ids, int[][] metas, int[][] antiids, int[][] antimetas, int i, int k) {
		List<Integer> block = mappings.get(c);
		if (block == null)
			return false;
		int id = block.get(0);
		int meta = block.get(1);
		ids[i][k] = id;
		metas[i][k] = meta;
		//ReikaJavaLibrary.pConsole(c+" Maps>> "+id+":"+meta);
		return true;
	}

	private boolean antimapBlock(char c, int[][] ids, int[][] metas, int[][] antiids, int[][] antimetas, int i, int k) {
		List<Integer> block = antiMappings.get(c);
		if (block == null)
			return false;
		int id = block.get(0);
		int meta = block.get(1);
		antiids[i][k] = id;
		antimetas[i][k] = meta;
		ids[i][k] = -1;
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
		int[][] ids = IDs.get(slice);
		int[][] metas = metadatas.get(slice);
		int[][] antiids = antiIDs.get(slice);
		int[][] antimetas = antiMetadatas.get(slice);

		for (int k = 0; k < ids.length; k++) {
			sb.append("[ ");
			for (int m = 0; m < ids[k].length; m++) {
				int id = ids[k][m];
				int meta = metas[k][m];
				int antiid = antiids[k][m];
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

	public boolean checkAgainst(World world, int x, int y, int z, int xref, int yref, ForgeDirection plane, int slice) {
		//ReikaJavaLibrary.pConsole(slice+": "+this.getString(slice));
		int[][] ids = IDs.get(slice);
		int[][] metas = metadatas.get(slice);
		int[][] antiids = antiIDs.get(slice);
		int[][] antimetas = antiMetadatas.get(slice);
		for (int i = 0; i < ids.length; i++) {
			for (int k = 0; k < ids[i].length; k++) {
				int dx = plane.offsetX == 0 ? x-xref+i : x;
				int dz = plane.offsetZ == 0 ? z-xref+i : z;
				int dy = y+yref-k;
				int id = ids[k][i];
				int meta = metas[k][i];
				int id2 = world.getBlockId(dx, dy, dz);
				int meta2 = world.getBlockMetadata(dx, dy, dz);
				if (id == -1) {
					id = antiids[k][i];
					meta = antimetas[k][i];
					if (id != -1) {
						if (id == id2) {
							//ReikaJavaLibrary.pConsole(slice+" w aID: "+id+"&"+id2+" @ "+i+", "+k+" >> "+dx+","+dy+","+dz);
							return false;
						}
						if (meta != -1) {
							if (meta == meta2) {
								//ReikaJavaLibrary.pConsole(slice+" w ameta: "+meta+"%"+meta2+" @ "+i+", "+k+" >> "+dx+","+dy+","+dz);
								return false;
							}
						}
					}
				}
				else {
					if (id != id2) {
						//ReikaJavaLibrary.pConsole(slice+" w ID: "+id+"&"+id2+" @ "+i+", "+k+" >> "+dx+","+dy+","+dz);
						//world.setBlock(dx, dy, dz, 49);
						return false;
					}
					if (meta != -1) {
						if (meta != meta2) {
							//ReikaJavaLibrary.pConsole(slice+" w meta: "+meta+"%"+meta2+" @ "+i+", "+k+" >> "+dx+","+dy+","+dz);
							//world.setBlock(dx, dy, dz, 49);
							return false;
						}
					}
				}
			}
		}
		return true;
	}
}

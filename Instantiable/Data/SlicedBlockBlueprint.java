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
	private final ArrayList<int[][]> metadatas = new ArrayList();;

	private final HashMap<Character, List<Integer>> mappings = new HashMap();

	public void addMapping(char c, int id, int meta) {
		if (c == 'x')
			throw new MisuseException("Character 'x' is reserved for \"don't care\"!");
		if (c == '-')
			throw new MisuseException("Character '-' is reserved for empty space!");
		mappings.put(c, Arrays.asList(id, meta));
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
		for (int i = 0; i < l; i++) {
			String s = array[i];
			char[] cs = s.toCharArray();
			for (int k = 0; k < cs.length; k++) { //cs.length == w
				char c = cs[k];
				if (c == '-') {
					ids[i][k] = 0;
					metas[i][k] = 0;
				}
				else if (c == 'x') {
					ids[i][k] = -1;
					metas[i][k] = -1;
				}
				else {
					List<Integer> block = mappings.get(c);
					int id = block.get(0);
					int meta = block.get(1);
					ids[i][k] = id;
					metas[i][k] = meta;
				}
			}
		}
		IDs.add(ids);
		metadatas.add(metas);
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
		for (int k = 0; k < ids.length; k++) {
			sb.append("[ ");
			for (int m = 0; m < ids[k].length; m++) {
				int id = ids[k][m];
				int meta = metas[k][m];
				sb.append(id+":"+meta);
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
		//ReikaJavaLibrary.pConsole(this.getString(slice));
		int[][] ids = IDs.get(slice);
		int[][] metas = metadatas.get(slice);
		for (int i = 0; i < ids.length; i++) {
			for (int k = 0; k < ids[i].length; k++) {
				int dx = plane.offsetX == 0 ? x-xref+i : x;
				int dz = plane.offsetZ == 0 ? z-xref+i : z;
				int dy = y+yref-k;
				int id = ids[k][i];
				int meta = metas[k][i];
				if (id != -1) {
					int id2 = world.getBlockId(dx, dy, dz);
					if (id != id2) {
						//ReikaJavaLibrary.pConsole(slice+": "+id+"&"+id2+" @ "+i+", "+k+" >> "+dx+","+dy+","+dz);
						return false;
					}
					if (meta != -1) {
						int meta2 = world.getBlockMetadata(dx, dy, dz);
						if (meta != meta2) {
							//ReikaJavaLibrary.pConsole(slice+": "+meta+"%"+meta2+" @ "+i+", "+k+" >> "+dx+","+dy+","+dz);
							return false;
						}
					}
				}
			}
		}
		return true;
	}
}

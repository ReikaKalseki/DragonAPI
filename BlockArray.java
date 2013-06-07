/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.ReikaMathLibrary;

public class BlockArray {

	private List<int[]> blocks = new ArrayList<int[]>();
	private int liquidID;

	public BlockArray() {

	}

	public void addBlockCoordinate(int x, int y, int z) {
		if (this.hasBlock(x, y, z))
			return;
		int[] e = {x, y, z};
		blocks.add(e);
	}

	public int[] getNextBlock() {
		if (this.isEmpty())
			return null;
		return blocks.get(0);
	}

	public int[] getNextAndMoveOn() {
		int[] next = this.getNextBlock();
		blocks.remove(0);
		return next;
	}

	public int getSize() {
		return blocks.size();
	}

	public void clear() {
		blocks.clear();
	}

	public boolean isEmpty() {
		return blocks.size() == 0;
	}

	public boolean hasBlock(int x, int y, int z) {
		for (int i = 0; i < blocks.size(); i++) {
			int[] e = blocks.get(i);
			if (e[0] == x && e[1] == y && e[2] == z)
				return true;
		}
		return false;
	}

	/** Recursively fills a contiguous area of one block type with another, akin to a fill tool.
	 * Args: World, start x, start y, start z, id to replace */
	public void recursiveFill(World world, int x, int y, int z, int id) {
		if (world.getBlockId(x, y, z) != id)
			return;
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);
		this.recursiveFill(world, x+1, y, z, id);
		this.recursiveFill(world, x-1, y, z, id);
		this.recursiveFill(world, x, y+1, z, id);
		this.recursiveFill(world, x, y-1, z, id);
		this.recursiveFill(world, x, y, z+1, id);
		this.recursiveFill(world, x, y, z-1, id);
	}

	/** Like the ordinary recursive fill but with a bounded volume. Args: World, x, y, z,
	 * id to replace, min x,y,z, max x,y,z */
	public void recursiveFillWithBounds(World world, int x, int y, int z, int id, int x1, int y1, int z1, int x2, int y2, int z2) {
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		if (world.getBlockId(x, y, z) != id) {
			return;
		}
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);
		this.recursiveFillWithBounds(world, x+1, y, z, id, x1, y1, z1, x2, y2, z2);
		this.recursiveFillWithBounds(world, x-1, y, z, id, x1, y1, z1, x2, y2, z2);
		this.recursiveFillWithBounds(world, x, y+1, z, id, x1, y1, z1, x2, y2, z2);
		this.recursiveFillWithBounds(world, x, y-1, z, id, x1, y1, z1, x2, y2, z2);
		this.recursiveFillWithBounds(world, x, y, z+1, id, x1, y1, z1, x2, y2, z2);
		this.recursiveFillWithBounds(world, x, y, z-1, id, x1, y1, z1, x2, y2, z2);
	}

	public void setLiquid(Material mat) {
		if (mat == Material.water)
			liquidID = 9;
		if (mat == Material.lava)
			liquidID = 11;
	}

	/** Like the ordinary recursive fill but with a bounded volume. Args: World, x, y, z,
	 * id to replace, min x,y,z, max x,y,z */
	public void recursiveFillLiquidWithBounds(World world, int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2) {
		//ReikaJavaLibrary.pConsole(liquidID+" and "+world.getBlockId(x, y, z));;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		if (world.getBlockId(x, y, z) != liquidID && world.getBlockId(x, y, z) != liquidID-1) {
			//ReikaJavaLibrary.pConsole("Could not match id "+world.getBlockId(x, y, z)+" to "+liquidID);
			return;
		}
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);
		this.recursiveFillLiquidWithBounds(world, x+1, y, z, x1, y1, z1, x2, y2, z2);
		this.recursiveFillLiquidWithBounds(world, x-1, y, z, x1, y1, z1, x2, y2, z2);
		this.recursiveFillLiquidWithBounds(world, x, y+1, z, x1, y1, z1, x2, y2, z2);
		this.recursiveFillLiquidWithBounds(world, x, y-1, z, x1, y1, z1, x2, y2, z2);
		this.recursiveFillLiquidWithBounds(world, x, y, z+1, x1, y1, z1, x2, y2, z2);
		this.recursiveFillLiquidWithBounds(world, x, y, z-1, x1, y1, z1, x2, y2, z2);
	}

	/** Like the ordinary recursive fill but with a spherical bounded volume. Args: World, x, y, z,
	 * id to replace, origin x,y,z, max radius */
	public void recursiveFillWithinSphere(World world, int x, int y, int z, int id, int x0, int y0, int z0, double r) {
		if (world.getBlockId(x, y, z) != id)
			return;
		if (this.hasBlock(x, y, z))
			return;
		if (ReikaMathLibrary.py3d(x-x0, y-y0, z-z0) > r)
			return;
		this.addBlockCoordinate(x, y, z);
		this.recursiveFillWithinSphere(world, x+1, y, z, id, x0, y0, z0, r);
		this.recursiveFillWithinSphere(world, x-1, y, z, id, x0, y0, z0, r);
		this.recursiveFillWithinSphere(world, x, y+1, z, id, x0, y0, z0, r);
		this.recursiveFillWithinSphere(world, x, y-1, z, id, x0, y0, z0, r);
		this.recursiveFillWithinSphere(world, x, y, z+1, id, x0, y0, z0, r);
		this.recursiveFillWithinSphere(world, x, y, z-1, id, x0, y0, z0, r);
	}

	public void sortBlocksByHeight() {
		List<int[]> newList = new ArrayList<int[]>();
		for (int i = 0; i < blocks.size(); i++) {
			int[] a = blocks.get(i);
			int y = a[1];
			//ReikaJavaLibrary.pConsole("List Size: "+newList.size());
			if (newList.size() == 0) {
				newList.add(a);
				//ReikaJavaLibrary.pConsole("Adding ["+a[0]+","+a[1]+","+a[2]+"] at 0");
			}
			else {
				for (int k = 0; k < newList.size(); k++) {
					int y2 = newList.get(k)[1];
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
		blocks = newList;
	}

	public void reverseBlockOrder() {
		List<int[]> newList = new ArrayList<int[]>();
		for (int i = 0; i < blocks.size(); i++) {
			newList.add(blocks.get(blocks.size()-1-i));
		}
		blocks = newList;
	}

}

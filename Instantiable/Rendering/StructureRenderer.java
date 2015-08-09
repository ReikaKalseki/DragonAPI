/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

public class StructureRenderer {

	private static final VisibilityComparator visibility = new VisibilityComparator();

	private static final RenderItem itemRender = new RenderItem();

	private double rx = 0;
	private double ry = 0;
	private double rz = 0;

	private int secY;

	private final FilledBlockArray array;

	private final HashMap<Coordinate, ItemStack> overrides = new HashMap();
	private final ItemHashMap<ItemStack> itemOverrides = new ItemHashMap();
	private final ItemHashMap<BlockRenderHook> renderHooks = new ItemHashMap();

	public StructureRenderer(FilledBlockArray structure) {
		array = structure;
	}

	public void resetRotation() {
		rx = ry = rz = 0;
	}

	public void rotate(double x, double y, double z) {
		rx += x;
		ry += y;
		rz += z;
	}

	public void reset() {
		this.resetRotation();
		this.resetStepY();
	}

	public void resetStepY() {
		secY = 0;
	}

	public void incrementStepY() {
		if (secY < array.getSizeY()-1) {
			secY++;
		}
	}

	public void decrementStepY() {
		if (secY > 0) {
			secY--;
		}
	}

	public void addOverride(int x, int y, int z, ItemStack is) {
		overrides.put(new Coordinate(x, y, z), is);
	}

	public void addOverride(ItemStack is, ItemStack render) {
		itemOverrides.put(is, render);
	}

	public void addRenderHook(ItemStack is, BlockRenderHook brh) {
		renderHooks.put(is, brh);
	}

	public void drawSlice(int j, int k) {
		int y = array.getMinY()+secY;
		int max = Math.max(array.getSizeX(), array.getSizeZ());
		int dd = max > 16 ? 28-max : 14;
		int ox = 120;
		int oy = 105;
		for (int x = array.getMinX(); x <= array.getMaxX(); x++) {
			for (int z = array.getMinZ(); z <= array.getMaxZ(); z++) {
				ItemStack is = array.getDisplayAt(x, y, z);
				ItemStack over = overrides.get(new Coordinate(x, y, z));
				if (over != null)
					is = over;
				if (is != null && is.getItem() != null) {
					over = itemOverrides.get(is);
					if (over != null)
						is = over;
				}
				if (is != null && is.getItem() != null) {
					int dx = (x-array.getMidX())*dd;
					int dz = (z-array.getMidZ())*dd;
					ReikaGuiAPI.instance.drawItemStack(itemRender, is, j+dx+ox, k+dz+oy);
				}
			}
		}
	}

	public void draw3D(int j, int k) {
		int dd = 12;
		int ddy = 12;

		HashMap<Vector3f, CoordStack> render = new HashMap();

		Matrix4f rot = new Matrix4f();
		ReikaVectorHelper.euler213Sequence(rot, rx, ry, rz);
		if (array.isEmpty())
			return;

		for (int y = array.getMinY(); y <= array.getMaxY(); y++) {
			for (int x = array.getMinX(); x <= array.getMaxX(); x++) {
				for (int z = array.getMinZ(); z <= array.getMaxZ(); z++) {
					ItemStack is = array.getDisplayAt(x, y, z);
					ItemStack over = overrides.get(new Coordinate(x, y, z));
					if (over != null)
						is = over;
					if (is != null) {
						int dx = x-array.getMidX();
						int dy = y-array.getMidY();
						int dz = z-array.getMidZ();
						Vector3f in = new Vector3f(dx, dy, dz);
						Vector3f vec = ReikaVectorHelper.multiplyVectorByMatrix(in, rot);
						int px = Math.round(vec.x*dd+vec.z*dd);
						int py = Math.round(-vec.x*dd/2+vec.z*dd/2-vec.y*ddy);
						int pz = 0;//250;
						render.put(vec, new CoordStack(is, px, py, pz));
					}
				}
			}
		}

		double max = Math.max(array.getSizeY()*1, Math.sqrt(Math.pow(array.getSizeX(), 2)+Math.pow(array.getMaxZ(), 2)));
		//ReikaJavaLibrary.pConsole(max);
		GL11.glPushMatrix();
		double d = 2;
		if (max >= 18) {
			d = 0.6;
		}
		else if (max >= 14) {
			d = 0.8;
		}
		else if (max >= 12) {
			d = 0.95;
		}
		else if (max >= 10) {
			d = 1.2;
		}
		else if (max >= 8) {
			d = 1.5;
		}
		else if (max >= 4) {
			d = 1.75;
		}
		GL11.glScaled(d, d, 1);

		int ox = (int)((j+122)/d);
		int oy = (int)((k+92)/d);
		if (d > 1)
			ox -= 5;
		if (d > 1)
			oy -= 5;

		ArrayList<Vector3f> keys = new ArrayList(render.keySet());
		Collections.sort(keys, visibility);

		for (Vector3f vec : keys) {
			CoordStack is = render.get(vec);
			if (is.item != null && is.item.getItem() != null) {
				GL11.glPushMatrix();
				GL11.glTranslated(0, 0, is.coord.zCoord);
				double scale = 1;
				int ox2 = 0;
				int oy2 = 0;
				BlockRenderHook brh = renderHooks.get(is.item);
				if (brh != null) {
					scale = brh.getScale();
					ox2 = brh.getOffsetX();
					oy2 = brh.getOffsetY();
				}
				GL11.glScaled(scale, scale, 1);
				ReikaGuiAPI.instance.drawItemStack(itemRender, is.item, (int)((is.coord.xCoord+ox)/scale)+ox2, (int)((is.coord.yCoord+oy)/scale)+oy2);
				GL11.glPopMatrix();
			}
		}

		GL11.glPopMatrix();
	}

	private static class VisibilityComparator implements Comparator<Vector3f> {

		private boolean posX = true;
		private boolean posY = true;
		private boolean posZ = true;

		@Override
		public int compare(Vector3f o1, Vector3f o2) {
			/*
			int dx = o1.xCoord-o2.xCoord;
			int dy = o1.yCoord-o2.yCoord;
			int dz = o1.zCoord-o2.zCoord;
			int mx = posX ? dx : -dx;
			int my = posY ? dy : -dy;
			int mz = posZ ? dz : -dz;
			return mx+my+mz;
			 */
			return (int)Math.signum(o1.z-o2.z);
		}

	}

	private static class CoordStack {

		private final ItemStack item;
		private final Coordinate coord;

		private CoordStack(ItemStack is, int x, int y, int z) {
			this(is, new Coordinate(x, y, z));
		}

		private CoordStack(ItemStack is, Coordinate c) {
			coord = c;
			item = is;
		}

	}

	public interface BlockRenderHook {

		public double getScale();
		public int getOffsetX();
		public int getOffsetY();

	}
}

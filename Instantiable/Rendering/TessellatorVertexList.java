/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

public class TessellatorVertexList {

	private ArrayList<TessellatorVertex> data = new ArrayList();
	private final LinkedList<ArrayList<TessellatorVertex>> list = new LinkedList();

	public final double originX;
	public final double originY;
	public final double originZ;

	public TessellatorVertexList() {
		this(0, 0, 0);
	}

	public TessellatorVertexList(double x, double y, double z) {
		originX = x;
		originY = y;
		originZ = z;
	}

	public void addVertex(double x, double y, double z) {
		data.add(new TessellatorVertex(x, y, z));
	}

	public void addVertexWithUV(double x, double y, double z, double u, double v) {
		data.add(new TessellatorVertex(x, y, z, u, v));
	}

	public void addVertexWithUVColor(double x, double y, double z, double u, double v, int color) {
		data.add(new TessellatorVertex(x, y, z, u, v, color));
	}

	public void render() {
		for (TessellatorVertex v : data) {
			v.addToTessellator();
		}
	}

	public void reverse() {
		Collections.reverse(data);
	}

	private static class TessellatorVertex {
		private double posX;
		private double posY;
		private double posZ;

		private double posU;
		private double posV;

		private int colorData;

		private boolean hasUV;
		private boolean hasColor;

		private TessellatorVertex(double x, double y, double z) {
			this(x, y, z, 0, 0);
			hasUV = false;
		}

		private TessellatorVertex(double x, double y, double z, double u, double v) {
			this(x, y, z, u, v, 0);
			hasColor = false;
		}

		private TessellatorVertex(double x, double y, double z, double u, double v, int color) {
			posX = x;
			posY = y;
			posZ = z;
			posU = u;
			posV = v;
			colorData = color;
			hasUV = true;
			hasColor = true;
		}

		private void addToTessellator() {
			if (hasColor) {
				Tessellator.instance.setColorOpaque_I(colorData);
			}
			if (hasUV)
				Tessellator.instance.addVertexWithUV(posX, posY, posZ, posU, posV);
			else
				Tessellator.instance.addVertex(posX, posY, posZ);
		}

		@Override
		public final boolean equals(Object o) {
			if (o instanceof TessellatorVertex) {
				TessellatorVertex v = (TessellatorVertex)o;
				if (v.posX == posX && v.posY == posY && v.posZ == posZ) {
					return hasUV ? (v.hasUV && v.posU == posU && v.posV == posV) : !v.hasUV;
				}
			}
			return false;
		}
	}

	public void offset(double dx, double dy, double dz) {
		for (TessellatorVertex v : data) {
			v.posX += dx;
			v.posY += dy;
			v.posZ += dz;
		}
	}

	public void scale(double dx, double dy, double dz) {
		for (TessellatorVertex v : data) {
			v.posX *= dx;
			v.posY *= dy;
			v.posZ *= dz;
		}
	}

	public void clamp(double minx, double miny, double minz, double maxx, double maxy, double maxz) {
		for (TessellatorVertex v : data) {
			v.posX = MathHelper.clamp_double(v.posX, minx, maxx);
			v.posY = MathHelper.clamp_double(v.posY, miny, maxy);
			v.posZ = MathHelper.clamp_double(v.posZ, minz, maxz);
		}
	}

	public void invertX() {
		for (TessellatorVertex v : data) {
			v.posX = 1-v.posX;
		}
		this.reverse();
	}

	public void invertY() {
		for (TessellatorVertex v : data) {
			v.posY = 1-v.posY;
		}
		this.reverse();
	}

	public void invertZ() {
		for (TessellatorVertex v : data) {
			v.posZ = 1-v.posZ;
		}
		this.reverse();
	}

	/** CW about +X */
	public void rotateYtoZ() {
		for (TessellatorVertex v : data) {
			double z = v.posZ-originZ;
			v.posZ = originZ+v.posY-originY;
			v.posY = originY+z;
		}
		this.reverse();
	}

	/** CW about +Y */
	public void rotateXtoZ() {
		for (TessellatorVertex v : data) {
			double z = v.posZ-originZ;
			v.posZ = originZ+v.posX-originX;
			v.posX = originX+z;
		}
		this.reverse();
	}

	/** CW about +Z */
	public void rotateYtoX() {
		for (TessellatorVertex v : data) {
			double x = v.posX-originX;
			v.posX = originX+v.posY-originY;
			v.posY = originY+x;
		}
		this.reverse();
	}

	public void rotateNonOrthogonal(double rx, double ry, double rz) {
		for (TessellatorVertex v : data) {
			Vec3 ret = ReikaVectorHelper.rotateVector(Vec3.createVectorHelper(v.posX-originX, v.posY-originY, v.posZ-originZ), rx, ry, rz);
			v.posX = originX+ret.xCoord;
			v.posY = originY+ret.yCoord;
			v.posZ = originZ+ret.zCoord;
		}
	}

	public void clear() {
		data.clear();
	}

	public void push() {
		list.addLast(data);
		data = new ArrayList();
	}

	public void pop() {
		if (list.isEmpty())
			throw new IllegalStateException("Popped an empty list!");
		data = list.pollLast();
	}

	public void addAll(TessellatorVertexList li) {
		list.addAll(li.list);
		data.addAll(li.data);
	}

}

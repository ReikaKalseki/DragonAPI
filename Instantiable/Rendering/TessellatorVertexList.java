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

import net.minecraft.client.renderer.Tessellator;

public class TessellatorVertexList {

	private final ArrayList<TessellatorVertex> data = new ArrayList();

	public TessellatorVertexList() {

	}

	public void addVertex(double x, double y, double z) {
		data.add(new TessellatorVertex(x, y, z));
	}

	public void addVertexWithUV(double x, double y, double z, double u, double v) {
		data.add(new TessellatorVertex(x, y, z, u, v));
	}

	public void render() {
		for (int i = 0; i < data.size(); i++) {
			TessellatorVertex v = data.get(i);
			v.addToTessellator();
		}
	}

	public void reverse() {
		Collections.reverse(data);
	}

	private static class TessellatorVertex {
		public final double posX;
		public final double posY;
		public final double posZ;

		public final double posU;
		public final double posV;

		private boolean hasUV;

		private TessellatorVertex(double x, double y, double z) {
			this(x, y, z, 0, 0);
			hasUV = false;
		}

		private TessellatorVertex(double x, double y, double z, double u, double v) {
			posX = x;
			posY = y;
			posZ = z;
			posU = u;
			posV = v;
			hasUV = true;
		}

		private void addToTessellator() {
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

}

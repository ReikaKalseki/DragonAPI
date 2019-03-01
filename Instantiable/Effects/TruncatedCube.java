/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;


public class TruncatedCube {

	public double mainSize;
	public double cutSize;

	private List<DecimalPosition>[] faces;
	private List<ArrayList<DecimalPosition>> corners;

	public TruncatedCube(double s, double s2) {
		mainSize = s2;
		cutSize = s;
	}

	public TruncatedCube cache(boolean startCenter, double x0, double y0, double z0) {
		faces = new List[6];
		for (int i = 0; i < 6; i++) {
			faces[i] = Collections.unmodifiableList(this.getFaceVertices(ForgeDirection.VALID_DIRECTIONS[i], startCenter, x0, y0, z0));
		}
		corners = Collections.unmodifiableList(this.getCornerVertices(x0, y0, z0));
		return this;
	}

	public List<DecimalPosition> getFaceVertices(ForgeDirection face, boolean startCenter, double x0, double y0, double z0) {
		if (faces != null)
			return faces[face.ordinal()];

		ArrayList<DecimalPosition> li = new ArrayList();
		switch(face) {
			case DOWN:
				if (startCenter)
					li.add(new DecimalPosition(x0, y0-mainSize, z0));
				li.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0-mainSize));
				break;
			case UP:
				if (startCenter)
					li.add(new DecimalPosition(x0, y0+mainSize, z0));
				li.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0-mainSize));
				break;
			case WEST:
				if (startCenter)
					li.add(new DecimalPosition(x0-mainSize, y0, z0));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0-mainSize));
				break;
			case EAST:
				if (startCenter)
					li.add(new DecimalPosition(x0+mainSize, y0, z0));
				li.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0-mainSize));
				break;
			case SOUTH:
				if (startCenter)
					li.add(new DecimalPosition(x0, y0, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0+mainSize));
				break;
			case NORTH:
				if (startCenter)
					li.add(new DecimalPosition(x0, y0, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0-mainSize));
				break;
			default:
				break;
		}
		return li;
	}

	public List<ArrayList<DecimalPosition>> getCornerVertices(double x0, double y0, double z0) {
		if (corners != null)
			return corners;

		ArrayList<ArrayList<DecimalPosition>> li = new ArrayList();

		//top corners
		ArrayList<DecimalPosition> li2 = new ArrayList();
		li2.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0-cutSize));
		li2.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0-mainSize));
		li2.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0-mainSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0-mainSize));
		li2.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0-mainSize));
		li2.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0-cutSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0+mainSize));
		li2.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0+mainSize));
		li2.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0+cutSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0+cutSize));
		li2.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0+mainSize));
		li2.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0+mainSize));
		li.add(li2);

		//bottom corners
		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0-mainSize));
		li2.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0-mainSize));
		li2.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0-cutSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0-cutSize));
		li2.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0-mainSize));
		li2.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0-mainSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0+cutSize));
		li2.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0+mainSize));
		li2.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0+mainSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0+mainSize));
		li2.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0+mainSize));
		li2.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0+cutSize));
		li.add(li2);

		return li;
	}

	public void render(double x, double y, double z, int c1, int c2, boolean edge, float pdist) {
		int a1 = ReikaColorAPI.getAlpha(c1);
		int a2 = ReikaColorAPI.getAlpha(c2);
		Tessellator v5 = Tessellator.instance;
		float p = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		if (edge) {
			float w = Math.max(0.125F, 2F-0.0625F*pdist);
			GL11.glLineWidth(w);
		}

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			List<DecimalPosition> li = this.getFaceVertices(dir, true, x, y, z);

			v5.startDrawing(GL11.GL_TRIANGLE_FAN);
			v5.setColorRGBA_I(c1 & 0xffffff, a1);
			v5.setBrightness(240);
			for (DecimalPosition d : li) {
				v5.addVertex(d.xCoord, d.yCoord, d.zCoord);
			}
			v5.draw();

			if (edge) {
				li = this.getFaceVertices(dir, false, x, y, z);
				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorRGBA_I(c2 & 0xffffff, a2);
				v5.setBrightness(240);
				for (DecimalPosition d : li) {
					v5.addVertex(d.xCoord, d.yCoord, d.zCoord);
				}
				v5.draw();
			}
		}

		for (List<DecimalPosition> li : this.getCornerVertices(x, y, z)) {
			v5.startDrawing(GL11.GL_TRIANGLES);
			v5.setColorRGBA_I(c1 & 0xffffff, a1);
			v5.setBrightness(240);
			for (DecimalPosition d : li) {
				v5.addVertex(d.xCoord, d.yCoord, d.zCoord);
			}
			v5.draw();

			if (edge) {
				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorRGBA_I(c2 & 0xffffff, a2);
				v5.setBrightness(240);
				for (DecimalPosition d : li) {
					v5.addVertex(d.xCoord, d.yCoord, d.zCoord);
				}
				v5.draw();
			}
		}

		if (edge) {
			GL11.glLineWidth(p);
		}
	}

}

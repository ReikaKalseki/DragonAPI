/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;

import Reika.DragonAPI.Libraries.ReikaDirectionHelper.FanDirections;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class Compass<F> extends CircularDivisionRenderer<F> {

	public final CompassDivisions divisions;
	private final Map<FanDirections, F> data = new HashMap();

	public Compass(CompassDivisions d) {
		divisions = d;
	}

	public void addValue(FanDirections dir, F val) {
		data.put(dir, val);
		if (val instanceof ColorCallback)
			this.addColorRenderer(val, (ColorCallback)val);
		this.resetColors();
	}

	public void removeValue(FanDirections dir) {
		data.remove(dir);
		this.resetColors();
	}

	public F getValue(FanDirections dir) {
		return data.get(dir);
	}

	@Override
	public Collection<F> getElements() {
		return Collections.unmodifiableCollection(this.data.values());
	}

	@Override
	public void clear() {
		data.clear();
		this.resetColors();
	}

	@Override
	public F getClickedSection(int x, int y) {
		double d = ReikaMathLibrary.py3d(x-centerX, y-centerY, 0);
		if (d > renderRadius)
			return null;
		double mouseAng = (Math.toDegrees(Math.atan2(y-centerY, x-centerX))+360)%360;
		for (Entry<FanDirections, F> e : data.entrySet()) {
			FanDirections dir = e.getKey();
			double ang1 = renderOrigin-dir.angle-divisions.angleSize/2;
			double ang2 = renderOrigin-dir.angle+divisions.angleSize/2;
			//ReikaJavaLibrary.pConsole(o+" > "+ang+" - "+(ang+angw)+" @ "+relAng);
			if (ang1 < mouseAng && ang2 > mouseAng) {
				return e.getValue();
			}
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(Map<F, Integer> colorMap) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		Tessellator v5 = Tessellator.instance;
		int i = 0;
		for (Entry<FanDirections, F> e : data.entrySet()) {
			FanDirections dir = e.getKey();
			double ang1 = renderOrigin-dir.angle-divisions.angleSize/2;
			double ang2 = renderOrigin-dir.angle+divisions.angleSize/2;
			F o = e.getValue();

			v5.startDrawing(innerRadius == 0 ? GL11.GL_TRIANGLE_FAN : GL11.GL_TRIANGLE_STRIP);
			int c = this.getColorForElement(o, colorMap);
			v5.setColorOpaque_I(c);

			//ReikaJavaLibrary.pConsole(o+" > "+this.getFraction(o)+" = "+angw);

			this.renderSection(v5, ang1, ang2);

			v5.draw();
		}
		GL11.glPopAttrib();
	}

	public static enum CompassDivisions {
		CARDINAL(4),
		OCTAGON(8),
		FULL(16);

		public final double angleSize;

		private CompassDivisions(int n) {
			angleSize = 360D/n;
		}

		public Collection<FanDirections> getDirections() {
			HashSet<FanDirections> ret = new HashSet();
			switch(this) {
				case CARDINAL:
					ret.add(FanDirections.E);
					ret.add(FanDirections.S);
					ret.add(FanDirections.W);
					ret.add(FanDirections.N);
				case OCTAGON:
					ret.add(FanDirections.NE);
					ret.add(FanDirections.SE);
					ret.add(FanDirections.NW);
					ret.add(FanDirections.SW);
				case FULL:
					ret.add(FanDirections.NNE);
					ret.add(FanDirections.ENE);
					ret.add(FanDirections.ESE);
					ret.add(FanDirections.SSE);
					ret.add(FanDirections.SSW);
					ret.add(FanDirections.WSW);
					ret.add(FanDirections.WNW);
					ret.add(FanDirections.NNW);
					break;
			}
			return ret;
		}
	}

}

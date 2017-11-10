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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.MapDeterminator;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class Proportionality<F> {

	private static final Random rand = new Random();

	private final Map<F, Double> data;
	private final HashMap<F, ColorCallback> renderColors = new HashMap();

	private double totalValue = 0;

	private double centerX;
	private double centerY;
	private double renderRadius;
	private double renderOrigin;

	public Proportionality() {
		this(null);
	}

	public Proportionality(MapDeterminator<F, ?> md) {
		data = md != null ? md.getMapType() : new HashMap();
	}

	public void addValue(F o, double amt) {
		Double get = data.get(o);
		double val = get != null ? get.doubleValue() : 0;
		data.put(o, val+amt);
		this.totalValue += amt;
		if (o instanceof ColorCallback)
			this.addColorRenderer(o, (ColorCallback)o);
	}

	public void removeValue(F o, double amt) {
		Double get = data.get(o);
		double val = get != null ? get.doubleValue() : 0;
		double res = val-amt;
		this.totalValue -= Math.min(amt, val);
		if (res > 0)
			data.put(o, res);
		else
			data.remove(o);
	}

	public double getValue(F o) {
		Double get = data.get(o);
		return get != null ? get.doubleValue() : 0;
	}

	public double getFraction(F o) {
		return this.getValue(o)/this.totalValue;
	}

	public Collection<F> getElements() {
		return Collections.unmodifiableCollection(this.data.keySet());
	}

	public boolean hasMajority(F o) {
		return this.getFraction(o) >= 0.5;
	}

	public F getLargestCategory() {
		double max = -1;
		F big = null;
		for (F o : data.keySet()) {
			double has = this.getValue(o);
			if (has > max) {
				has = max;
				big = o;
			}
		}
		return big;
	}

	public void clear() {
		data.clear();
	}

	public void addColorRenderer(F type, ColorCallback call) {
		this.renderColors.put(type, call);
	}

	public F getClickedSection(int x, int y) {
		double d = ReikaMathLibrary.py3d(x-centerX, y-centerY, 0);
		if (d > this.renderRadius)
			return null;
		double relAng = (Math.toDegrees(Math.atan2(y-centerY, x-centerX))+360)%360;
		double ang = this.renderOrigin;
		for (F o : data.keySet()) {
			double angw = 360D*this.getFraction(o);
			//ReikaJavaLibrary.pConsole(o+" > "+ang+" - "+(ang+angw)+" @ "+relAng);
			if (ang <= relAng && ang+angw >= relAng) {
				return o;
			}
			ang += angw;
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public void renderAsPie(double x, double y, double r, double zeroAng) {
		this.renderAsPie(x, y, r, zeroAng, null);
	}

	@SideOnly(Side.CLIENT)
	public void renderAsPie(double x, double y, double r, double zeroAng, Map<F, Integer> colorMap) {
		this.centerX = x;
		this.centerY = y;
		this.renderRadius = r;
		this.renderOrigin = zeroAng;

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		double ang = zeroAng;
		Tessellator v5 = Tessellator.instance;
		int i = 0;
		for (F o : data.keySet()) {
			double angw = 360D*this.getFraction(o);

			v5.startDrawing(GL11.GL_TRIANGLE_FAN);
			int c = 0;
			if (colorMap != null && colorMap.containsKey(o)) {
				c = colorMap.get(o);
			}
			else {
				ColorCallback call = this.renderColors.get(o);
				if (call != null) {
					c = call.getColor(o);
				}
				else {
					if (i >= defaultColors.size()) {
						int newcolor = ReikaColorAPI.RGBtoHex(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
						defaultColors.add(newcolor);
						c = newcolor;
					}
					else {
						c = defaultColors.get(i);
					}
					i++;
				}
			}
			v5.setColorOpaque_I(c);

			//ReikaJavaLibrary.pConsole(o+" > "+this.getFraction(o)+" = "+angw);

			v5.addVertex(x, y, 0);

			for (double d = ang; d <= ang+angw; d += 0.25) {
				double dx = x+r*Math.cos(Math.toRadians(d));
				double dy = y+r*Math.sin(Math.toRadians(d));
				v5.addVertex(dx, dy, 0);
			}

			double dx = x+r*Math.cos(Math.toRadians(ang+angw));
			double dy = y+r*Math.sin(Math.toRadians(ang+angw));
			v5.addVertex(dx, dy, 0);

			v5.draw();

			ang += angw;
		}
		GL11.glPopAttrib();
	}

	public static interface ColorCallback {

		public int getColor(Object key);

	}

	private static ArrayList<Integer> defaultColors = new ArrayList();

	static {
		defaultColors.add(0xff0000);
		defaultColors.add(0x00ff00);
		defaultColors.add(0x0000ff);
		defaultColors.add(0xffff00);
		defaultColors.add(0xff00ff);
		defaultColors.add(0x00ffff);
		defaultColors.add(0xa0a0a0);
	}

	/*
	public void writeToNBT(NBTTagCompound NBT) {
		NBT.setDouble("total", totalValue);
		NBTTagList li = new NBTTagList();
		for (F o : data.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("Prop_"+o.toString(), this.getValue(o));
		}
		NBT.setTag("data", li);
	}

	public void readFromNBT(NBTTagCompound NBT) {
		totalValue = NBT.getDouble("total");
		data.clear();


	}
	 */

}

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
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.MapDeterminator;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTIO;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class Proportionality<F> extends CircularDivisionRenderer<F> {

	private final Map<F, Double> data;
	private double totalValue = 0;

	public boolean drawSeparationLines = false;

	public Proportionality() {
		this(null);
	}

	public Proportionality(MapDeterminator<F, Double> md) {
		data = md != null ? md.getMapType() : new HashMap();
	}

	public void addValue(F o, double amt) {
		Double get = data.get(o);
		double val = get != null ? get.doubleValue() : 0;
		data.put(o, val+amt);
		this.totalValue += amt;
		if (o instanceof ColorCallback)
			this.addColorRenderer(o, (ColorCallback)o);
		this.resetColors();
	}

	public double removeValue(F o) {
		Double get = data.remove(o);
		if (get != null) {
			this.totalValue -= get;
		}
		this.resetColors();
		return get != null ? get.doubleValue() : 0;
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
		this.resetColors();
	}

	public double getValue(F o) {
		Double get = data.get(o);
		return get != null ? get.doubleValue() : 0;
	}

	public double getFraction(F o) {
		return this.isEmpty() ? 0 : this.getValue(o)/this.totalValue;
	}

	public boolean isEmpty() {
		return this.totalValue == 0 || this.data.isEmpty();
	}

	@Override
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
		double relAng = (Math.toDegrees(Math.atan2(y-centerY, x-centerX))+360)%360-renderOrigin;
		relAng = ((relAng%360)+360)%360;
		double ang = 0;
		for (F o : data.keySet()) {
			double angw = 360D*this.getFraction(o);
			if (ang <= relAng && ang+angw >= relAng) {
				return o;
			}
			ang += angw;
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
		double ang = renderOrigin;
		Tessellator v5 = Tessellator.instance;
		int i = 0;
		for (F o : data.keySet()) {
			double angw = 360D*this.getFraction(o);

			v5.startDrawing(innerRadius == 0 ? GL11.GL_TRIANGLE_FAN : GL11.GL_TRIANGLE_STRIP);
			int c = this.getColorForElement(o, colorMap);
			v5.setColorOpaque_I(c);

			//ReikaJavaLibrary.pConsole(o+" > "+this.getFraction(o)+" = "+angw);

			this.renderSection(v5, ang, ang+angw);

			v5.draw();

			ang += angw;
		}

		if (drawSeparationLines && data.size() > 1) {
			v5.startDrawing(GL11.GL_LINES);
			v5.setColorOpaque_I(0x000000);
			for (F o : data.keySet()) {
				double angw = 360D*this.getFraction(o);
				if (innerRadius == 0) {
					v5.addVertex(centerX, centerY, 0);
					double d2 = Math.toRadians(ang);
					double r2 = this.getOuterRadiusAt(d2);
					double dx = centerX+r2*Math.cos(d2);
					double dy = centerY+r2*Math.sin(d2);
					v5.addVertex(dx, dy, 0);
				}
				else {
					double d2 = Math.toRadians(ang);
					double r1 = this.getInnerRadiusAt(d2);
					double r2 = this.getOuterRadiusAt(d2);
					double dx1 = centerX+r1*Math.cos(d2);
					double dy1 = centerY+r1*Math.sin(d2);
					double dx2 = centerX+r2*Math.cos(d2);
					double dy2 = centerY+r2*Math.sin(d2);
					v5.addVertex(dx1, dy1, 0);
					v5.addVertex(dx2, dy2, 0);
				}
				ang += angw;
			}
			v5.draw();
		}
		GL11.glPopAttrib();
	}

	public void writeToNBT(NBTTagCompound NBT, NBTIO<F> converter) {
		NBT.setDouble("total", totalValue);
		NBT.setBoolean("lines", drawSeparationLines);
		NBTTagList li = new NBTTagList();
		for (Entry<F, Double> e : data.entrySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			NBTBase key = converter.convertToNBT(e.getKey());
			tag.setDouble("value", e.getValue());
			tag.setTag("key", key);
			li.appendTag(tag);
		}
		NBT.setTag("data", li);
	}

	public void readFromNBT(NBTTagCompound NBT, NBTIO<F> converter) {
		totalValue = NBT.getDouble("total");
		this.drawSeparationLines = NBT.getBoolean("lines");
		data.clear();
		NBTTagList li = NBT.getTagList("data", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			F obj = converter.createFromNBT(tag.getTag("key"));
			double val = tag.getDouble("value");
			data.put(obj, val);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (F f : this.data.keySet()) {
			sb.append(f.toString());
			sb.append(": ");
			sb.append(String.valueOf(this.getFraction(f)*100));
			sb.append("%; ");
		}
		return sb.toString();
	}

	public String mapString() {
		return this.data.toString();
	}

	public Proportionality<F> copy() {
		Proportionality ret = new Proportionality();
		ret.data.putAll(this.data);
		ret.totalValue = this.totalValue;
		return ret;
	}

}

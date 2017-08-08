/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import java.util.ArrayList;
import java.util.Random;

import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class ColorBlendList {

	private final ArrayList<Integer> data = new ArrayList();

	/** Larger = slower */
	private final float cycleModulus;

	/** Larger = slower */
	public ColorBlendList(float speed) {
		cycleModulus = speed;
	}

	/** Larger = slower */
	public ColorBlendList(float speed, int... colors) {
		this(speed);
		this.addAll(colors);
	}

	public ColorBlendList addAll(int... colors) {
		for (int i = 0; i < colors.length; i++) {
			this.addColor(colors[i]);
		}
		return this;
	}

	public ColorBlendList addColor(int color) {
		data.add(color);
		return this;
	}

	public int getColor(double tick) {
		if (data.isEmpty())
			return 0;
		//int idx = this.getIndex(tick);
		//int idxp = idx == data.size()-1 ? 0 : idx+1;
		//int idxm = idx == 0 ? data.size()-1 : idx-1;
		long f1 = (long)(tick/cycleModulus);
		f1 = (f1+data.size())%data.size();
		int c1 = data.get((int)(f1)%data.size());
		int c2 = data.get(((int)(f1)+1)%data.size());
		float f = (float)(tick%cycleModulus/cycleModulus);
		return ReikaColorAPI.mixColors(c1, c2, 1-f);
	}
	/*
	private int getIndex(float tick) {
		return (int)(tick*cycleSpeed%data.size());
	}
	 */

	public ColorBlendList shiftHue(int dh) {
		ArrayList<Integer> li = new ArrayList();
		for (int c : data) {
			int hue = ReikaColorAPI.getHue(c)+dh;
			li.add(ReikaColorAPI.getModifiedHue(c, hue));
		}
		data.clear();
		data.addAll(li);
		return this;
	}

	public ColorBlendList multiplySaturation(float fac) {
		ArrayList<Integer> li = new ArrayList();
		for (int c : data) {
			li.add(ReikaColorAPI.getModifiedSat(c, fac));
		}
		data.clear();
		data.addAll(li);
		return this;
	}

	public ColorBlendList multiplyBrightness(float fac) {
		ArrayList<Integer> li = new ArrayList();
		for (int c : data) {
			li.add(ReikaColorAPI.getColorWithBrightnessMultiplier(c, fac));
		}
		data.clear();
		data.addAll(li);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(cycleModulus);
		sb.append("x [");
		for (int color : data) {
			sb.append(Integer.toHexString(color));
			sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	public int getRandomBlendedColor(Random rand) {
		return this.getColor(rand.nextDouble()*data.size()*cycleModulus);
	}

}

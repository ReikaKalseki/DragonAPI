/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Math;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;

import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;


public class MovingAverage {

	private final double[] data;

	public MovingAverage(int dataPoints) {
		data = new double[dataPoints];
	}

	public MovingAverage addValue(double val) {
		ReikaArrayHelper.cycleArray(data, val);
		return this;
	}

	public double getAverage() {
		double avg = 0;
		for (int i = 0; i < data.length; i++) {
			avg += data[i];
		}
		return avg/data.length;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("size", data.length);
		NBTTagList li = new NBTTagList();

		for (double d : data) {
			li.appendTag(new NBTTagDouble(d));
		}

		tag.setTag("data", li);
	}

	public static MovingAverage readFromNBT(NBTTagCompound tag) {
		int size = tag.getInteger("size");
		MovingAverage mv = new MovingAverage(size);
		NBTTagList li = tag.getTagList("data", NBTTypes.DOUBLE.ID);
		for (int i = 0; i < li.tagCount(); i++) {
			mv.data[i] = ((NBTTagDouble)li.tagList.get(i)).func_150286_g();
		}
		return mv;
	}

}

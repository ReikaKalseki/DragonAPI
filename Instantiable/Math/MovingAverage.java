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

import java.util.ArrayDeque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;

import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;


public class MovingAverage {

	private final int size;
	private final ArrayDeque<Double> data;

	public MovingAverage(int dataPoints) {
		size = dataPoints;
		data = new ArrayDeque(dataPoints);
		for (int i = 0; i < size; i++) {
			data.add(0D);
		}
		//ReikaJavaLibrary.pConsole("ctr"+data, Side.SERVER);
	}

	public MovingAverage addValue(double val) {
		//ReikaJavaLibrary.pConsole("pre"+data, Side.SERVER);
		data.add(val);
		data.remove();
		//ReikaJavaLibrary.pConsole("post"+data, Side.SERVER);
		return this;
	}

	public double getAverage() {
		double avg = 0;
		for (double d : data) {
			avg += d;
		}
		return avg/size;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("size", size);
		NBTTagList li = new NBTTagList();

		for (double d : data) {
			li.appendTag(new NBTTagDouble(d));
		}

		tag.setTag("data", li);
	}

	public static MovingAverage readFromNBT(NBTTagCompound tag) {
		int size = tag.getInteger("size");
		MovingAverage mv = new MovingAverage(size);
		mv.data.clear();
		NBTTagList li = tag.getTagList("data", NBTTypes.DOUBLE.ID);
		for (int i = 0; i < li.tagCount(); i++) {
			mv.data.add(((NBTTagDouble)li.tagList.get(i)).func_150286_g());
		}
		while (mv.data.size() < mv.size)
			mv.data.add(0D);
		//ReikaJavaLibrary.pConsole("nbt"+mv.data, Side.SERVER);
		return mv;
	}

}

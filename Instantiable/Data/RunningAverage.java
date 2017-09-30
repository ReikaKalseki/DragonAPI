package Reika.DragonAPI.Instantiable.Data;

import net.minecraft.nbt.NBTTagCompound;


public class RunningAverage {

	private int numberDataPoints;
	private double currentAverage;

	public void addValue(double val) {
		currentAverage = numberDataPoints == 0 ? val : (currentAverage*numberDataPoints+val)/(1+numberDataPoints);
		numberDataPoints++;
	}

	public double getAverage() {
		return currentAverage;
	}

	public void readFromNBT(String key, NBTTagCompound nbt) {
		NBTTagCompound tag = nbt.getCompoundTag(key);
		numberDataPoints = tag.getInteger("npoints");
		currentAverage = tag.getDouble("avg");
	}

	public void writeToNBT(String key, NBTTagCompound nbt) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("npoints", numberDataPoints);
		tag.setDouble("avg", currentAverage);
		nbt.setTag(key, tag);
	}

}

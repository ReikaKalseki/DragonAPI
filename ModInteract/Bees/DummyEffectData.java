package Reika.DragonAPI.ModInteract.Bees;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.IEffectData;


public class DummyEffectData implements IEffectData {

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	public void setInteger(int index, int val) {

	}

	@Override
	public void setFloat(int index, float val) {

	}

	@Override
	public void setBoolean(int index, boolean val) {

	}

	@Override
	public int getInteger(int index) {
		return 0;
	}

	@Override
	public float getFloat(int index) {
		return 0;
	}

	@Override
	public boolean getBoolean(int index) {
		return false;
	}

}

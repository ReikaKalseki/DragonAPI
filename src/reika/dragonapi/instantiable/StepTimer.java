/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import reika.dragonapi.libraries.java.ReikaRandomHelper;

public final class StepTimer {

	private int value;
	private int cap;

	public StepTimer(int top) {
		cap = top;
	}

	public StepTimer setCap(int val) {
		cap = val;
		return this;
	}

	public StepTimer stagger() {
		value = ReikaRandomHelper.getSafeRandomInt(cap);
		return this;
	}

	public void update() {
		value++;
	}

	public void update(int time) {
		value += time;
	}

	private boolean isAtCap() {
		return value >= cap;
	}

	public boolean checkCap() {
		boolean cap = this.isAtCap();
		if (cap)
			this.reset();
		return cap;
	}

	public void reset() {
		value = 0;
	}

	public void setTick(int tick) {
		value = tick;
	}

	public void randomizeTick(Random r) {
		this.setTick(r.nextInt(cap));
	}

	public int getTick() {
		return value;
	}

	public int getCap() {
		return cap;
	}

	public float getFraction() {
		return (float)value/(float)cap;
	}

	@Override
	public String toString() {
		return "Timer @ "+value+"/"+cap;
	}

	protected void writeSyncTag(NBTTagCompound NBT, String id) {
		NBT.setInteger(id+"cap", cap);
		NBT.setInteger(id+"tick", value);
	}

	protected void readSyncTag(NBTTagCompound NBT, String id) {
		cap = NBT.getInteger(id+"cap");
		value = NBT.getInteger(id+"tick");
	}

}

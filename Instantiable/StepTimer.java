/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

public class StepTimer {

	public static final int SECOND = 20;
	public static final int MINUTE = 1200;
	public static final int HOUR = 72000;

	private int value;
	private int cap;

	public StepTimer(int top) {
		cap = top;
	}

	public void setCap(int val) {
		cap = val;
	}

	public void update() {
		value++;
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

	public int getTick() {
		return value;
	}

	public int getCap() {
		return cap;
	}

}

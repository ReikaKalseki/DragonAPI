package Reika.DragonAPI.Instantiable;

import net.minecraft.nbt.NBTTagCompound;

public class ProgressBar {

	private int tick;
	private int duration;

	private DurationCallback durationCall;

	public ProgressBar(int dur) {
		duration = dur;
	}

	public ProgressBar(DurationCallback call) {
		durationCall = call;
	}

	public boolean tick() {
		return this.tick(1);
	}

	public boolean tick(int amt) {
		this.updateDuration();
		if (tick+amt >= duration) {
			tick = (tick+amt)%duration;
			return true;
		}
		else {
			tick += amt;
			return false;
		}
	}

	public int tickNoRollover() {
		return this.tickNoRollover(1);
	}

	public int tickNoRollover(int amt) {
		this.updateDuration();
		int max = Math.min(amt, duration-tick);
		tick += max;
		return max;
	}

	public boolean isComplete() {
		this.updateDuration();
		return tick >= duration;
	}

	public int getScaled(int len) {
		this.updateDuration();
		return tick*len/duration;
	}

	private void updateDuration() {
		if (durationCall != null) {
			duration = durationCall.getDuration();
		}
	}

	public int getTick() {
		return tick;
	}

	public int getDuration() {
		return duration;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("duration", duration);
		tag.setInteger("tick", tick);
	}

	public void readFromNBT(NBTTagCompound tag) {
		duration = tag.getInteger("duration");
		tick = tag.getInteger("tick");
	}

	public static interface DurationCallback {

		public int getDuration();

	}

}

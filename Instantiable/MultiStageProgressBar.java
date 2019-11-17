package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import Reika.DragonAPI.Instantiable.ProgressBar.DurationCallback;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

public class MultiStageProgressBar {

	private final ArrayList<ProgressBar> bars = new ArrayList();

	public MultiStageProgressBar() {

	}

	public MultiStageProgressBar addBar(int dur) {
		return this.addBar(new ProgressBar(dur));
	}

	public MultiStageProgressBar addBar(DurationCallback b) {
		return this.addBar(new ProgressBar(b));
	}

	public MultiStageProgressBar addBar(ProgressBar b) {
		bars.add(b);
		return this;
	}

	public boolean tick() {
		return this.tick(1);
	}

	public boolean tick(int amt) {
		for (int i = 0; i < bars.size(); i++) {
			ProgressBar b = bars.get(i);
			int ticked = b.tickNoRollover(amt);
			amt -= ticked;
			if (i == bars.size()-1 && b.isComplete())
				return true;
			if (amt <= 0)
				return false;
		}
		return false;
	}

	public int getScaledBar(int slot, int len) {
		return bars.get(slot).getScaled(len);
	}

	public int getTick(int slot) {
		return bars.get(slot).getTick();
	}

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList li = new NBTTagList();
		for (ProgressBar b : bars) {
			NBTTagCompound tag = new NBTTagCompound();
			b.writeToNBT(tag);
			li.appendTag(tag);
		}
		nbt.setTag("bars", li);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		bars.clear();
		NBTTagList li = nbt.getTagList("bars", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			ProgressBar b = new ProgressBar(0);
			b.readFromNBT(tag);
			this.addBar(b);
		}
	}

}

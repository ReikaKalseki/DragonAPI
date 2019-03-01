package Reika.DragonAPI.Instantiable.ModInteract;

import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.ChangeCallback;

import appeng.api.storage.data.IAEItemStack;

public class MEWorkTracker implements ChangeCallback {

	public final int tickRate;

	private long age = 0;
	private boolean hasWork = true;

	public MEWorkTracker() {
		this(200);
	}

	public MEWorkTracker(int r) {
		tickRate = r;
	}

	public void tick() {
		age++;
		hasWork |= age%tickRate == 0;
	}

	public void markDirty() {
		hasWork = true;
	}

	public void reset() {
		hasWork = false;
	}

	public boolean hasWork() {
		return hasWork;
	}

	@Override
	public void onItemChange(IAEItemStack iae) {
		hasWork = true;
	}

}

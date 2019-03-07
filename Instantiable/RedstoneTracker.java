package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class RedstoneTracker {

	private int value;

	public void update(TileEntityBase te) {
		int v = te.getRedstoneOverride();
		if (v != value) {
			te.triggerBlockUpdate();
			ReikaWorldHelper.causeAdjacentUpdates(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
		}
		value = v;
	}

	public int getValue() {
		return value;
	}

}

package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class RedstoneTracker {

	private int value;

	public void update(TileEntityBase te) {
		int last = value;
		value = te.getRedstoneOverride();
		if (last != value) {
			//ReikaJavaLibrary.pConsole(last+" > "+value);
			te.triggerBlockUpdate();
			ReikaWorldHelper.causeAdjacentUpdates(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
		}
	}

	public int getValue() {
		return value;
	}

}

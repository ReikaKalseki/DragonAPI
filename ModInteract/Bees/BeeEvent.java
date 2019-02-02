package Reika.DragonAPI.ModInteract.Bees;

import cpw.mods.fml.common.eventhandler.Event;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;


public class BeeEvent extends Event {

	public final IBee bee;

	public BeeEvent(IBee b) {
		bee = b;
	}

	public static class BeeHouseEvent extends BeeEvent {

		public final IBeeHousing housing;
		public final IBeekeepingLogic logic;

		public BeeHouseEvent(IBeeHousing ibh, IBeekeepingLogic lgc, IBee bee) {
			super(bee);
			housing = ibh;
			logic = lgc;
		}

	}

	public static class BeeSetHealthEvent extends BeeHouseEvent {

		public BeeSetHealthEvent(IBeeHousing te, IBeekeepingLogic lgc, IBee bee) {
			super(te, lgc, bee);
		}

	}

}

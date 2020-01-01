package Reika.DragonAPI.Instantiable.Event;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;


public class MTReloadEvent extends Event {

	public final Phase phase;

	public MTReloadEvent(Phase p) {
		phase = p;
	}

}

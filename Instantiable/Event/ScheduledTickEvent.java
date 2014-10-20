package Reika.DragonAPI.Instantiable.Event;

import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickType;
import cpw.mods.fml.common.eventhandler.Event;

public class ScheduledTickEvent extends Event {

	public final TickType type;
	private final Object[] data;

	public ScheduledTickEvent(TickType tick, Object... obj) {
		type = tick;
		data = obj;
	}

	public Object getData(int i) {
		return data[i];
	}

	public void fire() {
		MinecraftForge.EVENT_BUS.post(this);
	}
}

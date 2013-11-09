package Reika.DragonAPI;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class EventController {

	public static final EventController instance = new EventController();

	private EventController() {

	}


	@ForgeSubscribe
	public void addReikaModel(RenderPlayerEvent evt) {
		RenderPlayer render = evt.renderer;
		EntityPlayer ep = evt.entityPlayer;
		if (ep != null && "Reika_Kalseki".equals(ep.getEntityName())) {

		}
	}
}

package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;


public class GetMouseoverEvent extends Event {

	public final float partialRenderTick;
	public final MovingObjectPosition originalLook;

	public MovingObjectPosition newLook;

	public GetMouseoverEvent(MovingObjectPosition mov, float p) {
		originalLook = mov;
		partialRenderTick = p;
		newLook = mov;
	}

	public static void fire(float ptick) {
		GetMouseoverEvent evt = new GetMouseoverEvent(Minecraft.getMinecraft().objectMouseOver, ptick);
		MinecraftForge.EVENT_BUS.post(evt);
		Minecraft.getMinecraft().objectMouseOver = evt.newLook;
	}

}

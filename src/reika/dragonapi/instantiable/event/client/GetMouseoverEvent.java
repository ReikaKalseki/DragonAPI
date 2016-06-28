/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.event.client;

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

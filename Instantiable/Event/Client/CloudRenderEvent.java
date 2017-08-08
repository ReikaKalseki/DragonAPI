/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class CloudRenderEvent extends Event {

	public final boolean defaultResult;

	public CloudRenderEvent() {
		GameSettings gs = Minecraft.getMinecraft().gameSettings;
		defaultResult = gs.renderDistanceChunks >= 4 && gs.clouds;
	}

	public static boolean fire() {
		CloudRenderEvent evt = new CloudRenderEvent();
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return true;
			case DEFAULT:
				return evt.defaultResult;
			case DENY:
				return false;
		}
		return evt.defaultResult; //never happens
	}
}

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

import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityRenderingLoopEvent extends Event {

	public final int renderPass;
	public final float partialTickTime;

	public EntityRenderingLoopEvent(int pass, float ptick) {
		renderPass = pass;
		partialTickTime = ptick;
	}

	public static void fire(float ptick) {
		MinecraftForge.EVENT_BUS.post(new EntityRenderingLoopEvent(MinecraftForgeClient.getRenderPass(), ptick));
	}

}

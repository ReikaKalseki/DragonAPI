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

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//@HasResult
@Cancelable
@SideOnly(Side.CLIENT)
public class ChunkWorldRenderEvent extends Event {

	/** posX, posY, posZ are block coords of the subchunk origin */
	public final WorldRenderer renderer;
	public final int renderPass;
	public final int defaultGLListID;

	public ChunkWorldRenderEvent(WorldRenderer wr, int pass, int id) {
		renderer = wr;
		renderPass = pass;
		defaultGLListID = id;
	}

	public static int fire(int ret, WorldRenderer wr, int pass) {
		ChunkWorldRenderEvent evt = new ChunkWorldRenderEvent(wr, pass, ret);
		return MinecraftForge.EVENT_BUS.post(evt) ? -1 : ret;
	}

}

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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.PositionEventClient;

import cpw.mods.fml.common.eventhandler.Cancelable;

//@HasResult
@Cancelable
public class RenderBlockAtPosEvent extends PositionEventClient {

	public final WorldRenderer renderer;

	public final RenderBlocks render;

	public final Block block;

	public final int renderPass;

	public boolean continueRendering = false;

	public RenderBlockAtPosEvent(WorldRenderer wr, IBlockAccess iba, RenderBlocks rb, Block b, int x, int y, int z, int pass) {
		super(iba, x, y, z);
		renderer = wr;

		render = rb;

		block = b;

		renderPass = pass;
	}

	@Override
	public void setCanceled(boolean cancel) {
		super.setCanceled(cancel);
		continueRendering = !cancel;
	}

	public static boolean fire(RenderBlocks rb, Block b, int x, int y, int z, WorldRenderer wr, int pass) {
		RenderBlockAtPosEvent evt = new RenderBlockAtPosEvent(wr, rb.blockAccess, rb, b, x, y, z, pass);
		boolean flag = !MinecraftForge.EVENT_BUS.post(evt);
		if (flag)
			flag &= rb.renderBlockByRenderType(b, x, y, z);
		/*
		switch(evt.getResult()) {
			case ALLOW:
				return true;
			case DENY:
				return false;
			case DEFAULT:
			default:
				return flag;
		}
		 */
		//ReikaJavaLibrary.pConsole(b, !flag && evt.continueRendering);
		return flag || evt.continueRendering;
	}

}

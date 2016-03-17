package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

//@HasResult
@Cancelable
public class RenderBlockAtPosEvent extends Event {

	public final WorldRenderer renderer;

	public final IBlockAccess world;
	public final RenderBlocks render;

	public final Block block;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	public final int renderPass;

	public RenderBlockAtPosEvent(WorldRenderer wr, IBlockAccess iba, RenderBlocks rb, Block b, int x, int y, int z, int pass) {
		renderer = wr;

		world = iba;
		render = rb;

		block = b;
		xCoord = x;
		yCoord = y;
		zCoord = z;

		renderPass = pass;
	}

	public static boolean fire(WorldRenderer wr, RenderBlocks rb, Block b, int x, int y, int z, int pass) {
		Event evt = new RenderBlockAtPosEvent(wr, rb.blockAccess, rb, b, x, y, z, pass);
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
		return flag;
	}

}

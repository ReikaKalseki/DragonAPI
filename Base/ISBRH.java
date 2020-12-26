package Reika.DragonAPI.Base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;


public abstract class ISBRH implements ISimpleBlockRenderingHandler {

	protected final ForgeDirection[] dirs = ForgeDirection.values();

	protected final Random rand = new Random();

	public final int renderID;
	protected int renderPass;

	protected ISBRH(int id) {
		renderID = id;
	}

	@Override
	public final int getRenderId() {
		return renderID;
	}

	protected final long calcSeed(int x, int y, int z) {
		return ChunkCoordIntPair.chunkXZ2Int(x, z) ^ y;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks rb) {}

	public final void setRenderPass(int pass) {
		renderPass = pass;
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.rendering;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import reika.dragonapi.base.BaseBlockRenderer;

public abstract class WorldPipingRenderer extends BaseBlockRenderer {

	public WorldPipingRenderer(int ID) {
		super(ID);
	}

	@Override
	public final void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks rb) {

	}

	protected abstract void renderFace(TileEntity tile, int x, int y, int z, ForgeDirection dir, double size);

	@Override
	public final boolean shouldRender3DInInventory(int model) {
		return true;
	}
}

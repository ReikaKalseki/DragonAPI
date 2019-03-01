/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Base.BaseBlockRenderer;

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

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

public class BlockModelRenderer {

	private static RenderBlocks rb = new RenderBlocks();

	public static class ModelBlockInterface {

		public double minX;
		public double minY;
		public double minZ;
		public double maxX;
		public double maxY;
		public double maxZ;

		public Block baseBlock = Block.sand;

		public Icon texture = null;

		public Icon getBlockTextureFromSide(int i) {
			if (texture == null)
				return baseBlock.getBlockTextureFromSide(i);
			else
				return texture;
		}

		public float getBlockBrightness(IBlockAccess iblockaccess, int i, int j, int k) {
			return baseBlock.getBlockBrightness(iblockaccess, i, j, k);
		}
	}

	public static void renderBlock(ModelBlockInterface block, IBlockAccess blockAccess, int i, int j, int k, boolean doLight, boolean doTessellating) {
		float f = 0.5F;
		float f1 = 1.0F;
		float f2 = 0.8F;
		float f3 = 0.6F;

		rb.renderMaxX = block.maxX;
		rb.renderMinX = block.minX;
		rb.renderMaxY = block.maxY;
		rb.renderMinY = block.minY;
		rb.renderMaxZ = block.maxZ;
		rb.renderMinZ = block.minZ;
		rb.enableAO = false;

		Tessellator v5 = Tessellator.instance;

		if (doTessellating) {
			v5.startDrawingQuads();
		}

		float f4 = 0, f5 = 0;

		if (doLight) {
			f4 = block.getBlockBrightness(blockAccess, i, j, k);
			f5 = block.getBlockBrightness(blockAccess, i, j, k);
			if (f5 < f4) {
				f5 = f4;
			}
			v5.setColorOpaque_F(f * f5, f * f5, f * f5);
		}

		rb.renderFaceYNeg(null, 0, 0, 0, block.getBlockTextureFromSide(0));

		if (doLight) {
			f5 = block.getBlockBrightness(blockAccess, i, j, k);
			if (f5 < f4) {
				f5 = f4;
			}
			v5.setColorOpaque_F(f1 * f5, f1 * f5, f1 * f5);
		}

		rb.renderFaceYPos(null, 0, 0, 0, block.getBlockTextureFromSide(1));

		if (doLight) {
			f5 = block.getBlockBrightness(blockAccess, i, j, k);
			if (f5 < f4) {
				f5 = f4;
			}
			v5.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
		}

		rb.renderFaceZNeg(null, 0, 0, 0, block.getBlockTextureFromSide(2));

		if (doLight) {
			f5 = block.getBlockBrightness(blockAccess, i, j, k);
			if (f5 < f4) {
				f5 = f4;
			}
			v5.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
		}

		rb.renderFaceZPos(null, 0, 0, 0, block.getBlockTextureFromSide(3));

		if (doLight) {
			f5 = block.getBlockBrightness(blockAccess, i, j, k);
			if (f5 < f4) {
				f5 = f4;
			}
			v5.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
		}

		rb.renderFaceXNeg(null, 0, 0, 0, block.getBlockTextureFromSide(4));

		if (doLight) {
			f5 = block.getBlockBrightness(blockAccess, i, j, k);
			if (f5 < f4) {
				f5 = f4;
			}
			v5.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
		}

		rb.renderFaceXPos(null, 0, 0, 0, block.getBlockTextureFromSide(5));

		if (doTessellating) {
			v5.draw();
		}
	}
}

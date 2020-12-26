/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BaseBlockRenderer extends ISBRH {

	private IBlockAccess world;
	private Block b;
	private int x;
	private int y;
	private int z;
	private RenderBlocks rb;

	public BaseBlockRenderer(int ID) {
		super(ID);
	}

	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		this.setParams(world, x, y, z, block, renderer);
		return true;
	}

	protected void setParams(IBlockAccess world2, int x2, int y2, int z2, Block block, RenderBlocks renderer) {
		b = block;
		world = world2;
		x = x2;
		y = y2;
		z = z2;
		rb = renderer;
	}

	protected final void faceBrightness(ForgeDirection dir, Tessellator v5) {
		this.faceBrightness(dir, v5, 1);
	}

	protected final void faceBrightnessColor(ForgeDirection dir, Tessellator v5, float red, float green, float blue) {
		float f3 = 0.5F;
		float f4 = 1.0F;
		float f5 = 0.8F;
		float f6 = 0.6F;

		float f7 = f4;
		float f8 = f4;
		float f9 = f4;

		float f10 = f3;
		float f11 = f5;
		float f12 = f6;
		float f13 = f3;
		float f14 = f5;
		float f15 = f6;
		float f16 = f3;
		float f17 = f5;
		float f18 = f6;

		f10 = f3;
		f11 = f5;
		f12 = f6;
		f13 = f3;
		f14 = f5;
		f15 = f6;
		f16 = f3;
		f17 = f5;
		f18 = f6;

		int l = b.getMixedBrightnessForBlock(world, x, y, z);

		switch(dir.getOpposite()) {
			case UP:
				v5.setBrightness(rb.renderMaxY < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y+1, z));
				v5.setColorOpaque_F(f7*red, f8*green, f9*blue);
				break;
			case DOWN:
				v5.setBrightness(rb.renderMinY > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y-1, z));
				v5.setColorOpaque_F(f10*red, f13*green, f16*blue);
				break;
			case NORTH:
				v5.setBrightness(rb.renderMinZ > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z - 1));
				v5.setColorOpaque_F(f11*red, f14*green, f17*blue);
				break;
			case SOUTH:
				v5.setBrightness(rb.renderMaxZ < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z + 1));
				v5.setColorOpaque_F(f11*red, f14*green, f17*blue);
				break;
			case WEST:
				v5.setBrightness(rb.renderMinX > 0.0D ? l : b.getMixedBrightnessForBlock(world, x - 1, y, z));
				v5.setColorOpaque_F(f12*red, f15*green, f18*blue);
				break;
			case EAST:
				v5.setBrightness(rb.renderMaxX < 1.0D ? l : b.getMixedBrightnessForBlock(world, x + 1, y, z));
				v5.setColorOpaque_F(f12*red, f15*green, f18*blue);
				break;
			default:
				break;
		}
	}

	protected final void faceBrightness(ForgeDirection dir, Tessellator v5, float multiplier) {
		float f3 = 0.5F;
		float f4 = 1.0F;
		float f5 = 0.8F;
		float f6 = 0.6F;

		float f7 = f4*multiplier;
		float f8 = f4*multiplier;
		float f9 = f4*multiplier;

		float f10 = f3;
		float f11 = f5;
		float f12 = f6;
		float f13 = f3;
		float f14 = f5;
		float f15 = f6;
		float f16 = f3;
		float f17 = f5;
		float f18 = f6;

		f10 = f3*multiplier;
		f11 = f5*multiplier;
		f12 = f6*multiplier;
		f13 = f3*multiplier;
		f14 = f5*multiplier;
		f15 = f6*multiplier;
		f16 = f3*multiplier;
		f17 = f5*multiplier;
		f18 = f6*multiplier;

		int l = b.getMixedBrightnessForBlock(world, x, y, z);

		switch(dir.getOpposite()) {
			case UP:
				v5.setBrightness(!b.isOpaqueCube() ? l : b.getMixedBrightnessForBlock(world, x, y+1, z));
				v5.setColorOpaque_F(f7, f8, f9);
				break;
			case DOWN:
				v5.setBrightness(!b.isOpaqueCube() ? l : b.getMixedBrightnessForBlock(world, x, y-1, z));
				v5.setColorOpaque_F(f10, f13, f16);
				break;
			case NORTH:
				v5.setBrightness(!b.isOpaqueCube() ? l : b.getMixedBrightnessForBlock(world, x, y, z - 1));
				v5.setColorOpaque_F(f11, f14, f17);
				break;
			case SOUTH:
				v5.setBrightness(!b.isOpaqueCube() ? l : b.getMixedBrightnessForBlock(world, x, y, z + 1));
				v5.setColorOpaque_F(f11, f14, f17);
				break;
			case WEST:
				v5.setBrightness(!b.isOpaqueCube() ? l : b.getMixedBrightnessForBlock(world, x - 1, y, z));
				v5.setColorOpaque_F(f12, f15, f18);
				break;
			case EAST:
				v5.setBrightness(!b.isOpaqueCube() ? l : b.getMixedBrightnessForBlock(world, x + 1, y, z));
				v5.setColorOpaque_F(f12, f15, f18);
				break;
			default:
				break;
		}
	}

	protected final void faceBrightnessNoWorld(ForgeDirection dir, Tessellator v5, float r, float g, float b) {
		float f3 = 0.5F;
		float f4 = 1.0F;
		float f5 = 0.8F;
		float f6 = 0.6F;

		float f7 = f4;
		float f8 = f4;
		float f9 = f4;

		float f10 = f3;
		float f11 = f5;
		float f12 = f6;
		float f13 = f3;
		float f14 = f5;
		float f15 = f6;
		float f16 = f3;
		float f17 = f5;
		float f18 = f6;

		f10 = f3;
		f11 = f5;
		f12 = f6;
		f13 = f3;
		f14 = f5;
		f15 = f6;
		f16 = f3;
		f17 = f5;
		f18 = f6;

		switch(dir.getOpposite()) {
			case UP:
				//v5.setBrightness(240);
				v5.setColorOpaque_F(f7*r, f8*g, f9*b);
				break;
			case DOWN:
				//v5.setBrightness(rb.renderMinY > 0.0D ? l : 1);
				v5.setColorOpaque_F(f10*r, f13*g, f16*b);
				break;
			case NORTH:
				//v5.setBrightness(rb.renderMinZ > 0.0D ? l : 1);
				v5.setColorOpaque_F(f11*r, f14*g, f17*b);
				break;
			case SOUTH:
				//v5.setBrightness(rb.renderMaxZ < 1.0D ? l : 1);
				v5.setColorOpaque_F(f11*r, f14*g, f17*b);
				break;
			case WEST:
				//v5.setBrightness(rb.renderMinX > 0.0D ? l : 1);
				v5.setColorOpaque_F(f12*r, f15*g, f18*b);
				break;
			case EAST:
				//v5.setBrightness(rb.renderMaxX < 1.0D ? l : 1);
				v5.setColorOpaque_F(f12*r, f15*g, f18*b);
				break;
			default:
				break;
		}
	}

	protected final void faceBrightnessNoWorld(ForgeDirection dir, Tessellator v5, float multiplier) {
		float f3 = 0.5F;
		float f4 = 1.0F;
		float f5 = 0.8F;
		float f6 = 0.6F;

		float f7 = f4*multiplier;
		float f8 = f4*multiplier;
		float f9 = f4*multiplier;

		float f10 = f3;
		float f11 = f5;
		float f12 = f6;
		float f13 = f3;
		float f14 = f5;
		float f15 = f6;
		float f16 = f3;
		float f17 = f5;
		float f18 = f6;

		f10 = f3*multiplier;
		f11 = f5*multiplier;
		f12 = f6*multiplier;
		f13 = f3*multiplier;
		f14 = f5*multiplier;
		f15 = f6*multiplier;
		f16 = f3*multiplier;
		f17 = f5*multiplier;
		f18 = f6*multiplier;

		switch(dir.getOpposite()) {
			case UP:
				//v5.setBrightness(240);
				v5.setColorOpaque_F(f7, f8, f9);
				break;
			case DOWN:
				//v5.setBrightness(rb.renderMinY > 0.0D ? l : 1);
				v5.setColorOpaque_F(f10, f13, f16);
				break;
			case NORTH:
				//v5.setBrightness(rb.renderMinZ > 0.0D ? l : 1);
				v5.setColorOpaque_F(f11, f14, f17);
				break;
			case SOUTH:
				//v5.setBrightness(rb.renderMaxZ < 1.0D ? l : 1);
				v5.setColorOpaque_F(f11, f14, f17);
				break;
			case WEST:
				//v5.setBrightness(rb.renderMinX > 0.0D ? l : 1);
				v5.setColorOpaque_F(f12, f15, f18);
				break;
			case EAST:
				//v5.setBrightness(rb.renderMaxX < 1.0D ? l : 1);
				v5.setColorOpaque_F(f12, f15, f18);
				break;
			default:
				break;
		}
	}

}

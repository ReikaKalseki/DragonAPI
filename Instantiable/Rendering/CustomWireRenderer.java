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
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Interfaces.Block.WireBlock;

public class CustomWireRenderer implements ISBRH {

	private final int renderID;

	private static final ForgeDirection[] dirs = ForgeDirection.values();

	public CustomWireRenderer(int ID) {
		renderID = ID;
	}

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelID, RenderBlocks renderer) {
		DragonAPICore.logError("This item for block "+b+" should have a placer item, not be obtainable directly!");
		// should have placer item
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
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
		if (b instanceof WireBlock) {
			WireBlock w = (WireBlock)b;
			boolean alone = true;
			for (int i = 2; i < 6; i++)
				if (w.isDirectlyConnectedTo(world, x, y, z, i))
					alone = false;
			int power = w.getPowerState(world, x, y, z);
			IIcon ico = rb.getIconSafe(w.getBaseTexture());
			IIcon over = rb.getIconSafe(w.getConnectedSideOverlay());
			Tessellator v5 = Tessellator.instance;
			int l = b.getMixedBrightnessForBlock(world, x, y, z);
			v5.setBrightness(rb.renderMaxY < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y+1, z));
			float[] color = {1, 1, 1};
			color[0] = w.getColor().getRed()/255F;
			color[1] = w.getColor().getGreen()/255F;
			color[2] = w.getColor().getBlue()/255F;

			for (int i = 0; i < 3; i++) {
				color[i] *= 0.25F+power/12F;
			}

			v5.setColorOpaque_F(f7*color[0], f8*color[1], f9*color[2]);
			v5.addTranslation(x, y, z);
			double d = 0.001;

			v5.addVertexWithUV(0, d, 1, ico.getMinU(), ico.getMaxV());
			v5.addVertexWithUV(1, d, 1, ico.getMaxU(), ico.getMaxV());
			v5.addVertexWithUV(1, d, 0, ico.getMaxU(), ico.getMinV());
			v5.addVertexWithUV(0, d, 0, ico.getMinU(), ico.getMinV());

			for (int i = 2; i < 6; i++) {
				d += 0.001;
				double d2 = 0.001;
				ForgeDirection dir = dirs[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				if (w.isDirectlyConnectedTo(world, x, y, z, dir.ordinal()) || (!alone && w.isTerminus(world, x, y, z, dir.getOpposite().ordinal()))) {
					boolean up = w.drawWireUp(world, x, y, z, i);
					switch(dir) {
						case EAST:
							v5.addVertexWithUV(1, d, 1, over.getMinU(), over.getMaxV());
							v5.addVertexWithUV(1, d, 0, over.getMaxU(), over.getMaxV());
							v5.addVertexWithUV(0, d, 0, over.getMaxU(), over.getMinV());
							v5.addVertexWithUV(0, d, 1, over.getMinU(), over.getMinV());

							if (up) {
								v5.addVertexWithUV(1-d2, 1, 1, over.getMinU(), over.getMaxV());
								v5.addVertexWithUV(1-d2, 1, 0, over.getMaxU(), over.getMaxV());
								v5.addVertexWithUV(1-d2, 0, 0, over.getMaxU(), over.getMinV());
								v5.addVertexWithUV(1-d2, 0, 1, over.getMinU(), over.getMinV());

								v5.addVertexWithUV(1-d2, 1, 1, over.getMinU(), over.getMinV());
								v5.addVertexWithUV(1-d2, 1, 0, over.getMaxU(), over.getMinV());
								v5.addVertexWithUV(1-d2, 0, 0, over.getMaxU(), over.getMaxV());
								v5.addVertexWithUV(1-d2, 0, 1, over.getMinU(), over.getMaxV());

								v5.addVertexWithUV(1-d2, 1, 1, ico.getMinU(), ico.getMinV());
								v5.addVertexWithUV(1-d2, 1, 0, ico.getMaxU(), ico.getMinV());
								v5.addVertexWithUV(1-d2, 0, 0, ico.getMaxU(), ico.getMaxV());
								v5.addVertexWithUV(1-d2, 0, 1, ico.getMinU(), ico.getMaxV());
							}
							break;
						case NORTH: //done
							v5.addVertexWithUV(0, d, 1, over.getMinU(), over.getMinV());
							v5.addVertexWithUV(1, d, 1, over.getMaxU(), over.getMinV());
							v5.addVertexWithUV(1, d, 0, over.getMaxU(), over.getMaxV());
							v5.addVertexWithUV(0, d, 0, over.getMinU(), over.getMaxV());

							if (up) {
								v5.addVertexWithUV(1, 1, d2, over.getMinU(), over.getMaxV());
								v5.addVertexWithUV(0, 1, d2, over.getMaxU(), over.getMaxV());
								v5.addVertexWithUV(0, 0, d2, over.getMaxU(), over.getMinV());
								v5.addVertexWithUV(1, 0, d2, over.getMinU(), over.getMinV());

								v5.addVertexWithUV(1, 1, d2, over.getMinU(), over.getMinV());
								v5.addVertexWithUV(0, 1, d2, over.getMaxU(), over.getMinV());
								v5.addVertexWithUV(0, 0, d2, over.getMaxU(), over.getMaxV());
								v5.addVertexWithUV(1, 0, d2, over.getMinU(), over.getMaxV());

								v5.addVertexWithUV(1, 1, d2, ico.getMinU(), ico.getMinV());
								v5.addVertexWithUV(0, 1, d2, ico.getMaxU(), ico.getMinV());
								v5.addVertexWithUV(0, 0, d2, ico.getMaxU(), ico.getMaxV());
								v5.addVertexWithUV(1, 0, d2, ico.getMinU(), ico.getMaxV());
							}
							break;
						case SOUTH: //done
							v5.addVertexWithUV(0, d, 1, over.getMinU(), over.getMaxV());
							v5.addVertexWithUV(1, d, 1, over.getMaxU(), over.getMaxV());
							v5.addVertexWithUV(1, d, 0, over.getMaxU(), over.getMinV());
							v5.addVertexWithUV(0, d, 0, over.getMinU(), over.getMinV());

							if (up) {
								v5.addVertexWithUV(1, 0, 1-d2, over.getMinU(), over.getMinV());
								v5.addVertexWithUV(0, 0, 1-d2, over.getMaxU(), over.getMinV());
								v5.addVertexWithUV(0, 1, 1-d2, over.getMaxU(), over.getMaxV());
								v5.addVertexWithUV(1, 1, 1-d2, over.getMinU(), over.getMaxV());

								v5.addVertexWithUV(1, 0, 1-d2, over.getMinU(), over.getMaxV());
								v5.addVertexWithUV(0, 0, 1-d2, over.getMaxU(), over.getMaxV());
								v5.addVertexWithUV(0, 1, 1-d2, over.getMaxU(), over.getMinV());
								v5.addVertexWithUV(1, 1, 1-d2, over.getMinU(), over.getMinV());

								v5.addVertexWithUV(1, 0, 1-d2, ico.getMinU(), ico.getMaxV());
								v5.addVertexWithUV(0, 0, 1-d2, ico.getMaxU(), ico.getMaxV());
								v5.addVertexWithUV(0, 1, 1-d2, ico.getMaxU(), ico.getMinV());
								v5.addVertexWithUV(1, 1, 1-d2, ico.getMinU(), ico.getMinV());
							}
							break;
						case WEST:
							v5.addVertexWithUV(1, d, 1, over.getMinU(), over.getMinV());
							v5.addVertexWithUV(1, d, 0, over.getMaxU(), over.getMinV());
							v5.addVertexWithUV(0, d, 0, over.getMaxU(), over.getMaxV());
							v5.addVertexWithUV(0, d, 1, over.getMinU(), over.getMaxV());

							if (up) {
								v5.addVertexWithUV(d2, 0, 1, over.getMinU(), over.getMinV());
								v5.addVertexWithUV(d2, 0, 0, over.getMaxU(), over.getMinV());
								v5.addVertexWithUV(d2, 1, 0, over.getMaxU(), over.getMaxV());
								v5.addVertexWithUV(d2, 1, 1, over.getMinU(), over.getMaxV());

								v5.addVertexWithUV(d2, 0, 1, over.getMinU(), over.getMaxV());
								v5.addVertexWithUV(d2, 0, 0, over.getMaxU(), over.getMaxV());
								v5.addVertexWithUV(d2, 1, 0, over.getMaxU(), over.getMinV());
								v5.addVertexWithUV(d2, 1, 1, over.getMinU(), over.getMinV());

								v5.addVertexWithUV(d2, 0, 1, ico.getMinU(), ico.getMaxV());
								v5.addVertexWithUV(d2, 0, 0, ico.getMaxU(), ico.getMaxV());
								v5.addVertexWithUV(d2, 1, 0, ico.getMaxU(), ico.getMinV());
								v5.addVertexWithUV(d2, 1, 1, ico.getMinU(), ico.getMinV());
							}
							break;
						default:
							break;
					}
				}
			}

			v5.addTranslation(-x, -y, -z);
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int model) {
		return false;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}

}

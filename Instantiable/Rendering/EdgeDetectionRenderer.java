/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;


public class EdgeDetectionRenderer {

	public final BlockKey block;
	//private final float[][] icons = new float[4][4];
	private IIcon[] icons = new IIcon[4];

	public EdgeDetectionRenderer(Block b) {
		this(new BlockKey(b));
	}

	public EdgeDetectionRenderer(Block b, int meta) {
		this(new BlockKey(b, meta));
	}

	public EdgeDetectionRenderer(BlockKey bk) {
		if (bk == null || bk.blockID == null)
			throw new MisuseException("You cannot use a null block in the edge detection renderer!");
		block = bk;
	}

	public EdgeDetectionRenderer setIcons(IIcon[] icons) {
		if (icons.length != 4)
			throw new IllegalArgumentException("You must provide only four icons!");
		/*
		for (int i = 0; i < icons.length; i++) {
			IIcon ico = icons[i];
			this.icons[i] = new float[4];
			this.icons[i][0] = ico.getMinU();
			this.icons[i][1] = ico.getMinV();
			this.icons[i][2] = ico.getMaxU();
			this.icons[i][3] = ico.getMaxV();
		}*/
		this.icons = icons;
		return this;
	}

	public void renderBlock(IBlockAccess world, int x, int y, int z, RenderBlocks rb) {
		for (int i = 0; i < 6; i++) {
			if (this.canRenderFace(world, x, y, z))
				this.renderFace(world, x, y, z, rb, ForgeDirection.VALID_DIRECTIONS[i]);
		}
	}

	private boolean canRenderFace(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	/** Render edge unless has block on that edge, and that block can render that face */
	private void renderFace(IBlockAccess world, int x, int y, int z, RenderBlocks rb, ForgeDirection face) {
		Tessellator v5 = Tessellator.instance;
		double o = 0.005;

		boolean draw = false;
		if (!v5.isDrawing) {
			v5.startDrawingQuads();
			draw = true;
		}

		BlockKey bk;

		switch(face) {
			case DOWN:

				bk = BlockKey.getAt(world, x+1, y, z);
				if (!bk.equals(block) || !world.getBlock(x+1, y, z).shouldSideBeRendered(world, x+1, y-1, z, 0)) {
					//render bottom east
					//v5.addVertexWithUV(1, 0, 0);
					//v5.addVertexWithUV(1, 0, 1);

					IIcon ico = icons[2];
					v5.addVertexWithUV(0, -o, 0, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(1, -o, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1, -o, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(0, -o, 1, ico.getMinU(), ico.getMaxV());
				}

				bk = BlockKey.getAt(world, x-1, y, z);
				if (!bk.equals(block) || !world.getBlock(x-1, y, z).shouldSideBeRendered(world, x-1, y-1, z, 0)) {
					//render bottom west
					//v5.addVertexWithUV(0, 0, 0);
					//v5.addVertexWithUV(0, 0, 1);

					IIcon ico = icons[0];
					v5.addVertexWithUV(0, -o, 0, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(1, -o, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1, -o, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(0, -o, 1, ico.getMinU(), ico.getMaxV());
				}

				bk = BlockKey.getAt(world, x, y, z+1);
				if (!bk.equals(block) || !world.getBlock(x, y, z+1).shouldSideBeRendered(world, x, y-1, z+1, 0)) {
					//render bottom south
					//v5.addVertexWithUV(0, 0, 1);
					//v5.addVertexWithUV(1, 0, 1);

					IIcon ico = icons[3];
					v5.addVertexWithUV(0, -o, 0, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(1, -o, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1, -o, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(0, -o, 1, ico.getMinU(), ico.getMaxV());
				}

				bk = BlockKey.getAt(world, x, y, z-1);
				if (!bk.equals(block) || !world.getBlock(x, y, z-1).shouldSideBeRendered(world, x, y-1, z-1, 0)) {
					//render bottom north
					//v5.addVertexWithUV(0, 0, 0);
					//v5.addVertexWithUV(1, 0, 0);

					IIcon ico = icons[1];
					v5.addVertexWithUV(0, -o, 0, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(1, -o, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1, -o, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(0, -o, 1, ico.getMinU(), ico.getMaxV());
				}

				break;
			case UP:

				bk = BlockKey.getAt(world, x+1, y, z);
				if (!bk.equals(block) || !world.getBlock(x+1, y, z).shouldSideBeRendered(world, x+1, y+1, z, 0)) {
					//render top east
					//v5.addVertexWithUV(1, 1, 0);
					//v5.addVertexWithUV(1, 1, 1);

					IIcon ico = icons[2];
					v5.addVertexWithUV(0, 1+o, 1, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1+o, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1+o, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(0, 1+o, 0, ico.getMinU(), ico.getMinV());
				}

				bk = BlockKey.getAt(world, x-1, y, z);
				if (!bk.equals(block) || !world.getBlock(x-1, y, z).shouldSideBeRendered(world, x-1, y+1, z, 0)) {
					//render top west
					//v5.addVertexWithUV(0, 1, 0);
					//v5.addVertexWithUV(0, 1, 1);

					IIcon ico = icons[0];
					v5.addVertexWithUV(0, 1+o, 1, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1+o, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1+o, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(0, 1+o, 0, ico.getMinU(), ico.getMinV());
				}

				bk = BlockKey.getAt(world, x, y, z+1);
				if (!bk.equals(block) || !world.getBlock(x, y, z+1).shouldSideBeRendered(world, x, y+1, z+1, 0)) {
					//render top south
					//v5.addVertexWithUV(0, 1, 1);
					//v5.addVertexWithUV(1, 1, 1);

					IIcon ico = icons[3];
					v5.addVertexWithUV(0, 1+o, 1, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1+o, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1+o, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(0, 1+o, 0, ico.getMinU(), ico.getMinV());
				}

				bk = BlockKey.getAt(world, x, y, z-1);
				if (!bk.equals(block) || !world.getBlock(x, y, z-1).shouldSideBeRendered(world, x, y+1, z-1, 0)) {
					//render top north
					//v5.addVertexWithUV(0, 1, 0);
					//v5.addVertexWithUV(1, 1, 0);

					IIcon ico = icons[1];
					v5.addVertexWithUV(0, 1+o, 1, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1+o, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1+o, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(0, 1+o, 0, ico.getMinU(), ico.getMinV());
				}

				break;

			case EAST:

				bk = BlockKey.getAt(world, x, y+1, z);
				if (!bk.equals(block) || !world.getBlock(x, y+1, z).shouldSideBeRendered(world, x+1, y+1, z, 0)) {
					IIcon ico = icons[2];
					v5.addVertexWithUV(1+o, 0, 0, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(1+o, 1, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1+o, 1, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1+o, 0, 1, ico.getMinU(), ico.getMaxV());
				}

				bk = BlockKey.getAt(world, x, y-1, z);
				if (!bk.equals(block) || !world.getBlock(x, y-1, z).shouldSideBeRendered(world, x+1, y-1, z, 0)) {
					IIcon ico = icons[0];
					v5.addVertexWithUV(1+o, 0, 0, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(1+o, 1, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1+o, 1, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1+o, 0, 1, ico.getMinU(), ico.getMaxV());
				}

				bk = BlockKey.getAt(world, x, y, z+1);
				if (!bk.equals(block) || !world.getBlock(x, y, z+1).shouldSideBeRendered(world, x+1, y, z+1, 0)) {
					IIcon ico = icons[3];
					v5.addVertexWithUV(1+o, 0, 0, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(1+o, 1, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1+o, 1, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1+o, 0, 1, ico.getMinU(), ico.getMaxV());
				}

				bk = BlockKey.getAt(world, x, y, z-1);
				if (!bk.equals(block) || !world.getBlock(x, y, z-1).shouldSideBeRendered(world, x+1, y, z-1, 0)) {
					IIcon ico = icons[1];
					v5.addVertexWithUV(1+o, 0, 0, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(1+o, 1, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1+o, 1, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1+o, 0, 1, ico.getMinU(), ico.getMaxV());
				}

				break;
			case WEST:

				bk = BlockKey.getAt(world, x, y+1, z);
				if (!bk.equals(block) || !world.getBlock(x, y+1, z).shouldSideBeRendered(world, x-1, y+1, z, 0)) {
					IIcon ico = icons[2];
					v5.addVertexWithUV(-o, 0, 1, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(-o, 1, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(-o, 1, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(-o, 0, 0, ico.getMinU(), ico.getMinV());
				}

				bk = BlockKey.getAt(world, x, y-1, z);
				if (!bk.equals(block) || !world.getBlock(x, y-1, z).shouldSideBeRendered(world, x-1, y-1, z, 0)) {
					IIcon ico = icons[0];
					v5.addVertexWithUV(-o, 0, 1, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(-o, 1, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(-o, 1, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(-o, 0, 0, ico.getMinU(), ico.getMinV());
				}

				bk = BlockKey.getAt(world, x, y, z+1);
				if (!bk.equals(block) || !world.getBlock(x, y, z+1).shouldSideBeRendered(world, x-1, y, z+1, 0)) {
					IIcon ico = icons[3];
					v5.addVertexWithUV(-o, 0, 1, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(-o, 1, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(-o, 1, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(-o, 0, 0, ico.getMinU(), ico.getMinV());
				}

				bk = BlockKey.getAt(world, x, y, z-1);
				if (!bk.equals(block) || !world.getBlock(x, y, z-1).shouldSideBeRendered(world, x-1, y, z-1, 0)) {
					IIcon ico = icons[1];
					v5.addVertexWithUV(-o, 0, 1, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(-o, 1, 1, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(-o, 1, 0, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(-o, 0, 0, ico.getMinU(), ico.getMinV());
				}

				break;

			case NORTH:

				bk = BlockKey.getAt(world, x, y+1, z);
				if (!bk.equals(block) || !world.getBlock(x, y+1, z).shouldSideBeRendered(world, x, y+1, z-1, 0)) {
					IIcon ico = icons[2];
					v5.addVertexWithUV(0, 0, -o, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(0, 1, -o, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1, 1, -o, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1, 0, -o, ico.getMinU(), ico.getMaxV());
				}

				bk = BlockKey.getAt(world, x, y-1, z);
				if (!bk.equals(block) || !world.getBlock(x, y-1, z).shouldSideBeRendered(world, x, y-1, z-1, 0)) {
					IIcon ico = icons[0];
					v5.addVertexWithUV(0, 0, -o, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(0, 1, -o, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1, 1, -o, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1, 0, -o, ico.getMinU(), ico.getMaxV());
				}

				bk = BlockKey.getAt(world, x+1, y, z);
				if (!bk.equals(block) || !world.getBlock(x+1, y, z).shouldSideBeRendered(world, x+1, y, z-1, 0)) {
					IIcon ico = icons[3];
					v5.addVertexWithUV(0, 0, -o, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(0, 1, -o, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1, 1, -o, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1, 0, -o, ico.getMinU(), ico.getMaxV());
				}

				bk = BlockKey.getAt(world, x-1, y, z);
				if (!bk.equals(block) || !world.getBlock(x-1, y, z).shouldSideBeRendered(world, x-1, y, z-1, 0)) {
					IIcon ico = icons[1];
					v5.addVertexWithUV(0, 0, -o, ico.getMinU(), ico.getMinV());
					v5.addVertexWithUV(0, 1, -o, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(1, 1, -o, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(1, 0, -o, ico.getMinU(), ico.getMaxV());
				}

				break;
			case SOUTH:

				bk = BlockKey.getAt(world, x, y+1, z);
				if (!bk.equals(block) || !world.getBlock(x, y+1, z).shouldSideBeRendered(world, x, y+1, z+1, 0)) {
					IIcon ico = icons[2];
					v5.addVertexWithUV(1, 0, 1+o, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1, 1+o, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(0, 1, 1+o, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(0, 0, 1+o, ico.getMinU(), ico.getMinV());
				}

				bk = BlockKey.getAt(world, x, y-1, z);
				if (!bk.equals(block) || !world.getBlock(x, y-1, z).shouldSideBeRendered(world, x, y-1, z+1, 0)) {
					IIcon ico = icons[0];
					v5.addVertexWithUV(1, 0, 1+o, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1, 1+o, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(0, 1, 1+o, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(0, 0, 1+o, ico.getMinU(), ico.getMinV());
				}

				bk = BlockKey.getAt(world, x+1, y, z);
				if (!bk.equals(block) || !world.getBlock(x+1, y, z).shouldSideBeRendered(world, x+1, y, z+1, 0)) {
					IIcon ico = icons[3];
					v5.addVertexWithUV(1, 0, 1+o, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1, 1+o, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(0, 1, 1+o, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(0, 0, 1+o, ico.getMinU(), ico.getMinV());
				}

				bk = BlockKey.getAt(world, x-1, y, z);
				if (!bk.equals(block) || !world.getBlock(x-1, y, z).shouldSideBeRendered(world, x-1, y, z+1, 0)) {
					IIcon ico = icons[1];
					v5.addVertexWithUV(1, 0, 1+o, ico.getMinU(), ico.getMaxV());
					v5.addVertexWithUV(1, 1, 1+o, ico.getMaxU(), ico.getMaxV());
					v5.addVertexWithUV(0, 1, 1+o, ico.getMaxU(), ico.getMinV());
					v5.addVertexWithUV(0, 0, 1+o, ico.getMinU(), ico.getMinV());
				}

				break;
			default:
				break;
		}

		if (draw)
			v5.draw();
	}

}

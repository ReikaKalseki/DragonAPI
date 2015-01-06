/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Instantiable.GUI.InWorldGui;

public class BlockInWorldGui extends Block {

	public BlockInWorldGui(Material mat) {
		super(mat);

		this.setLightOpacity(0);
		this.setBlockUnbreakable();
		this.setResistance(60000F);
		this.setStepSound(soundTypeGlass);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityInWorldGui();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ep) {

	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		TileEntityInWorldGui te = (TileEntityInWorldGui)world.getTileEntity(x, y, z);
		float fx = -1;
		float fy = -1;
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		if (dir.offsetY == 0) {
			fy = 1-b;
			fx = dir.offsetX == 0 ? a : c;
			if (dir.offsetX > 0 || dir.offsetZ < 0)
				fx = 1-fx;
		}
		else {
			//no valid operation
		}
		//ReikaJavaLibrary.pConsole(String.format("%.2f, %.2f, %.2f -> %.2f, %.2f", a, b, c, fx, fy));
		if (fx >= 0 && fy >= 0) {
			te.activateArea(fx, fy);
			return true;
		}
		return false;
	}

	public static final class TileEntityInWorldGui extends TileEntity {

		private InWorldGui gui;
		private float xmin; //our left is what pos'n in gui
		private float ymin; //our top is what pos'n in gui
		private float width; //fraction of GUI width
		private float height; //fraction of GUI height

		public void setGui(InWorldGui gui, float x, float y, float w, float h) {
			this.gui = gui;
			xmin = x;
			ymin = y;
			width = w;
			height = h;
		}

		private void activateArea(float fx, float fy) {
			if (gui != null) {
				float rx = xmin+fx*width;
				float ry = ymin+fy*height;
				int px = (int)(rx*gui.xSize);
				int py = (int)(ry*gui.ySize);
				//ReikaJavaLibrary.pConsole(String.format("[%.0f, %.0f : %.0f, %.0f]", xmin*gui.xSize, ymin*gui.ySize, (xmin+width)*gui.xSize, (ymin+height)*gui.ySize));
				//ReikaJavaLibrary.pConsole(String.format("%.2f, %.2f -> %.2f, %.2f -> %d, %d", fx, fy, rx, ry, px, py));
				gui.click(px, py, 1);
			}
		}

	}

}

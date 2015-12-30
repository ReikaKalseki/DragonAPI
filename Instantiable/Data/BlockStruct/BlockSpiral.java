/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class BlockSpiral {

	private final BlockArray blocks = new BlockArray();
	private boolean rightHanded;
	public final int originX;
	public final int originY;
	public final int originZ;
	public final int radius;

	private int gridSize = 1;

	private int posX;
	private int posY;
	private int posZ;
	private ForgeDirection step = ForgeDirection.NORTH;

	public BlockSpiral(int x, int y, int z, int r) {
		originX = x;
		originY = y;
		originZ = z;
		radius = r;
	}

	public BlockSpiral setRightHanded() {
		rightHanded = true;
		return this;
	}

	public BlockSpiral setGridSize(int s) {
		gridSize = s;
		return this;
	}

	public BlockSpiral setInitialDirection(ForgeDirection dir) {
		if (dir.offsetY != 0)
			throw new MisuseException("Spirals are not designed for vertical directions!");
		step = dir;
		return this;
	}

	public BlockSpiral calculate() {
		blocks.clear();
		posX = originX;
		posY = originY;
		posZ = originZ;
		blocks.addBlockCoordinate(posX, posY, posZ);

		while (Math.abs(posX-originX) <= radius*gridSize && Math.abs(posY-originY) <= radius*gridSize && Math.abs(posZ-originZ) <= radius*gridSize) {
			posX += step.offsetX*gridSize;
			posY += step.offsetY*gridSize;
			posZ += step.offsetZ*gridSize;
			blocks.addBlockCoordinate(posX, posY, posZ);
			ForgeDirection dir = rightHanded ? ReikaDirectionHelper.getRightBy90(step) : ReikaDirectionHelper.getLeftBy90(step);
			if (!blocks.hasBlock(posX+dir.offsetX*gridSize, posY+dir.offsetY*gridSize, posZ+dir.offsetZ*gridSize)) {
				step = dir;
			}
		}

		return this;
	}

	public Coordinate getNthBlock(int n) {
		return blocks.getNthBlock(n);
	}

	public Coordinate getNextAndMoveOn() {
		return blocks.getNextAndMoveOn();
	}

	public int getSize() {
		return blocks.getSize();
	}

}

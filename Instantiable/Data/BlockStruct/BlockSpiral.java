package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class BlockSpiral {

	private final BlockArray blocks = new BlockArray();
	private boolean rightHanded;
	public final int originX;
	public final int originY;
	public final int originZ;
	public final int radius;

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
		this.recurse();
		return this;
	}

	private void recurse() {
		posX += step.offsetX;
		posY += step.offsetY;
		posZ += step.offsetZ;
		if (Math.abs(posX-originX) > radius || Math.abs(posY-originY) > radius || Math.abs(posZ-originZ) > radius) {
			return;
		}
		blocks.addBlockCoordinate(posX, posY, posZ);
		ForgeDirection dir = rightHanded ? ReikaDirectionHelper.getRightBy90(step) : ReikaDirectionHelper.getLeftBy90(step);
		if (!blocks.hasBlock(posX+dir.offsetX, posY+dir.offsetY, posZ+dir.offsetZ)) {
			step = dir;
		}
		this.recurse();
	}

	public int[] getNthBlock(int n) {
		return blocks.getNthBlock(n);
	}

	public int getSize() {
		return blocks.getSize();
	}

}

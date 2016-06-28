/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data.collections;


import net.minecraft.util.Vec3;
import reika.dragonapi.instantiable.data.blockstruct.BlockArray;
import reika.dragonapi.instantiable.data.immutable.Coordinate;

public class RelativePositionList {

	private final BlockArray positions = new BlockArray();

	public static final RelativePositionList cornerDirections = new RelativePositionList();

	static {
		cornerDirections.addPosition(-1, -1, -1);
		cornerDirections.addPosition(-1, -1, 0);
		cornerDirections.addPosition(-1, -1, 1);
		cornerDirections.addPosition(-1, 0, -1);

		cornerDirections.addPosition(-1, 0, 1);
		cornerDirections.addPosition(-1, 1, -1);
		cornerDirections.addPosition(-1, 1, 0);
		cornerDirections.addPosition(-1, 1, 1);

		cornerDirections.addPosition(0, -1, -1);

		cornerDirections.addPosition(0, -1, 1);

		cornerDirections.addPosition(0, 1, -1);

		cornerDirections.addPosition(0, 1, 1);

		cornerDirections.addPosition(1, -1, -1);
		cornerDirections.addPosition(1, -1, 0);
		cornerDirections.addPosition(1, -1, 1);
		cornerDirections.addPosition(1, 0, -1);

		cornerDirections.addPosition(1, 0, 1);
		cornerDirections.addPosition(1, 1, -1);
		cornerDirections.addPosition(1, 1, 0);
		cornerDirections.addPosition(1, 1, 1);
	}

	public RelativePositionList() {

	}

	public void addPosition(int dx, int dy, int dz) {
		positions.addBlockCoordinate(dx, dy, dz);
	}

	public void removePosition(int dx, int dy, int dz) {
		positions.remove(dx, dy, dz);
	}

	public boolean containsPosition(int dx, int dy, int dz) {
		return positions.hasBlock(dx, dy, dz);
	}

	public BlockArray getPositionsRelativeTo(int x, int y, int z) {
		return positions.copy().offset(x, y, z);
	}

	public int getSize() {
		return positions.getSize();
	}

	public Coordinate getNthPosition(int x, int y, int z, int n) {
		Coordinate relative = this.getNthRelativePosition(n);
		return relative.offset(x, y, z);
	}

	public Coordinate getNthRelativePosition(int n) {
		return positions.getNthBlock(n);
	}

	public Vec3 getVector(int n) {
		Coordinate d = this.getNthRelativePosition(n);
		return Vec3.createVectorHelper(d.xCoord, d.yCoord, d.zCoord);
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;


import net.minecraft.util.Vec3;

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

	public int[] getNthPosition(int x, int y, int z, int n) {
		int[] relative = this.getNthRelativePosition(n);
		relative[0] += x;
		relative[1] += y;
		relative[2] += z;
		return relative;
	}

	public int[] getNthRelativePosition(int n) {
		return positions.getNthBlock(n);
	}

	public Vec3 getVector(int n) {
		int[] d = this.getNthRelativePosition(n);
		return Vec3.createVectorHelper(d[0], d[1], d[2]);
	}

}

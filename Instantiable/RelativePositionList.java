package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Instantiable.Data.BlockArray;

public class RelativePositionList {

	private final BlockArray positions = new BlockArray();

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

}

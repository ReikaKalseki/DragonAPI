package Reika.DragonAPI.Instantiable.Data;

public class BlockBox {

	public final int minX;
	public final int minY;
	public final int minZ;
	public final int maxX;
	public final int maxY;
	public final int maxZ;

	public BlockBox(int x0, int y0, int z0, int x1, int y1, int z1) {
		minX = x0;
		minY = y0;
		minZ = z0;

		maxX = x1;
		maxY = y1;
		maxZ = z1;
	}

	public int getSizeX() {
		return maxX-minX;
	}

	public int getSizeY() {
		return maxY-minY;
	}

	public int getSizeZ() {
		return maxZ-minZ;
	}

	public int getVolume() {
		return this.getSizeX()*this.getSizeY()*this.getSizeZ();
	}

}
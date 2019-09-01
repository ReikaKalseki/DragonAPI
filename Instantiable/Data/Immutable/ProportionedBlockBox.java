package Reika.DragonAPI.Instantiable.Data.Immutable;

import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.WeightedRandom;

public class ProportionedBlockBox {

	public final BlockBox volume;

	public final int totalFaceArea;
	public final int totalEdgeLength;

	private final WeightedRandom<ForgeDirection> faceAreas = new WeightedRandom();
	private final WeightedRandom<CubeEdge> edgeLengths = new WeightedRandom();

	public ProportionedBlockBox(BlockBox box) {
		volume = box;

		totalFaceArea = this.calculateFaces();
		totalEdgeLength = this.calculateEdges();
	}

	private int calculateFaces() {
		int area = volume.getSizeX()*volume.getSizeZ();
		faceAreas.addEntry(ForgeDirection.UP, area);
		faceAreas.addEntry(ForgeDirection.DOWN, area);

		area = volume.getSizeZ()*volume.getSizeY();
		faceAreas.addEntry(ForgeDirection.EAST, area);
		faceAreas.addEntry(ForgeDirection.WEST, area);

		area = volume.getSizeX()*volume.getSizeY();
		faceAreas.addEntry(ForgeDirection.SOUTH, area);
		faceAreas.addEntry(ForgeDirection.NORTH, area);

		return (int)faceAreas.getTotalWeight();
	}

	private int calculateEdges() {
		int len = volume.getSizeY();
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.minX, volume.minY, volume.minZ), ForgeDirection.UP, len, false, false, false), len);
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.maxX-1, volume.minY, volume.minZ), ForgeDirection.UP, len, true, false, false), len);
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.maxX-1, volume.minY, volume.maxZ-1), ForgeDirection.UP, len, true, false, true), len);
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.minX, volume.minY, volume.maxZ-1), ForgeDirection.UP, len, false, false, true), len);

		len = volume.getSizeX();
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.minX, volume.minY, volume.minZ), ForgeDirection.EAST, len, false, false, false), len);
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.minX, volume.maxY-1, volume.minZ), ForgeDirection.EAST, len, false, true, false), len);
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.minX, volume.minY, volume.maxZ-1), ForgeDirection.EAST, len, false, false, true), len);
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.minX, volume.maxY-1, volume.maxZ-1), ForgeDirection.EAST, len, false, true, true), len);

		len = volume.getSizeZ();
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.minX, volume.minY, volume.minZ), ForgeDirection.SOUTH, len, false, false, false), len);
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.minX, volume.maxY-1, volume.minZ), ForgeDirection.SOUTH, len, false, true, false), len);
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.maxX-1, volume.minY, volume.minZ), ForgeDirection.SOUTH, len, true, false, false), len);
		edgeLengths.addEntry(new CubeEdge(new Coordinate(volume.maxX-1, volume.maxY-1, volume.minZ), ForgeDirection.SOUTH, len, true, true, false), len);

		return (int)edgeLengths.getTotalWeight();
	}

	public ForgeDirection getRandomFace() {
		return faceAreas.getRandomEntry();
	}

	public CubeEdge getRandomEdge() {
		return edgeLengths.getRandomEntry();
	}

	public static class CubeEdge {

		public final Coordinate root;
		public final ForgeDirection axis;
		public final int length;

		public final boolean isPositiveX;
		public final boolean isPositiveY;
		public final boolean isPositiveZ;

		private CubeEdge(Coordinate c, ForgeDirection dir, int l, boolean px, boolean py, boolean pz) {
			root = c;
			axis = dir;
			length = l;

			isPositiveX = px;
			isPositiveY = py;
			isPositiveZ = pz;
		}

	}

}

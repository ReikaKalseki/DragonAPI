package Reika.DragonAPI.Libraries.Rendering;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TexturedQuad;
import net.minecraftforge.common.util.ForgeDirection;

public class ReikaModelHelper {

	public static void flipUVs(ModelRenderer part, boolean x, boolean y, boolean z) {
		for (Object o : part.cubeList) {
			flipUVs((ModelBox)o, x, y, z);
		}
		if (part.childModels != null) {
			for (Object o : part.childModels) {
				flipUVs((ModelRenderer)o, x, y, z);
			}
		}
	}

	public static void flipUVs(ModelBox box, boolean x, boolean y, boolean z) {
		boolean[] doFlip = new boolean[] {y || z, y || z, x || z, x || z, x || y, x || y};
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			if ((dir.offsetX == 0 && x) || (dir.offsetY == 0 && y) || (dir.offsetZ == 0 && z)) {
				flipFace(getFace(box, dir), dir, x, y, z);
			}
		}
	}

	private static TexturedQuad getFace(ModelBox box, ForgeDirection face) {
		int idx = -1; //+x -x -y +y -z +z
		switch(face) {
			case EAST:
				idx = 0;
				break;
			case WEST:
				idx = 1;
				break;
			case DOWN:
				idx = 2;
				break;
			case UP:
				idx = 3;
				break;
			case NORTH:
				idx = 4;
				break;
			case SOUTH:
				idx = 5;
				break;
			default:
				break;
		}
		return idx >= 0 ? box.quadList[idx] : null;
	}

	private static void flipFace(TexturedQuad quad, ForgeDirection face, boolean x, boolean y, boolean z) {
		/*
		if (x) {
			double maxX = Double.NEGATIVE_INFINITY;
			double minX = Double.POSITIVE_INFINITY;
			double maxY = Double.NEGATIVE_INFINITY;
			double minY = Double.POSITIVE_INFINITY;
			boolean[] leftSide = new boolean[4];
			for (int i = 0; i < 4; i++) {
				PositionTextureVertex vert = quad.vertexPositions[i];
				maxX = Math.max(maxX, vert.vector3D.xCoord);
				minX = Math.min(minX, vert.vector3D.xCoord);
				maxX = Math.max(maxX, vert.vector3D.xCoord);
				minX = Math.min(minX, vert.vector3D.xCoord);
			}
			for (int i = 0; i < 4; i++) {
				PositionTextureVertex vert = quad.vertexPositions[i];
				if (vert.vector3D.xCoord == minX) {
					leftSide[i] = true;
				}
			}
		}
		 */
		float temp;
		switch(face) {
			case DOWN:	//boxURoot + sizeZ, boxVRoot, boxURoot + sizeZ + sizeX, boxVRoot + sizeZ
			case UP:	//boxURoot + sizeZ + sizeX, boxVRoot + sizeZ, boxURoot + sizeZ + sizeX + sizeX, boxVRoot
				if (x) {
					temp = quad.vertexPositions[0].texturePositionX;
					quad.vertexPositions[0].texturePositionX = quad.vertexPositions[2].texturePositionX;
					quad.vertexPositions[2].texturePositionX = temp;
				}
				if (z) {
					temp = quad.vertexPositions[0].texturePositionY;
					quad.vertexPositions[0].texturePositionY = quad.vertexPositions[2].texturePositionY;
					quad.vertexPositions[2].texturePositionY = temp;
				}
				break;
			case EAST:	//boxURoot + sizeZ + sizeX, boxVRoot + sizeZ, boxURoot + sizeZ + sizeX + sizeZ, boxVRoot + sizeZ + sizeY
			case WEST:	//boxURoot, boxVRoot + sizeZ, boxURoot + sizeZ, boxVRoot + sizeZ + sizeY
				if (z) {
					temp = quad.vertexPositions[0].texturePositionX;
					quad.vertexPositions[0].texturePositionX = quad.vertexPositions[2].texturePositionX;
					quad.vertexPositions[2].texturePositionX = temp;
				}
				if (y) {
					temp = quad.vertexPositions[1].texturePositionY;
					quad.vertexPositions[1].texturePositionY = quad.vertexPositions[3].texturePositionY;
					quad.vertexPositions[3].texturePositionY = temp;
				}
				break;
			case NORTH:	//boxURoot + sizeZ, boxVRoot + sizeZ, boxURoot + sizeZ + sizeX, boxVRoot + sizeZ + sizeY
			case SOUTH:	//boxURoot + sizeZ + sizeX + sizeZ, boxVRoot + sizeZ, boxURoot + sizeZ + sizeX + sizeZ + sizeX, boxVRoot + sizeZ + sizeY
				if (x) {
					temp = quad.vertexPositions[0].texturePositionX;
					quad.vertexPositions[0].texturePositionX = quad.vertexPositions[2].texturePositionX;
					quad.vertexPositions[2].texturePositionX = temp;
				}
				if (y) {
					temp = quad.vertexPositions[1].texturePositionY;
					quad.vertexPositions[1].texturePositionY = quad.vertexPositions[3].texturePositionY;
					quad.vertexPositions[3].texturePositionY = temp;
				}
				break;
		}

		//group rule: for U, 0&3 and 1&2; for V, 0&1 and 2&3
		quad.vertexPositions[1].texturePositionX = quad.vertexPositions[2].texturePositionX;
		quad.vertexPositions[3].texturePositionX = quad.vertexPositions[0].texturePositionX;
		quad.vertexPositions[1].texturePositionY = quad.vertexPositions[0].texturePositionY;
		quad.vertexPositions[3].texturePositionY = quad.vertexPositions[2].texturePositionY;

	}
}

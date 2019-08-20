package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;

@Deprecated
public class RenderableDistortedCuboid {

	/** An array of 6 TexturedQuads, one for each face of a cube */
	protected TexturedQuad[] quadList;

	public RenderableDistortedCuboid(float x1, float y1, float z1, int sx, int sy, int sz, int textureWidth, int textureHeight)
	{
		float posX1 = x1;
		float posY1 = y1;
		float posZ1 = z1;
		float posX2 = x1 + sx;
		float posY2 = y1 + sy;
		float posZ2 = z1 + sz;
		quadList = new TexturedQuad[6];

		PositionTextureVertex x1y1z1 = new PositionTextureVertex(x1, y1, z1, 0F, 0F);
		PositionTextureVertex x2y1z1 = new PositionTextureVertex(posX2, y1, z1, 0F, 8F);
		PositionTextureVertex x2y2z1 = new PositionTextureVertex(posX2, posY2, z1, 8F, 8F);
		PositionTextureVertex z1y2z1 = new PositionTextureVertex(x1, posY2, z1, 8F, 0F);
		PositionTextureVertex x1y1z2 = new PositionTextureVertex(x1, y1, posZ2, 0F, 0F);
		PositionTextureVertex x2y1z2 = new PositionTextureVertex(posX2, y1, posZ2, 0F, 8F);
		PositionTextureVertex x2y2z2 = new PositionTextureVertex(posX2, posY2, posZ2, 8F, 8F);
		PositionTextureVertex x1y2z2 = new PositionTextureVertex(x1, posY2, posZ2, 8F, 0F);
		quadList[0] = new TexturedQuad(new PositionTextureVertex[] {x2y1z2, x2y1z1, x2y2z1, x2y2z2}, sz + sx, sz, sz + sx + sz, sz + sy, textureWidth, textureHeight);
		quadList[1] = new TexturedQuad(new PositionTextureVertex[] {x1y1z1, x1y1z2, x1y2z2, z1y2z1}, 0, sz, sz, sz + sy, textureWidth, textureHeight);
		quadList[2] = new TexturedQuad(new PositionTextureVertex[] {x2y1z2, x1y1z2, x1y1z1, x2y1z1}, sz, 0, sz + sx, sz, textureWidth, textureHeight);
		quadList[3] = new TexturedQuad(new PositionTextureVertex[] {x2y2z1, z1y2z1, x1y2z2, x2y2z2}, sz + sx, sz, sz + sx + sx, 0, textureWidth, textureHeight);
		quadList[4] = new TexturedQuad(new PositionTextureVertex[] {x2y1z1, x1y1z1, z1y2z1, x2y2z1}, sz, sz, sz + sx, sz + sy, textureWidth, textureHeight);
		quadList[5] = new TexturedQuad(new PositionTextureVertex[] {x1y1z2, x2y1z2, x2y2z2, x1y2z2}, sz + sx + sz, sz, sz + sx + sz + sx, sz + sy, textureWidth, textureHeight);
	}

}

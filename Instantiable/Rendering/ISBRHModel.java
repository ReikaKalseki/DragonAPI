/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

/** A Model object designed for being used in an ISBRH. */
public class ISBRHModel extends ModelBase {

	private final Collection<ModelBox> boxes = new ArrayList();

	public ISBRHModel(int s) {
		this(s, s);
	}

	public ISBRHModel(int w, int h) {
		textureHeight = h;
		textureWidth = w;
	}

	public ISBRHModel addBox(int x, int y, int z, float px, float py, float pz, int tx, int ty) {
		return this.addBox(x, y, z, px, py, pz, 0, 0, 0, 0, 0, 0, tx, ty);
	}

	public ISBRHModel addBox(int x, int y, int z, float px, float py, float pz, float ox, float oy, float oz, int tx, int ty) {
		return this.addBox(x, y, z, px, py, pz, ox, oy, oz, 0, 0, 0, tx, ty);
	}

	public ISBRHModel addBox(int x, int y, int z, float px, float py, float pz, float ox, float oy, float oz, float rx, float ry, float rz, int tx, int ty) {
		ModelBox box = new ModelBoxNoTess(new NoTessModelBox(this, tx, ty), tx, ty, px, py, pz, x, y, z, 0);
		/*
		box.addBox(px, py, pz, x, y, z);
		box.setRotationPoint(ox, oy, oz);
		box.rotateAngleX = rx;
		box.rotateAngleY = ry;
		box.rotateAngleZ = rz;
		box.setTextureSize(textureWidth, textureHeight);
		 */
		return this.addBox(box);
	}

	private ISBRHModel addBox(ModelBox box) {
		boxes.add(box);
		return this;
	}

	public void render() {
		this.render(0.0625F);
	}

	public void render(float scale) {
		for (ModelBox m : boxes) {
			m.render(Tessellator.instance, scale);
		}
	}

	private static class NoTessModelBox extends ModelRenderer {

		private final int texX;
		private final int texY;

		public NoTessModelBox(ModelBase mb, int tx, int ty) {
			super(mb, tx, ty);
			texX = tx;
			texY = ty;
		}

		@Override
		public ModelRenderer addBox(float px, float py, float pz, int x, int y, int z) {
			cubeList.add(new ModelBoxNoTess(this, texX, texY, px, py, pz, x, y, z, 0.0F));
			return this;
		}

	}

	private static class ModelBoxNoTess extends ModelBox {

		public ModelBoxNoTess(ModelRenderer m, int tx, int ty, float px, float py, float pz, int x, int y, int z, float d) {
			super(m, tx, ty, px, py, pz, x, y, z, d);

			for (int i = 0; i < quadList.length; i++) {
				quadList[i] = new NoTessQuad(quadList[i]);
			}
		}

	}

	private static class NoTessQuad extends TexturedQuad {

		public NoTessQuad(TexturedQuad override) {
			super(override.vertexPositions);
		}

		@Override
		public void draw(Tessellator v5, float sc) {
			Vec3 vec3 = vertexPositions[1].vector3D.subtract(vertexPositions[0].vector3D);
			Vec3 vec31 = vertexPositions[1].vector3D.subtract(vertexPositions[2].vector3D);
			Vec3 vec32 = vec31.crossProduct(vec3).normalize();
			//v5.startDrawingQuads();

			//if (invertNormal) { //never used or accessible
			//	v5.setNormal(-((float)vec32.xCoord), -((float)vec32.yCoord), -((float)vec32.zCoord));
			//}
			//else {
			v5.setNormal((float)vec32.xCoord, (float)vec32.yCoord, (float)vec32.zCoord);
			//}

			for (int i = 0; i < 4; ++i) {
				PositionTextureVertex positiontexturevertex = vertexPositions[i];
				v5.addVertexWithUV((float)positiontexturevertex.vector3D.xCoord*sc, (float)positiontexturevertex.vector3D.yCoord*sc, (float)positiontexturevertex.vector3D.zCoord*sc, positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY);
			}

			//v5.draw();
		}

	}

}

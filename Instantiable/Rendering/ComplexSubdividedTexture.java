package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.client.renderer.Tessellator;

import Reika.DragonAPI.Instantiable.Math.DoublePolygon;

public class ComplexSubdividedTexture {

	private final double textureOriginX;
	private final double textureOriginY;

	private final double textureWidth;
	private final double textureHeight;

	private DoublePolygon currentPoly;

	public ComplexSubdividedTexture(double x, double y, double w, double h) {
		textureOriginX = x;
		textureOriginY = y;
		textureWidth = w;
		textureHeight = h;
	}

	public void startPoly(DoublePolygon p) {
		currentPoly = p;
	}

	public void addVertex(double x, double y) {
		double u = (x-textureOriginX)/textureWidth;
		double v = (y-textureOriginY)/textureHeight;/*

		double cu = currentPoly.getBounds().x+currentPoly.getBounds().width/2;
		double cv = currentPoly.getBounds().y+currentPoly.getBounds().height/2;

		double du = x-cu;
		double dv = y-cv;
		double d = 1.15;
		double rx = MathHelper.clamp_double(cu+du*d, 0, 1);
		double ry = MathHelper.clamp_double(cv+dv*d, 0, 1);*/

		Tessellator.instance.addVertexWithUV(x, y, 0, u, v);
	}

}

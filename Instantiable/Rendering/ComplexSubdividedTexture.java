package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.client.renderer.Tessellator;

public class ComplexSubdividedTexture {

	private final double textureWidth;
	private final double textureHeight;

	public ComplexSubdividedTexture(double w, double h) {
		textureWidth = w;
		textureHeight = h;
	}

	public void addVertex(double x, double y) {
		double u = x/textureWidth;
		double v = y/textureHeight;
		Tessellator.instance.addVertexWithUV(x, y, 0, u, v);
	}

}

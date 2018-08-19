/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;


public class RenderTransform {

	public final double offsetX;
	public final double offsetY;
	public final double offsetZ;

	public final double rotationX;
	public final double rotationY;
	public final double rotationZ;

	public RenderTransform(double ox, double oy, double oz) {
		this(ox, oy, oz, 0, 0, 0);
	}

	public RenderTransform(double ox, double oy, double oz, double rx, double ry, double rz) {
		rotationX = rx;
		offsetX = ox;
		rotationY = ry;
		offsetY = oy;
		rotationZ = rz;
		offsetZ = oz;
	}

}

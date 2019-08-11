/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import java.awt.Polygon;

import net.minecraft.client.Minecraft;

public class PolygonalGuiButton extends ImagedGuiButton {

	private final Polygon shape;

	public PolygonalGuiButton(int par1, int par2, int par3, String par4Str, Class mod, Polygon p) {
		super(par1, par2, par3, par4Str, mod);
		shape = p;
	}

	/** Draw a Gui Button with an image background. Args: id, x, y, width, height, u, v, filepath, class root */
	public PolygonalGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String file, Class mod, Polygon p)
	{
		super(par1, par2, par3, par4, par5, par7, par8, file, mod);
		shape = p;
	}

	/** Draw a Gui Button with an image background and text overlay.
	 *Args: id, x, y, width, height, u, v, text overlay, text color, shadow, filepath, class root */
	public PolygonalGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String par6Str, int par9, boolean par10, String file, Class mod, Polygon p)
	{
		super(par1, par2, par3, par4, par5, par7, par8, par6Str, par9, par10, file, mod);
		shape = p;
	}

	/** Draw a Gui Button with an image background and text tooltip. Args: id, x, y, width, height, u, v, filepath, text tooltip, text color, shadow */
	public PolygonalGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String file, String par6Str, int par9, boolean par10, Class mod, Polygon p)
	{
		super(par1, par2, par3, par4, par5, par7, par8, file, par6Str, par9, par10, mod);
		shape = p;
	}

	@Override
	public void drawButton(Minecraft mc, int mx, int my) {
		super.drawButton(mc, mx, my);
	}

	@Override
	protected boolean isPositionWithin(int mx, int my) {
		return shape.contains(mx, my);
	}

}

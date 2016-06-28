/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import reika.dragonapi.interfaces.FractionalButtonGui;

/** A type of GuiButton that can tell what fraction of it has been clicked and act accordingly. */
public class FractionalGuiButton extends GuiButton {

	public final int xDivisions;
	public final int yDivisions;
	private final FractionalButtonGui gui;
	public boolean shouldRender = true;

	public FractionalGuiButton(int id, int x, int y, int w, int h, String text, int xfrac, int yfrac, FractionalButtonGui gui) {
		super(id, x, y, w, h, text);

		xDivisions = Math.max(1, xfrac);
		yDivisions = Math.max(1, yfrac);
		this.gui = gui;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mx, int my)
	{
		boolean flag = super.mousePressed(mc, mx, my);
		if (flag) {
			this.triggerFractionalClick(mx, my);
		}
		return flag;
	}

	private void triggerFractionalClick(int mx, int my) {
		int dx = mx-xPosition;
		int dy = my-yPosition;
		int u = dx*xDivisions/width;
		int v = dy*yDivisions/height;
		gui.onFractionalClick(id, u, v);
	}

	@Override
	public void drawButton(Minecraft mc, int x, int y)
	{
		if (shouldRender)
			super.drawButton(mc, x, y);
	}
}

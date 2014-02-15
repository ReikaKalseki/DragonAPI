/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class ColorButton extends GuiButton {

	private final int color;
	private final int brighter;
	public boolean isSelected = false;

	public ColorButton(int par1, int par2, int par3, int par4, int par5, Color c) {
		this(par1, par2, par3, par4, par5, c.getRGB());
	}

	/** Draw a Gui Button with an image background. Args: id, x, y, width, height, color*/
	public ColorButton(int par1, int par2, int par3, int par4, int par5, int par9)
	{
		super(par1, par2, par3, 200, 20, null);
		enabled = true;
		drawButton = true;
		id = par1;
		xPosition = par2;
		yPosition = par3;
		width = par4;
		height = par5;
		displayString = null;

		color = par9;
		Color br = Color.decode(String.valueOf(color));
		int r = Math.min(255, br.getRed()+96);
		int g = Math.min(255, br.getGreen()+96);
		int b = Math.min(255, br.getBlue()+96);
		brighter = new Color(r, g, b).getRGB();
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft mc, int x, int y)
	{
		if (isSelected) {
			this.drawRect(xPosition, yPosition, xPosition+width, yPosition+height, 0xff777777);
			this.drawRect(xPosition, yPosition, xPosition+width-1, yPosition+height-1, 0xff333333);
			this.drawRect(xPosition+1, yPosition+1, xPosition+width-1, yPosition+height-1, 0xff000000 | brighter);
			this.drawRect(xPosition+1, yPosition+1, xPosition+width-2, yPosition+height-2, 0xff000000 | color);//
		}
		else {
			this.drawRect(xPosition, yPosition, xPosition+width, yPosition+height, 0xff333333);
			this.drawRect(xPosition, yPosition, xPosition+width-1, yPosition+height-1, 0xff777777);
			this.drawRect(xPosition+1, yPosition+1, xPosition+width-1, yPosition+height-1, 0xff000000 | brighter);//
			this.drawRect(xPosition+2, yPosition+2, xPosition+width-1, yPosition+height-1, 0xff000000 | color);
		}
	}

}

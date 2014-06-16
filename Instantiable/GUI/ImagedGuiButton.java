/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public final class ImagedGuiButton extends GuiButton {

	private int u;
	private int v;
	private int color;
	private boolean shadow = true;
	private String filepath;
	private final boolean hasToolTip;
	private final Class modClass;

	public ImagedGuiButton(int par1, int par2, int par3, String par4Str, Class mod)
	{
		super(par1, par2, par3, 200, 20, par4Str);

		hasToolTip = false;
		modClass = mod;
	}

	/** Draw a Gui Button with an image background. Args: id, x, y, width, height, u, v, filepath */
	public ImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String file, Class mod)
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

		u = par7;
		v = par8;
		filepath = file;

		hasToolTip = false;
		modClass = mod;
	}

	/** Draw a Gui Button with an image background and text overlay.
	 * Args: id, x, y, width, height, u, v, text overlay, text color, shadow, filepath */
	public ImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String par6Str, int par9, boolean par10, String file, Class mod)
	{
		super(par1, par2, par3, 200, 20, par6Str);
		enabled = true;
		drawButton = true;
		id = par1;
		xPosition = par2;
		yPosition = par3;
		width = par4;
		height = par5;
		displayString = par6Str;

		u = par7;
		v = par8;
		color = par9;
		shadow = par10;
		filepath = file;

		hasToolTip = false;
		modClass = mod;
	}

	/** Draw a Gui Button with an image background and text tooltip. Args: id, x, y, width, height, u, v, filepath, text tooltip, text color, shadow */
	public ImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String file, String par6Str, int par9, boolean par10, Class mod)
	{
		super(par1, par2, par3, 200, 20, par6Str);
		enabled = true;
		drawButton = true;
		id = par1;
		xPosition = par2;
		yPosition = par3;
		width = par4;
		height = par5;
		displayString = par6Str;

		u = par7;
		v = par8;
		color = par9;
		shadow = par10;
		filepath = file;

		hasToolTip = true;
		modClass = mod;
	}

	private final String getButtonTexture() {
		return filepath;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft mc, int mx, int my)
	{
		if (drawButton)
		{
			FontRenderer var4 = mc.fontRenderer;
			int tex = GL11.GL_TEXTURE_BINDING_2D;
			ReikaTextureHelper.bindTexture(modClass, this.getButtonTexture());
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(xPosition, yPosition, u, v, width, height);

			field_82253_i = mx >= xPosition && my >= yPosition && mx < xPosition + width && my < yPosition + height;
			int k = this.getHoverState(field_82253_i);

			//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, u, v, this.width / 2, this.height);
			this.mouseDragged(mc, mx, my);/*
            int var7 = 14737632;

            if (!this.enabled)
            {
                var7 = -6250336;
            }
            else if (var5)
            {
                var7 = 16777120;
            }*/
			if (displayString != null && !hasToolTip) {
				ReikaTextureHelper.bindFontTexture();
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
				if (shadow)
					this.drawCenteredString(var4, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, color);
				else
					this.drawCenteredStringNoShadow(var4, displayString, xPosition + width / 2 + 1, yPosition + (height - 8) / 2, color);
			}
			else if (k == 2 && displayString != null && hasToolTip) {
				this.drawToolTip(mc, mx, my);
			}
			GL11.glColor4d(1, 1, 1, 1);
		}
	}

	private void drawToolTip(Minecraft mc, int mx, int my) {
		ReikaGuiAPI.instance.drawTooltip(mc.fontRenderer, displayString);
		ReikaTextureHelper.bindFontTexture();
	}

	/**
	 * Renders the specified text to the screen, center-aligned.
	 */
	public static void drawCenteredStringNoShadow(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5)
	{
		par1FontRenderer.drawString(par2Str, par3 - par1FontRenderer.getStringWidth(par2Str) / 2, par4, par5);
	}

}

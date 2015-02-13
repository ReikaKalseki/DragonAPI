/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

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

	public TextAlign alignment = TextAlign.CENTER;
	public int textOffset = 0;
	public FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;

	public int textureSize = 256;

	public ImagedGuiButton(int par1, int par2, int par3, String par4Str, Class mod)
	{
		super(par1, par2, par3, 200, 20, par4Str);

		hasToolTip = false;
		modClass = mod;
	}

	/** Draw a Gui Button with an image background. Args: id, x, y, width, height, u, v, filepath, class root */
	public ImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String file, Class mod)
	{
		super(par1, par2, par3, 200, 20, null);
		enabled = true;
		visible = true;
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
	 *Args: id, x, y, width, height, u, v, text overlay, text color, shadow, filepath, class root */
	public ImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String par6Str, int par9, boolean par10, String file, Class mod)
	{
		super(par1, par2, par3, 200, 20, par6Str);
		enabled = true;
		visible = true;
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
		visible = true;
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

	@Override
	public void drawButton(Minecraft mc, int mx, int my)
	{
		if (visible) {
			int tex = GL11.GL_TEXTURE_BINDING_2D;
			ReikaTextureHelper.bindTexture(modClass, this.getButtonTexture());
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(xPosition, yPosition, u, v, width, height);

			field_146123_n = mx >= xPosition && my >= yPosition && mx < xPosition+width && my < yPosition+height;
			int k = this.getHoverState(field_146123_n);

			this.mouseDragged(mc, mx, my);
			if (displayString != null && !hasToolTip) {
				ReikaTextureHelper.bindFontTexture();
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
				renderer.drawString(displayString, this.getLabelX()+alignment.getDX(renderer, displayString), yPosition+(height-8)/2, color, shadow);
			}
			else if (k == 2 && displayString != null && hasToolTip) {
				this.drawToolTip(mc, mx, my);
			}
			GL11.glColor4d(1, 1, 1, 1);
		}
	}

	@Override
	public void drawTexturedModalRect(int x, int y, int u, int v, int w, int h) {
		float f = 1F/textureSize;
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.addVertexWithUV(x+0, y+h, zLevel, (u+0)*f, (v+h)*f);
		v5.addVertexWithUV(x+w, y+h, zLevel, (u+w)*f, (v+h)*f);
		v5.addVertexWithUV(x+w, y+0, zLevel, (u+w)*f, (v+0)*f);
		v5.addVertexWithUV(x+0, y+0, zLevel, (u+0)*f, (v+0)*f);
		v5.draw();
	}

	private int getLabelX() {
		int base = textOffset+xPosition;
		switch(alignment) {
		case CENTER:
			return base+width/2-renderer.getStringWidth(displayString)+1;
		case LEFT:
			return base+2;
		case RIGHT:
			return base+width-4-renderer.getStringWidth(displayString)*2;
		default:
			return base;
		}
	}

	private void drawToolTip(Minecraft mc, int mx, int my) {
		ReikaGuiAPI.instance.drawTooltip(mc.fontRenderer, displayString);
		ReikaTextureHelper.bindFontTexture();
	}

	public static enum TextAlign {
		LEFT(),
		CENTER(),
		RIGHT();

		private int getDX(FontRenderer f, String s) {
			switch(this) {
			case CENTER:
				return f.getStringWidth(s)/2;
			case LEFT:
				return 0;
			case RIGHT:
				return f.getStringWidth(s);
			default:
				return 0;
			}
		}
	}

}

package Reika.DragonAPI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;

import org.lwjgl.opengl.GL11;

public final class ImagedGuiButton extends GuiButton {

	private int u;
	private int v;
	private int color;
	private boolean shadow = true;
	private String filepath;

    public ImagedGuiButton(int par1, int par2, int par3, String par4Str)
    {
        super(par1, par2, par3, 200, 20, par4Str);
    }

    /** Draw a Gui Button with an image background. Args: id, x, y, width, height, u, v, color, filepath */
    public ImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, int par9, String file)
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
        color = par9;
        filepath = file;
    }

    /** Draw a Gui Button with an image background and text overlay. Args: id, x, y, width, height, text overlay, u, v, text color, shadow, filepath */
    public ImagedGuiButton(int par1, int par2, int par3, int par4, int par5, String par6Str, int par7, int par8, int par9, boolean par10, String file)
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
    }

    /**
     * Draws this button to the screen.
     */
    @Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (drawButton)
        {
            FontRenderer var4 = par1Minecraft.fontRenderer;
            int tex = GL11.GL_TEXTURE_BINDING_2D;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1Minecraft.renderEngine.getTexture(filepath));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var5 = par2 >= xPosition && par3 >= yPosition && par2 < xPosition + width && par3 < yPosition + height;
            //int var6 = this.getHoverState(var5);
            this.drawTexturedModalRect(xPosition, yPosition, u, v, width, height);
            //this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, u, v, this.width / 2, this.height);
            this.mouseDragged(par1Minecraft, par2, par3);/*
            int var7 = 14737632;

            if (!this.enabled)
            {
                var7 = -6250336;
            }
            else if (var5)
            {
                var7 = 16777120;
            }*/
            par1Minecraft.renderEngine.bindTexture("/font/glyph_AA.png");
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
            if (shadow)
            	this.drawCenteredString(var4, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, color);
            else
            	this.drawCenteredStringNoShadow(var4, displayString, xPosition + width / 2 + 1, yPosition + (height - 8) / 2, color);
            GL11.glColor4d(1, 1, 1, 1);
        }
    }

    /**
     * Renders the specified text to the screen, center-aligned.
     */
    public static void drawCenteredStringNoShadow(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5)
    {
        par1FontRenderer.drawString(par2Str, par3 - par1FontRenderer.getStringWidth(par2Str) / 2, par4, par5);
    }

}

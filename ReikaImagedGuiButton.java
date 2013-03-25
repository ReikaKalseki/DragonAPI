package Reika.DragonAPI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.src.ModLoader;

import org.lwjgl.opengl.GL11;

public class ReikaImagedGuiButton extends GuiButton {
	
	private int u;
	private int v;
	private int color;
	private boolean shadow = true;
	private String filepath;
	
    public ReikaImagedGuiButton(int par1, int par2, int par3, String par4Str)
    {
        super(par1, par2, par3, 200, 20, par4Str);
    }

    /** Draw a Gui Button with an image background. Args: id, x, y, width, height, u, v, color, filepath */
    public ReikaImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, int par9, String file)
    {
    	super(par1, par2, par3, 200, 20, null);
        this.enabled = true;
        this.drawButton = true;
        this.id = par1;
        this.xPosition = par2;
        this.yPosition = par3;
        this.width = par4;
        this.height = par5;
        this.displayString = null;
        
        this.u = par7;
        this.v = par8;
        this.color = par9;
        this.filepath = file;
    }
    
    /** Draw a Gui Button with an image background and text overlay. Args: id, x, y, width, height, text overlay, u, v, text color, shadow, filepath */
    public ReikaImagedGuiButton(int par1, int par2, int par3, int par4, int par5, String par6Str, int par7, int par8, int par9, boolean par10, String file)
    {
    	super(par1, par2, par3, 200, 20, par6Str);
        this.enabled = true;
        this.drawButton = true;
        this.id = par1;
        this.xPosition = par2;
        this.yPosition = par3;
        this.width = par4;
        this.height = par5;
        this.displayString = par6Str;
        
        this.u = par7;
        this.v = par8;
        this.color = par9;
        this.shadow = par10;
        this.filepath = file;
    }
	
    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            FontRenderer var4 = par1Minecraft.fontRenderer;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1Minecraft.renderEngine.getTexture(String.format("%s", this.filepath)));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var5 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            //int var6 = this.getHoverState(var5);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, u, v, this.width, this.height);
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
            if (this.shadow)
            	this.drawCenteredString(var4, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, this.color);
            else
            	this.drawCenteredStringNoShadow(var4, this.displayString, this.xPosition + this.width / 2 + 1, this.yPosition + (this.height - 8) / 2, this.color);
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

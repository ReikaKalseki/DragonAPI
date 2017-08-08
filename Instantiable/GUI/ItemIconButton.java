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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;

public class ItemIconButton extends GuiButton {

	private int color;
	private boolean shadow = true;

	private ItemStack iconItem;

	private static final RenderItem itemRender = new RenderItem();

	/** Draw a Gui Button with an image background. Args: id, x, y, color, itemstack */
	public ItemIconButton(int par1, int par2, int par3, int par9, ItemStack is)
	{
		super(par1, par2, par3, 200, 20, null);
		enabled = true;
		visible = true;
		id = par1;
		xPosition = par2;
		yPosition = par3;
		width = 16;
		height = 16;
		displayString = null;

		color = par9;

		if (is != null)
			iconItem = is.copy();
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
	{
		if (visible)
		{
			FontRenderer var4 = par1Minecraft.fontRenderer;
			int tex = GL11.GL_TEXTURE_BINDING_2D;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			boolean var5 = par2 >= xPosition && par3 >= yPosition && par2 < xPosition + width && par3 < yPosition + height;

			ReikaGuiAPI.instance.drawItemStack(itemRender, par1Minecraft.fontRenderer, iconItem, xPosition, yPosition);

			this.mouseDragged(par1Minecraft, par2, par3);

			GL11.glColor4d(1, 1, 1, 1);
		}
	}

}

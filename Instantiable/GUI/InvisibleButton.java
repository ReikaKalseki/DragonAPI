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
import net.minecraft.client.gui.GuiButton;

public class InvisibleButton extends GuiButton {

	public InvisibleButton(int par1, int par2, int par3, int par4, int par5)
	{
		super(par1, par2, par3, par4, par5, "");
	}

	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
	{
		//super.drawButton(par1Minecraft, par2, par3);
	}

}
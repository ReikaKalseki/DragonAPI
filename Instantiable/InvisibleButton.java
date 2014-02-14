package Reika.DragonAPI.Instantiable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class InvisibleButton extends GuiButton {

	public InvisibleButton(int par1, int par2, int par3, int par4, int par5, String par6Str)
	{
		super(par1, par2, par3, par4, par5, par6Str);
	}

	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
	{
		//super.drawButton(par1Minecraft, par2, par3);
	}

}

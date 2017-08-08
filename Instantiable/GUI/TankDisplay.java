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

import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTankInfo;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaLiquidRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TankDisplay {

	private FluidTankInfo tank;
	public final int posX;
	public final int posY;
	public final int xSize;
	public final int ySize;

	public TankDisplay(FluidTankInfo fl, int x, int y, int w, int h) {
		tank = fl;
		posX = x;
		posY = y;
		xSize = w;
		ySize = h;
	}

	public TankDisplay updateTank(FluidTankInfo fl) {
		tank = fl;
		return this;
	}

	private int getLevel() {
		return tank.fluid != null ? tank.fluid.amount : 0;
	}

	private int getRenderLevel() {
		float frac = this.getLevel()/(float)tank.capacity;
		return (int)(frac*ySize);
	}

	private Fluid getFluid() {
		return tank.fluid != null ? tank.fluid.getFluid() : null;
	}

	private IIcon getIcon() {
		return this.getFluid().getIcon();
	}

	public void render(boolean lines) {
		Fluid f = this.getFluid();
		if (f == null)
			return;
		IIcon ico = this.getIcon();
		ReikaLiquidRenderer.bindFluidTexture(f);
		int yh = posY+ySize-this.getRenderLevel();
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(posX, yh, ico, xSize, this.getRenderLevel());
		lines = false;
		if (lines) {
			int dist = ySize*1000/tank.capacity*4;
			for (int i = 0; i < ySize; i += dist)
				ReikaGuiAPI.instance.drawLine(posX+xSize-xSize/4, posY+i, posX+xSize, posY+i, 0xff0000);
		}
	}

}

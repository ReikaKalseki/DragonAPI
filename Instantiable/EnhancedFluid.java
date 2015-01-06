/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class EnhancedFluid extends Fluid {

	private String ingame;
	private int color;

	public EnhancedFluid(String fluidName) {
		super(fluidName);
	}

	public EnhancedFluid setGameName(String name) {
		ingame = name;
		return this;
	}

	public EnhancedFluid setColor(int rgb) {
		color = rgb;
		return this;
	}

	@Override
	public final String getLocalizedName(FluidStack fs)
	{
		return ingame != null ? ingame : super.getLocalizedName(fs);
	}

	@Override
	public final int getColor()
	{
		return color;
	}

}

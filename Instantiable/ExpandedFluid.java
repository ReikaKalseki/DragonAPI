/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraftforge.fluids.Fluid;

public class ExpandedFluid extends Fluid {

	private String ingame;
	private int color;

	public ExpandedFluid(String fluidName) {
		super(fluidName);
	}

	public ExpandedFluid setGameName(String name) {
		ingame = name;
		return this;
	}

	public ExpandedFluid setColor(int rgb) {
		color = rgb;
		return this;
	}

	@Override
	public final String getLocalizedName()
	{
		return ingame != null ? ingame : super.getLocalizedName();
	}

	@Override
	public final int getColor()
	{
		return color;
	}

}

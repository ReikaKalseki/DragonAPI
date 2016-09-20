/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.ModInteract;

import java.util.List;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;

public class PositionedStackWithTooltip extends PositionedStack {

	public final String tooltip;

	public PositionedStackWithTooltip(Object o, int x, int y, String s)
	{
		super(o, x, y);
		tooltip = s;
	}

	public List<String> handleTooltip(GuiRecipe gui, List<String> currenttip) {
		if (tooltip != null && !tooltip.isEmpty()) {
			currenttip.add(tooltip);
		}
		return currenttip;
	}
}

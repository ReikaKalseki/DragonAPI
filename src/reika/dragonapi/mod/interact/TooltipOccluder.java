/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact;

import java.util.Collection;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.lwjgl.util.Rectangle;
import reika.dragonapi.libraries.io.ReikaGuiAPI;
import codechicken.lib.vec.Rectangle4i;
import codechicken.nei.api.INEIGuiAdapter;

public class TooltipOccluder extends INEIGuiAdapter {

	@Override
	public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h)
	{
		Rectangle4i item = new Rectangle4i(x, y, w, h);
		Collection<Rectangle> c = ReikaGuiAPI.instance.getTooltips().values();
		//ReikaJavaLibrary.pConsole(c);
		for (Rectangle r : c) {
			Rectangle4i r4 = new Rectangle4i(r.getX(), r.getY(), r.getWidth(), r.getHeight());
			if (r4 != null && r4.intersects(item)) {
				return true;
			}
		}
		return false;
	}

}

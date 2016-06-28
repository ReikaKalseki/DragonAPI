/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.event;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class NEIRecipeCheckEvent extends Event {

	private final ItemStack item;
	public final GuiContainer gui;

	public NEIRecipeCheckEvent(GuiContainer gui) {
		this(gui, GuiContainerManager.getStackMouseOver(gui));
	}

	public NEIRecipeCheckEvent(GuiContainer gui, ItemStack item) {
		this.gui = gui;
		this.item = item;
	}

	public ItemStack getItem() {
		return item != null ? item.copy() : null;
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.deepinteract;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import reika.dragonapi.instantiable.event.NEIRecipeCheckEvent;
import codechicken.nei.ItemPanel;
import codechicken.nei.ItemPanel.ItemPanelSlot;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIController;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.recipe.GuiRecipe;

/** Allows for loading custom GUIs on NEI recipe checks. */
public class NEIIntercept implements IContainerInputHandler {

	public static final NEIIntercept instance = new NEIIntercept();

	private NEIIntercept() {

	}

	public void register() {
		GuiContainerManager.inputHandlers.add(this);
	}

	@Override
	public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {return false;}

	@Override
	public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {}

	@Override
	public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyID) {
		return keyID == NEIClientConfig.getKeyBinding("gui.recipe") && !NEIClientConfig.isHidden() && MinecraftForge.EVENT_BUS.post(new NEIRecipeCheckEvent(gui));
	}

	@Override
	public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
		if (button == 0) {
			if (gui instanceof GuiRecipe && !NEIClientConfig.isHidden())
				return MinecraftForge.EVENT_BUS.post(new NEIRecipeCheckEvent(gui));
			else {
				ItemPanel panel = LayoutManager.itemPanel;
				if (panel != null) {
					ItemPanelSlot slot = panel.getSlotMouseOver(mousex, mousey);
					if (slot != null && panel.draggedStack == null) {
						ItemStack item = slot.item;
						if (NEIController.manager.window instanceof GuiRecipe || !NEIClientConfig.canCheatItem(item)) {
							if (button == 0) {
								return MinecraftForge.EVENT_BUS.post(new NEIRecipeCheckEvent(gui, item));
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onMouseClicked(GuiContainer gui, int mousex, int mousey, int button) {}

	@Override
	public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {}

	@Override
	public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {return false;}

	@Override
	public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {}

	@Override
	public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) {}

}

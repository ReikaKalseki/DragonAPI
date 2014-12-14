/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Instantiable.Event.NEIRecipeCheckEvent;
import codechicken.nei.NEIClientConfig;
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
		return keyID == NEIClientConfig.getKeyBinding("gui.recipe") && MinecraftForge.EVENT_BUS.post(new NEIRecipeCheckEvent(gui));
	}

	@Override
	public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
		return button == 0 && gui instanceof GuiRecipe && MinecraftForge.EVENT_BUS.post(new NEIRecipeCheckEvent(gui));
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

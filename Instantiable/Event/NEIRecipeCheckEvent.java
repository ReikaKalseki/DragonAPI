package Reika.DragonAPI.Instantiable.Event;

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
		this.gui = gui;
		item = GuiContainerManager.getStackMouseOver(gui);
	}

	public ItemStack getItem() {
		return item != null ? item.copy() : null;
	}

}

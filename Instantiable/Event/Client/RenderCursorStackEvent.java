package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class RenderCursorStackEvent extends RenderItemInSlotEvent {

	public ItemStack itemToRender;

	public RenderCursorStackEvent(GuiContainer c, ItemStack is, int x, int y) {
		super(c, is, x, y);

		itemToRender = this.getItem();
	}

}

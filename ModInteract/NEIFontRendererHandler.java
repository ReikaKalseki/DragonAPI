package Reika.DragonAPI.ModInteract;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.lib.gui.GuiDraw;
import codechicken.lib.gui.GuiDraw.ITooltipLineHandler;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerTooltipHandler;

/** Used to make NEI respect the result Item.getFontRenderer() when rendering tooltips, something it normally does not do. */
public class NEIFontRendererHandler implements IContainerTooltipHandler {

	public static final NEIFontRendererHandler instance = new NEIFontRendererHandler();

	private ItemStack item;
	private List<String> strings;

	private NEIFontRendererHandler() {

	}

	public void register() {
		GuiContainerManager.addTooltipHandler(this);
	}

	private class LineHandler implements ITooltipLineHandler {

		private final String string;
		private final FontRenderer font;

		private LineHandler(String s) {
			string = s;
			font = item.getItem().getFontRenderer(item);
		}

		@Override
		public Dimension getSize() {
			return new Dimension(font.getStringWidth(string), string.endsWith(GuiDraw.TOOLTIP_LINESPACE) ? 12 : 10);
		}

		@Override
		public void draw(int x, int y) {
			font.drawStringWithShadow(string, x, y, -1);
		}

	}

	@Override
	public List<String> handleTooltip(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
		return currenttip;
	}

	@Override
	public List<String> handleItemDisplayName(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
		return this.interceptValuesAndRegisterHandlers(itemstack, currenttip);
	}

	@Override
	public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, int mousex, int mousey, List<String> currenttip) {
		return this.interceptValuesAndRegisterHandlers(itemstack, currenttip);
	}

	private List<String> interceptValuesAndRegisterHandlers(ItemStack itemstack, List<String> currenttip) {
		if (itemstack == null)
			return currenttip;
		item = itemstack;
		strings = new ArrayList();
		for (int i = 0; i < currenttip.size(); i++) {
			String s = currenttip.get(i);
			String pre = GuiDraw.TOOLTIP_HANDLER+GuiDraw.getTipLineId(new LineHandler(s));
			strings.add(pre);
		}
		return strings;
	}



}

package Reika.DragonAPI.Instantiable.GUI;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import Reika.DragonAPI.Interfaces.Registry.TileEnum;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface GuiItemDisplay {

	@SideOnly(Side.CLIENT)
	public abstract void draw(FontRenderer fr, int x, int y);

	public static class GuiStackDisplay implements GuiItemDisplay {

		private static final RenderItem renderer = new RenderItem();

		private final ItemStack item;

		public GuiStackDisplay(String s) {
			this(ReikaItemHelper.lookupItem(s));
		}

		public GuiStackDisplay(TileEnum m) {
			this(m.getCraftedProduct());
		}

		public GuiStackDisplay(Item b) {
			this(new ItemStack(b));
		}

		public GuiStackDisplay(Block b) {
			this(new ItemStack(b));
		}

		public GuiStackDisplay(ItemStack is) {
			item = is == null ? null : is.copy();
		}

		@Override
		public void draw(FontRenderer fr, int x, int y) {
			if (item != null)
				ReikaGuiAPI.instance.drawItemStack(renderer, fr, item, x, y);
		}

		public boolean isEmpty() {
			return item == null || item.getItem() == null;
		}

	}

	public static class GuiIconDisplay implements GuiItemDisplay {

		public final IIcon icon;

		public GuiIconDisplay(IIcon ico) {
			icon = ico;
		}

		@Override
		public void draw(FontRenderer fr, int x, int y) {
			ReikaTextureHelper.bindTerrainTexture();
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y, icon, 16, 16);
		}

	}

}

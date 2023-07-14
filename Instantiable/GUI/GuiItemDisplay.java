package Reika.DragonAPI.Instantiable.GUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import Reika.DragonAPI.Interfaces.Registry.TileEnum;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface GuiItemDisplay extends Comparable<GuiItemDisplay> {

	@SideOnly(Side.CLIENT)
	public abstract void draw(FontRenderer fr, int x, int y);

	public boolean isEmpty();

	public static class GuiStackDisplay implements GuiItemDisplay {

		//@SideOnly(Side.CLIENT)
		//private static final RenderItem renderer = new RenderItem();

		protected ItemStack item;

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
		@SideOnly(Side.CLIENT)
		public void draw(FontRenderer fr, int x, int y) {
			if (item != null)
				ReikaGuiAPI.instance.drawItemStackWithTooltip(ReikaGuiAPI.itemRenderer, fr, item, x, y);
		}

		public boolean isEmpty() {
			return item == null || item.getItem() == null;
		}

		@Override
		public int compareTo(GuiItemDisplay o) {
			return o instanceof GuiStackDisplay ? ReikaItemHelper.comparator.compare(item, ((GuiStackDisplay)o).item) : 1;
		}

		public ItemStack getItem() {
			return this.isEmpty() ? null : item.copy();
		}

	}

	public static abstract class GuiMultiStackDisplay extends GuiStackDisplay {

		public GuiMultiStackDisplay() {
			super((ItemStack)null);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void draw(FontRenderer fr, int x, int y) {
			item = this.chooseItem();
			super.draw(fr, x, y);
		}

		protected abstract ItemStack chooseItem();

		@Override
		public abstract boolean isEmpty();

		@Override
		public abstract int compareTo(GuiItemDisplay o);
	}

	public static class GuiStackListDisplay extends GuiMultiStackDisplay {

		private final ArrayList<ItemStack> items = new ArrayList();
		public int cycleSpeed = 1000;

		public GuiStackListDisplay() {
			super();
		}

		public GuiStackListDisplay(ItemStack... c) {
			super();
			this.addItems(c);
		}

		public GuiStackListDisplay(Collection<ItemStack> c) {
			super();
			this.addItems(c);
		}

		public GuiStackListDisplay addItems(Collection<ItemStack> li) {
			items.addAll(li);
			Collections.sort(items, ReikaItemHelper.comparator);
			return this;
		}

		public GuiStackListDisplay addItems(ItemStack... li) {
			for (ItemStack is : li)
				items.add(is);
			Collections.sort(items, ReikaItemHelper.comparator);
			return this;
		}

		public GuiItemDisplay setCycleSpeed(int speed) {
			cycleSpeed = speed;
			return this;
		}

		@Override
		@SideOnly(Side.CLIENT)
		protected ItemStack chooseItem() {
			int idx = (int)((System.currentTimeMillis()/(GuiScreen.isShiftKeyDown() ? cycleSpeed/4 : cycleSpeed))%items.size());
			return items.get(idx);
		}

		@Override
		public boolean isEmpty() {
			return items.isEmpty();
		}

		@Override
		public int compareTo(GuiItemDisplay o) {
			return o instanceof GuiStackListDisplay ? ReikaItemHelper.itemListComparator.compare(items, ((GuiStackListDisplay)o).items) : 1;
		}

	}

	public static class GuiIconDisplay implements GuiItemDisplay {

		public final IconProvider iconFetch;

		public GuiIconDisplay(IIcon ico) {
			this(() -> ico);
		}

		public GuiIconDisplay(IconProvider ico) {
			iconFetch = ico;
		}

		private IIcon getIcon() {
			return iconFetch == null ? ReikaTextureHelper.getMissingIcon() : iconFetch.getIcon();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void draw(FontRenderer fr, int x, int y) {
			ReikaTextureHelper.bindTerrainTexture();
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y, this.getIcon(), 16, 16);
		}

		@Override
		public int compareTo(GuiItemDisplay o) {
			return o instanceof GuiIconDisplay ? this.getIcon().getIconName().compareToIgnoreCase(((GuiIconDisplay)o).getIcon().getIconName()) : -1;
		}

		public boolean isEmpty() {
			return iconFetch == null;
		}

	}

	public static interface IconProvider {

		public IIcon getIcon();

	}

}

package Reika.DragonAPI.Instantiable;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.MatchMode;

public abstract class ItemFilter {

	protected ItemFilter() {

	}

	public abstract void writeToNBT(NBTTagCompound tag);
	public abstract void readFromNBT(NBTTagCompound tag);

	public abstract boolean matches(ItemStack is);

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object o);

	public abstract ItemStack getItem();

	public static final class ItemCategoryMatch extends ItemFilter {

		private static final HashMap<String, ItemCategory> categories = new HashMap();

		private ItemCategory category;

		public ItemCategoryMatch(ItemCategory cat) {
			category = cat;
		}

		public static void addCategory(ItemCategory cat) {
			categories.put(cat.getID(), cat);
		}

		public static ItemCategory getCategory(String cat) {
			return categories.get(cat);
		}

		public static enum BasicCategories implements ItemCategory {
			ORE(),
			MOBDROP();

			private BasicCategories() {
				addCategory(this);
			}

			@Override
			public boolean isItemInCategory(ItemStack is) {
				switch(this) {
					case ORE:
						return ReikaBlockHelper.isOre(is);
					case MOBDROP:
						break;
				}
				return false;
			}

			@Override
			public String getID() {
				return this.name();
			}
		}

		public static interface ItemCategory {

			public boolean isItemInCategory(ItemStack is);

			public String getID();

		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			tag.setString("id", category.getID());
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			category = getCategory(tag.getString("id"));
		}

		@Override
		public boolean matches(ItemStack is) {
			return category.isItemInCategory(is);
		}

		@Override
		public int hashCode() {
			return category.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof ItemCategoryMatch && category.equals(((ItemCategoryMatch)o).category);
		}

		@Override
		public ItemStack getItem() {
			return null;
		}

	}

	public static final class ItemRule extends ItemFilter {

		private ItemStack item;
		private MatchMode mode;

		public ItemRule(ItemStack is) {
			this(is, MatchMode.EXACTNONBT);
		}

		public ItemRule(ItemStack is, MatchMode m) {
			item = ReikaItemHelper.getSizedItemStack(is, is.getMaxStackSize());
			mode = m;
		}

		@Override
		public boolean matches(ItemStack is) {
			return mode.compare(is, item);
		}

		@Override
		public int hashCode() {
			return item.getItem().hashCode() ^ mode.ordinal();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof ItemRule) {
				ItemRule ir = (ItemRule)o;
				return ItemStack.areItemStacksEqual(item, ir.item) && mode == ir.mode;
			}
			return false;
		}

		@Override
		public String toString() {
			return item+" * "+mode;
		}

		@Override
		public ItemStack getItem() {
			return item.copy();
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			ItemStack is = ItemStack.loadItemStackFromNBT(tag);
			item = is;
			mode = MatchMode.list[tag.getInteger("mode")];
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			tag.setInteger("mode", mode.ordinal());
			item.writeToNBT(tag);
		}

	}

}

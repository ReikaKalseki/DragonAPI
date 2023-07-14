package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Strings;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiIconDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiStackDisplay;

public abstract class ItemSpecificEffectDescription {

	public abstract String getDescription(GuiItemDisplay i);
	public abstract List<GuiItemDisplay> getRelevantItems();

	public static class ItemListEffectDescription extends ItemSpecificEffectDescription implements Comparable<ItemListEffectDescription> {

		public final String description;
		private final ArrayList<GuiItemDisplay> items = new ArrayList();

		private int ordering;

		public ItemListEffectDescription(String s) {
			description = s;
		}

		public final ItemListEffectDescription setOrderIndex(int index) {
			ordering = index;
			return this;
		}

		public final ItemListEffectDescription addIcons(IIcon... set) {
			for (IIcon is : set)
				items.add(new GuiIconDisplay(is));
			Collections.sort(items);
			return this;
		}

		public final ItemListEffectDescription addItems(Collection<ItemStack> c) {
			for (ItemStack is : c) {
				if (is.getItem() == null)
					throw new IllegalArgumentException("Null item!");
				items.add(new GuiStackDisplay(is));
			}
			Collections.sort(items);
			return this;
		}

		public final ItemListEffectDescription addItems(ItemStack... set) {
			for (ItemStack is : set) {
				if (is.getItem() == null)
					throw new IllegalArgumentException("Null item!");
				items.add(new GuiStackDisplay(is));
			}
			Collections.sort(items);
			return this;
		}

		public final ItemListEffectDescription addDisplays(Collection<GuiItemDisplay> set) {
			for (GuiItemDisplay is : set) {
				if (!is.isEmpty() && !items.contains(is))
					items.add(is);
			}
			Collections.sort(items);
			return this;
		}

		public final ItemListEffectDescription addDisplays(GuiItemDisplay... set) {
			for (GuiItemDisplay is : set) {
				if (!is.isEmpty() && !items.contains(is))
					items.add(is);
			}
			Collections.sort(items);
			return this;
		}

		@Override
		public final List<GuiItemDisplay> getRelevantItems() {
			return Collections.unmodifiableList(items);
		}

		@Override
		public String toString() {
			return description+": "+items;
		}

		@Override
		public final int compareTo(ItemListEffectDescription o) {
			if (o.ordering != ordering)
				return Integer.compare(ordering, o.ordering);
			if (Strings.isNullOrEmpty(description) && Strings.isNullOrEmpty(o.description))
				return 0;
			else if (Strings.isNullOrEmpty(description))
				return -1;
			else if (Strings.isNullOrEmpty(o.description))
				return 1;
			else
				return description.compareToIgnoreCase(o.description);
		}

		@Override
		public String getDescription(GuiItemDisplay i) {
			return description;
		}

	}

}

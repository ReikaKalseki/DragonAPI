package Reika.DragonAPI.Instantiable.Recipe;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class FlexibleIngredient {

	private final ItemMatch filter;
	public final float chanceToUse;
	public final int numberToUse;

	private FlexibleIngredient(Block in, float chance, int toDecr) {
		this(new ItemStack(in), chance, toDecr);
	}

	private FlexibleIngredient(Item in, float chance, int toDecr) {
		this(new ItemStack(in), chance, toDecr);
	}

	private FlexibleIngredient(ItemStack in, float chance, int toDecr) {
		this(in != null ? ReikaJavaLibrary.makeListFrom(in) : null, chance, toDecr);
	}

	private FlexibleIngredient(String ore, float chance, int toDecr) {
		this(OreDictionary.getOres(ore), chance, toDecr);
	}

	private FlexibleIngredient(Collection<ItemStack> in, float chance, int toDecr) {
		filter = in != null ? new ItemMatch(in) : null;
		chanceToUse = chance/100F;
		numberToUse = toDecr;
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getItemForDisplay() {
		if (!this.exists())
			return null;
		int tick = (int)((System.currentTimeMillis()/1000)%display.size());
		return ReikaItemHelper.getSizedItemStack(display.get(tick), numberToUse);
	}

	@Override
	public String toString() {
		return fullIDKeys(items)+" x"+numberToUse+"@"+chanceToUse+"%";
	}

	public boolean match(ItemStack in) {
		return !this.exists() ? in == null : this.isItemCorrect(in) && in.stackSize >= numberToUse;
	}

	private boolean isItemCorrect(ItemStack in) {
		return in != null && items.contains(new KeyedItemStack(in).setSimpleHash(true));
	}

	public boolean exists() {
		return !filter.isEmpty();
	}

	public Collection<ItemStack> getItems() {
		ArrayList<ItemStack> c = new ArrayList();
		for (KeyedItemStack ks : items) {
			c.add(ks.getItemStack());
		}
		return c;
	}
}

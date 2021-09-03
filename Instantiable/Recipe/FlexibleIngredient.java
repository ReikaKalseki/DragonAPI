package Reika.DragonAPI.Instantiable.Recipe;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.IO.CustomRecipeList;
import Reika.DragonAPI.Instantiable.IO.LuaBlock;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class FlexibleIngredient {

	public static final FlexibleIngredient EMPTY = new FlexibleIngredient();

	private final ItemMatch filter;
	public final float chanceToUse;
	public final int numberToUse;

	private FlexibleIngredient() {
		this((ItemMatch)null, 0, 1);
	}

	public FlexibleIngredient(Block in, float chance, int toDecr) {
		this(new ItemMatch(in), chance, toDecr);
	}

	public FlexibleIngredient(Item in, float chance, int toDecr) {
		this(new ItemMatch(in), chance, toDecr);
	}

	public FlexibleIngredient(ItemStack in, float chance, int toDecr) {
		this(in != null ? new ItemMatch(in) : null, chance, toDecr);
	}

	public FlexibleIngredient(String ore, float chance, int toDecr) {
		this(OreDictionary.getOres(ore), chance, toDecr);
	}

	public FlexibleIngredient(Collection<ItemStack> in, float chance, int toDecr) {
		this(in != null ? new ItemMatch(in) : null, chance, toDecr);
	}

	public FlexibleIngredient(ItemMatch in, float chance, int toDecr) {
		filter = in;
		chanceToUse = chance/100F;
		numberToUse = toDecr;
	}

	public void addItem(ItemStack is) {
		if (filter != null) {
			filter.addItem(new KeyedItemStack(is).setIgnoreMetadata(false).setIgnoreNBT(true).setSized(false).setSimpleHash(true));
		}
	}

	public FlexibleIngredient lock() {
		if (filter != null) {
			for (KeyedItemStack ks : filter.getItemList()) {
				ks.lock();
			}
		}
		return this;
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getItemForDisplay(boolean size) {
		if (!this.exists())
			return null;
		return ReikaItemHelper.getSizedItemStack(filter.getCycledItem(), size ? numberToUse : 1);
	}

	@Override
	public String toString() {
		return filter.getItemList()+" x"+numberToUse+"@"+chanceToUse+"%";
	}

	public String fullID(IngredientIDHandler id) {
		return this.exists() ? id.fullIDForItems(filter.getItemList())+" x"+numberToUse+"@"+chanceToUse+"%" : "Empty";
	}

	/** Does NOT check size, just identity! */
	public boolean match(ItemStack in) {
		return this.exists() ? filter.match(in) : in == null;
	}

	public boolean isSufficient(ItemStack is) {
		return this.exists() ? is.stackSize >= numberToUse : true;
	}

	public boolean exists() {
		return filter != null && !filter.isEmpty();
	}

	public Collection<ItemStack> getItems() {
		ArrayList<ItemStack> c = new ArrayList();
		for (KeyedItemStack ks : filter.getItemList()) {
			c.add(ks.getItemStack());
		}
		return c;
	}

	public static FlexibleIngredient parseLua(CustomRecipeList crl, LuaBlock b, boolean allowEmptyItemList) {
		if (b == null)
			return EMPTY;
		Collection<ItemStack> li = b.hasChild("items") ? crl.parseItemCollection(b.getChild("items").getDataValues(), true) : null;
		if (li == null || li.isEmpty()) {
			if (allowEmptyItemList)
				return EMPTY;
			else
				throw new RuntimeException("Lua block "+b.name+" found no items!");
		}
		int num = b.getInt("number_to_use");
		if (num <= 0)
			throw new IllegalArgumentException("No number to use specified!");
		if (!b.containsKeyInherit("consumption_chance"))
			throw new IllegalArgumentException("No consumption chance specified!");
		return new FlexibleIngredient(li, (float)b.getDouble("consumption_chance"), num);
	}

	public static interface IngredientIDHandler {

		public String fullIDForItems(Collection<KeyedItemStack> c);

	}
}

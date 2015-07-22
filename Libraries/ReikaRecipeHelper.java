/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Recipe.ExpandedOreRecipe;
import Reika.DragonAPI.Instantiable.Recipe.RecipePattern;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ReikaRecipeHelper extends DragonAPICore {

	private static final CraftingManager cr = CraftingManager.getInstance();
	private static final List<IRecipe> recipes = cr.getRecipeList();

	private static final Random rand = new Random();

	private static final int[] permuOffsets = new int[9];

	private static final HashMap<IRecipe, RecipeCache> recipeCache = new HashMap();
	private static final HashMap<IRecipe, RecipeCache> recipeCacheClient = new HashMap();

	private static Field shapedOreHeight;
	private static Field shapedOreWidth;
	private static Field shapedOreInput;

	private static class RecipeCache {
		private final List<ItemStack>[] items;
		private final int width;
		private final int height;

		private RecipeCache(List<ItemStack>[] items, int w, int h) {
			this.items = items;
			width = w;
			height = h;
		}
	}

	static {
		try {
			shapedOreHeight = ShapedOreRecipe.class.getDeclaredField("height");
			shapedOreWidth = ShapedOreRecipe.class.getDeclaredField("width");
			shapedOreInput = ShapedOreRecipe.class.getDeclaredField("input");

			shapedOreHeight.setAccessible(true);
			shapedOreWidth.setAccessible(true);
			shapedOreInput.setAccessible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void overwriteShapedOreRecipeInput(ShapedOreRecipe s, Object[] in, int height, int width) {
		try {
			shapedOreInput.set(s, in);
			shapedOreHeight.set(s, height);
			shapedOreWidth.set(s, width);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getOreRecipeHeight(ShapedOreRecipe s) {
		try {
			return shapedOreHeight.getInt(s);
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static int getOreRecipeWidth(ShapedOreRecipe s) {
		try {
			return shapedOreWidth.getInt(s);
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/** Finds a recipe by its product. */
	public static IRecipe getRecipeByOutput(ItemStack out) {
		return recipes.get(recipes.indexOf(out));
	}

	/** Finds recipes by product. */
	public static List<IRecipe> getRecipesByOutput(ItemStack out) {
		List<IRecipe> li = new ArrayList<IRecipe>();
		for (int i = 0; i < recipes.size(); i++) {
			IRecipe ir = recipes.get(i);
			if (ItemStack.areItemStacksEqual(ir.getRecipeOutput(), out))
				li.add(ir);
		}
		return li;
	}

	/** Finds recipes by product. */
	public static List<ShapedRecipes> getShapedRecipesByOutput(ItemStack out) {
		List<ShapedRecipes> li = new ArrayList<ShapedRecipes>();
		for (int i = 0; i < recipes.size(); i++) {
			IRecipe ir = recipes.get(i);
			if (ir instanceof ShapedRecipes) {
				if (ItemStack.areItemStacksEqual(ir.getRecipeOutput(), out))
					li.add((ShapedRecipes)ir);
			}
		}
		return li;
	}

	/** Finds recipes by product. */
	public static List<ShapedRecipes> getShapedRecipesByOutput(List<IRecipe> in, ItemStack out) {
		List<ShapedRecipes> li = new ArrayList<ShapedRecipes>();
		for (int i = 0; i < in.size(); i++) {
			IRecipe ir = in.get(i);
			//ReikaJavaLibrary.pConsole(ir.getRecipeOutput()+" == "+out);
			if (ir instanceof ShapedRecipes) {
				if (ReikaItemHelper.matchStacks(ir.getRecipeOutput(), out))
					li.add((ShapedRecipes)ir);
			}
		}
		//ReikaJavaLibrary.pConsole(li);
		return li;
	}

	/** Returns the item in a shaped recipe at x, y in the grid. */
	public static ItemStack getItemInRecipeAtXY(ShapedRecipes r, int x, int y) {
		int xy = x+r.recipeWidth*y;
		return r.recipeItems[xy];
	}

	/** Finds recipes by product. */
	public static ArrayList<IRecipe> getAllRecipesByOutput(List<IRecipe> in, ItemStack out) {
		ArrayList<IRecipe> li = new ArrayList<IRecipe>();
		for (IRecipe ir : in) {
			//ReikaJavaLibrary.pConsole(ir.getRecipeOutput()+" == "+out);
			if (ReikaItemHelper.matchStacks(ir.getRecipeOutput(), out))
				li.add(ir);
		}
		//ReikaJavaLibrary.pConsole(li);
		return li;
	}

	public static boolean isCraftable(ItemStack is) {
		return isCraftable(CraftingManager.getInstance().getRecipeList(), is);
	}

	public static boolean isCraftable(List<IRecipe> in, ItemStack is) {
		return getAllRecipesByOutput(in, is).size() > 0;
	}

	/** Finds recipes by product. */
	public static List<ShapedOreRecipe> getShapedOreRecipesByOutput(List<IRecipe> in, ItemStack out) {
		List<ShapedOreRecipe> li = new ArrayList<ShapedOreRecipe>();
		for (IRecipe ir : in) {
			//ReikaJavaLibrary.pConsole(ir.getRecipeOutput()+" == "+out);
			if (ir instanceof ShapedOreRecipe) {
				if (ReikaItemHelper.matchStacks(ir.getRecipeOutput(), out))
					li.add((ShapedOreRecipe)ir);
			}
		}
		//ReikaJavaLibrary.pConsole(li);
		return li;
	}

	/** Finds recipes by product. */
	public static List<ExpandedOreRecipe> getExpandedOreRecipesByOutput(List<IRecipe> in, ItemStack out) {
		List<ExpandedOreRecipe> li = new ArrayList<ExpandedOreRecipe>();
		for (IRecipe ir : in) {
			//ReikaJavaLibrary.pConsole(ir.getRecipeOutput()+" == "+out);
			if (ir instanceof ExpandedOreRecipe) {
				if (ReikaItemHelper.matchStacks(ir.getRecipeOutput(), out))
					li.add((ExpandedOreRecipe)ir);
			}
		}
		//ReikaJavaLibrary.pConsole(li);
		return li;
	}

	/** Finds recipes by product. */
	public static List<ShapelessRecipes> getShapelessRecipesByOutput(List<IRecipe> in, ItemStack out) {
		List<ShapelessRecipes> li = new ArrayList<ShapelessRecipes>();
		for (IRecipe ir : in) {
			if (ir instanceof ShapelessRecipes) {
				if (ReikaItemHelper.matchStacks(ir.getRecipeOutput(), out))
					li.add((ShapelessRecipes)ir);
			}
		}
		//ReikaJavaLibrary.pConsole(li);
		return li;
	}

	/** Finds recipes by product. */
	public static List<ShapelessOreRecipe> getShapelessOreRecipesByOutput(List<IRecipe> in, ItemStack out) {
		List<ShapelessOreRecipe> li = new ArrayList<ShapelessOreRecipe>();
		for (IRecipe ir : in) {
			if (ir instanceof ShapelessOreRecipe) {
				if (ReikaItemHelper.matchStacks(ir.getRecipeOutput(), out))
					li.add((ShapelessOreRecipe)ir);
			}
		}
		//ReikaJavaLibrary.pConsole(li);
		return li;
	}

	private static List<ItemStack> getRecipeItemStack(ItemStack is, boolean client) {
		if (is == null)
			return null;
		if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE && client)
			return ReikaItemHelper.getAllMetadataPermutations(is.getItem());
		else
			return ReikaJavaLibrary.makeListFrom(is);
	}

	private static RecipeCache getRecipeCacheObject(IRecipe ir, boolean client) {
		HashMap<IRecipe, RecipeCache> map = client ? recipeCacheClient : recipeCache;
		RecipeCache cache = map.get(ir);
		if (cache == null) {
			cache = calculateRecipeToItemStackArray(ir, client);
			map.put(ir, cache);
		}
		return cache;
	}

	/** Turns a recipe into a 3x3 itemstack array. Args: Recipe */
	public static List<ItemStack>[] getRecipeArray(IRecipe ir) {
		List<ItemStack>[] lists = new List[9];
		for (int i = 0; i < 9; i++) {
			List li = getRecipeCacheObject(ir, false).items[i];
			if (li != null && !li.isEmpty())
				lists[i] = Collections.unmodifiableList(li);
		}
		return lists;
	}

	/** Turns a recipe into a 3x3 itemstack array, permuting it as well, usually for rendering. Args: Recipe */
	@SideOnly(Side.CLIENT)
	public static ItemStack[] getPermutedRecipeArray(IRecipe ir) {
		RecipeCache r = getRecipeCacheObject(ir, true);
		List<ItemStack>[] isin = r.items;

		int time = 1000;
		for (int i = 0; i < isin.length; i++) {
			if (isin[i] != null && !isin[i].isEmpty()) {
				if (System.currentTimeMillis()%time == 0) {
					permuOffsets[i] = rand.nextInt(isin[i].size());
				}
			}
		}

		int[] indices = new int[9];
		ItemStack[] add = new ItemStack[9];
		for (int i = 0; i < 9; i++) {
			List<ItemStack> li = isin[i];
			if (li != null && !li.isEmpty()) {
				int tick = (int)(((System.currentTimeMillis()/time)+permuOffsets[i])%li.size());
				add[i] = li.get(tick);
			}
		}

		ItemStack[] in = new ItemStack[9];
		if (r.width == 3 && r.height == 3) {
			for (int i = 0; i < 9; i++)
				in[i] = add[i];
		}
		if (r.width == 1 && r.height == 1) {
			in[4] = add[0];
		}
		if (r.width == 2 && r.height == 2) {
			in[0] = add[0];
			in[1] = add[1];
			in[3] = add[2];
			in[4] = add[3];
		}
		if (r.width == 1 && r.height == 2) {
			in[4] = add[0];
			in[7] = add[1];
		}
		if (r.width == 2 && r.height == 1) {
			in[0] = add[0];
			in[1] = add[1];
		}
		if (r.width == 3 && r.height == 1) {
			in[0] = add[0];
			in[1] = add[1];
			in[2] = add[2];
		}
		if (r.width == 1 && r.height == 3) {
			in[1] = add[0];
			in[4] = add[1];
			in[7] = add[2];
		}
		if (r.width == 2 && r.height == 3) {
			in[0] = add[0];
			in[1] = add[1];
			in[3] = add[2];
			in[4] = add[3];
			in[6] = add[4];
			in[7] = add[5];
		}
		if (r.width == 3 && r.height == 2) {
			in[3] = add[0];
			in[4] = add[1];
			in[5] = add[2];
			in[6] = add[3];
			in[7] = add[4];
			in[8] = add[5];
		}

		return in;
	}

	private static RecipeCache calculateRecipeToItemStackArray(IRecipe ire, boolean client) {
		List<ItemStack>[] isin = new List[9];
		int num;
		int w = 0;
		int h = 0;
		if (ire == null)
			ReikaJavaLibrary.pConsole("Recipe is null!");
		if (ire == null) {
			ReikaJavaLibrary.dumpStack();
			return null;
		}
		if (ire instanceof ShapedRecipes) {
			ShapedRecipes r = (ShapedRecipes)ire;
			num = r.recipeItems.length;
			w = r.recipeWidth;
			h = r.recipeHeight;
			for (int i = 0; i < r.recipeItems.length; i++) {
				ItemStack is = r.recipeItems[i];
				isin[i] = getRecipeItemStack(is, client);
			}

		}
		else if (ire instanceof ShapedOreRecipe) {
			ShapedOreRecipe so = (ShapedOreRecipe)ire;
			Object[] objin = so.getInput();
			//ReikaJavaLibrary.pConsole(Arrays.toString(objin));
			w = 3;
			h = 3;
			for (int i = 0; i < objin.length; i++) {
				if (objin[i] instanceof ItemStack) {
					ItemStack is = (ItemStack)objin[i];
					isin[i] = getRecipeItemStack(is, client);
				}
				else if (objin[i] instanceof List) {
					List<ItemStack> li = (List)objin[i];
					if (!li.isEmpty()) {
						isin[i] = li;
					}
				}
			}
		}
		else if (ire instanceof ExpandedOreRecipe) {
			ExpandedOreRecipe so = (ExpandedOreRecipe)ire;
			Object[] objin = so.getInputCopy();
			//ReikaJavaLibrary.pConsole(Arrays.toString(objin));
			w = so.getWidth();
			h = so.getHeight();
			for (int i = 0; i < objin.length; i++) {
				if (objin[i] instanceof ItemStack) {
					ItemStack is = (ItemStack)objin[i];
					isin[i] = getRecipeItemStack(is, client);
				}
				else if (objin[i] instanceof List) {
					List<ItemStack> li = (List)objin[i];
					if (!li.isEmpty()) {
						isin[i] = li;
					}
				}
			}
		}
		else if (ire instanceof ShapelessRecipes) {
			ShapelessRecipes sr = (ShapelessRecipes)ire;
			//ReikaJavaLibrary.pConsole(ire);
			for (int i = 0; i < sr.getRecipeSize(); i++) {
				ItemStack is = (ItemStack)sr.recipeItems.get(i);
				isin[i] = getRecipeItemStack(is, client);
			}
			w = sr.getRecipeSize() >= 3 ? 3 : sr.getRecipeSize();
			h = (sr.getRecipeSize()+2)/3;
		}
		else if (ire instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe so = (ShapelessOreRecipe)ire;
			for (int i = 0; i < so.getRecipeSize(); i++) {
				Object obj = so.getInput().get(i);
				if (obj instanceof ItemStack) {
					ItemStack is = (ItemStack)obj;
					isin[i] = getRecipeItemStack(is, client);
				}
				else if (obj instanceof List) {
					List<ItemStack> li = (List)obj;
					if (!li.isEmpty()) {
						isin[i] = li;
					}
				}
				//ReikaJavaLibrary.pConsole(ire);
			}
			w = so.getRecipeSize() >= 3 ? 3 : so.getRecipeSize();
			h = (so.getRecipeSize()+2)/3;
		}

		return new RecipeCache(isin, w, h);
	}

	/** Get the smelting recipe of an item by output. Args: output */
	public static ItemStack getFurnaceInput(ItemStack out) {
		HashMap m = (HashMap)FurnaceRecipes.smelting().getSmeltingList();
		for (Object o : m.keySet()) {
			ItemStack in = (ItemStack)o;
			if (ReikaItemHelper.matchStacks(FurnaceRecipes.smelting().getSmeltingResult(in), out)) {
				return in;
			}
		}
		return null;
	}

	/** Adds a smelting recipe. Args; Item in, item out, xp */
	public static void addSmelting(ItemStack in, ItemStack out, float xp) {
		FurnaceRecipes.smelting().func_151394_a(in, out, xp);
	}

	/** Returns true if succeeded. */
	public static boolean addOreRecipe(ItemStack out, Object... in) {
		ShapedOreRecipe so = new ShapedOreRecipe(out, in);
		boolean allowed = true;
		ArrayList<String> missing = new ArrayList();
		for (int i = 0; i < in.length; i++) {
			if (in[i] instanceof String) {
				String s = (String) in[i];
				if (i > 0 && in[i-1] instanceof Character) {
					if (!ReikaItemHelper.oreItemExists(s)) {
						allowed = false;
						missing.add(s);
					}
				}
			}
		}
		if (allowed)
			GameRegistry.addRecipe(so);
		else
			ReikaJavaLibrary.pConsole("Recipe for "+out.getDisplayName()+" requires missing Ore Dictionary items "+missing+", and has not been loaded.");
		return allowed;
	}

	public static void replaceIngredientInAllRecipes(ItemStack ingredient, ItemStack replacement, boolean makeCopy) {
		if (ingredient == null)
			throw new MisuseException("You cannot replace null in recipes!");
		ArrayList<IRecipe> copies = new ArrayList();
		List<IRecipe> li = CraftingManager.getInstance().getRecipeList();
		for (IRecipe ir : li) {
			if (ir instanceof ShapedRecipes) {
				ShapedRecipes s = (ShapedRecipes) ir;
				boolean match = false;
				for (int i = 0; i < s.recipeItems.length; i++) {
					if (ReikaItemHelper.matchStacks(ingredient, s.recipeItems[i])) {
						match = true;
					}
				}
				if (match && makeCopy)
					copies.add(new ShapedRecipes(s.recipeWidth, s.recipeHeight, s.recipeItems, s.getRecipeOutput()));
				if (match) {
					for (int i = 0; i < s.recipeItems.length; i++) {
						if (ReikaItemHelper.matchStacks(ingredient, s.recipeItems[i])) {
							s.recipeItems[i] = replacement;
						}
					}
				}
			}
			else if (ir instanceof ShapelessRecipes) {
				ShapelessRecipes s = (ShapelessRecipes) ir;
				boolean match = false;
				List<ItemStack> in = s.recipeItems;
				for (int i = 0; i < in.size(); i++) {
					if (ReikaItemHelper.matchStacks(ingredient, in.get(i))) {
						match = true;
					}
				}
				if (match && makeCopy) {
					ItemStack[] inarr = new ItemStack[in.size()];
					for (int i = 0; i < inarr.length; i++) {
						inarr[i] = in.get(i);
					}
					//GameRegistry.addShapelessRecipe(s.getRecipeOutput(), inarr);
					copies.add(new ShapelessRecipes(s.getRecipeOutput(), ReikaJavaLibrary.copyList(in)));
				}
				if (match) {
					for (int i = 0; i < in.size(); i++) {
						if (ReikaItemHelper.matchStacks(ingredient, in.get(i))) {
							in.set(i, replacement);
						}
					}
				}
			}
			else if (ir instanceof ShapedOreRecipe) {
				ShapedOreRecipe s = (ShapedOreRecipe) ir;
				boolean match = false;
				Object[] in = s.getInput();
				for (int i = 0; i < in.length; i++) {
					if (in[i] instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack) in[i])) {
						match = true;
					}
				}
				if (match && makeCopy) {
					int h = getOreRecipeHeight(s);
					int w = getOreRecipeWidth(s);
					if (h > 0 && w > 0) {
						ShapedOreRecipe rec = new ShapedOreRecipe(s.getRecipeOutput(), 'B', Blocks.stone);
						//ReikaJavaLibrary.spamConsole(rec.getInput().length+":"+Arrays.toString(rec.getInput()));
						//ReikaJavaLibrary.spamConsole(in.length+":"+Arrays.toString(in));
						Object[] items = new Object[in.length];
						System.arraycopy(in, 0, items, 0, in.length);
						overwriteShapedOreRecipeInput(rec, items, h, w);
						copies.add(rec);
						//ReikaJavaLibrary.spamConsole(rec.getInput().length+":"+Arrays.toString(rec.getInput()));
						//ReikaJavaLibrary.pConsole("----------------------------------------------------");
					}
				}
				if (match) {
					for (int i = 0; i < in.length; i++) {
						if (in[i] instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack) in[i])) {
							in[i] = replacement;
						}
					}
				}
			}
			else if (ir instanceof ShapelessOreRecipe) {
				ShapelessOreRecipe s = (ShapelessOreRecipe) ir;
				boolean match = false;
				ArrayList in = s.getInput();
				for (int i = 0; i < in.size(); i++) {
					if (in.get(i) instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack) in.get(i))) {
						match = true;
					}
				}
				if (match && makeCopy) {
					Object[] inarr = new Object[in.size()];
					for (int i = 0; i < inarr.length; i++) {
						if (in.get(i) instanceof ArrayList) {
							ItemStack is = ((ArrayList<ItemStack>)in.get(i)).get(0);
							String oreName = OreDictionary.getOreName(OreDictionary.getOreID(is));
							inarr[i] = oreName;
						}
						else {
							inarr[i] = in.get(i);
						}
					}
					copies.add(new ShapelessOreRecipe(s.getRecipeOutput(), inarr));
				}
				if (match) {
					for (int i = 0; i < in.size(); i++) {
						if (in.get(i) instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack) in.get(i))) {
							in.set(i, replacement);
						}
					}
				}
			}
		}
		/*
		for (IRecipe ir : copies) {
			if (ir instanceof ShapedOreRecipe) {
				ShapedOreRecipe rec = (ShapedOreRecipe) ir;
				//ReikaJavaLibrary.spamConsole(ir.getRecipeOutput().getDisplayName()+":"+Arrays.toString(rec.getInput()));
			}
		}
		 */
		CraftingManager.getInstance().getRecipeList().addAll(copies);
	}

	public static ShapedRecipes getShapedRecipeFor(ItemStack out, Object... in) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;

		if (in[i] instanceof String[])
		{
			String[] astring = ((String[])in[i++]);

			for (int l = 0; l < astring.length; ++l)
			{
				String s1 = astring[l];
				++k;
				j = s1.length();
				s = s + s1;
			}
		}
		else
		{
			while (in[i] instanceof String)
			{
				String s2 = (String)in[i++];
				++k;
				j = s2.length();
				s = s + s2;
			}
		}

		HashMap hashmap;

		for (hashmap = new HashMap(); i < in.length; i += 2)
		{
			Character character = (Character)in[i];
			ItemStack itemstack1 = null;

			if (in[i + 1] instanceof Item)
			{
				itemstack1 = new ItemStack((Item)in[i + 1]);
			}
			else if (in[i + 1] instanceof Block)
			{
				itemstack1 = new ItemStack((Block)in[i + 1], 1, 32767);
			}
			else if (in[i + 1] instanceof ItemStack)
			{
				itemstack1 = (ItemStack)in[i + 1];
			}

			hashmap.put(character, itemstack1);
		}

		ItemStack[] aitemstack = new ItemStack[j * k];

		for (int i1 = 0; i1 < j * k; ++i1)
		{
			char c0 = s.charAt(i1);

			if (hashmap.containsKey(Character.valueOf(c0)))
			{
				aitemstack[i1] = ((ItemStack)hashmap.get(Character.valueOf(c0))).copy();
			}
			else
			{
				aitemstack[i1] = null;
			}
		}

		ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, out);
		return shapedrecipes;
	}

	public static ArrayList<ItemStack> getAllItemsInRecipe(IRecipe ire) {
		ArrayList<ItemStack> li = new ArrayList();
		if (ire instanceof ShapedRecipes) {
			ShapedRecipes r = (ShapedRecipes)ire;
			for (int i = 0; i < r.recipeItems.length; i++) {
				li.add(r.recipeItems[i]);
			}
		}
		else if (ire instanceof ShapedOreRecipe) {
			ShapedOreRecipe so = (ShapedOreRecipe)ire;
			Object[] objin = so.getInput();
			for (int i = 0; i < objin.length; i++) {
				if (objin[i] instanceof ItemStack)
					li.add((ItemStack)objin[i]);
				else if (objin[i] instanceof ArrayList) {
					li.addAll((ArrayList)objin[i]);
				}
			}
		}
		else if (ire instanceof ExpandedOreRecipe) {
			ExpandedOreRecipe so = (ExpandedOreRecipe)ire;
			Object[] objin = so.getInputCopy();
			for (int i = 0; i < objin.length; i++) {
				if (objin[i] instanceof ItemStack)
					li.add((ItemStack)objin[i]);
				else if (objin[i] instanceof ArrayList) {
					li.addAll((ArrayList)objin[i]);
				}
			}
		}
		else if (ire instanceof ShapelessRecipes) {
			ShapelessRecipes sr = (ShapelessRecipes)ire;
			li.addAll(sr.recipeItems);
		}
		else if (ire instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe so = (ShapelessOreRecipe)ire;
			for (int i = 0; i < so.getRecipeSize(); i++) {
				Object obj = so.getInput().get(i);
				if (obj instanceof ItemStack)
					li.add((ItemStack)obj);
				else if (obj instanceof ArrayList) {
					li.addAll((ArrayList)obj);
				}
			}
		}
		return li;
	}



	public static Object[] getInputArrayCopy(IRecipe ire) {
		Object[] out = new Object[9];
		if (ire instanceof ShapedRecipes) {
			ShapedRecipes r = (ShapedRecipes)ire;
			for (int i = 0; i < r.recipeItems.length; i++) {
				if (r.recipeItems[i] != null)
					out[i] = r.recipeItems[i].copy();
			}
		}
		else if (ire instanceof ShapedOreRecipe) {
			ShapedOreRecipe so = (ShapedOreRecipe)ire;
			Object[] objin = so.getInput();
			for (int i = 0; i < objin.length; i++) {
				Object o = objin[i];
				if (o instanceof ItemStack)
					out[i] = ((ItemStack)o).copy();
				else if (o instanceof List)
					out[i] = new ArrayList((List)o);
			}
		}
		else if (ire instanceof ExpandedOreRecipe) {
			ExpandedOreRecipe so = (ExpandedOreRecipe)ire;
			Object[] objin = so.getInputCopy();
			for (int i = 0; i < objin.length; i++) {
				Object o = objin[i];
				if (o instanceof ItemStack)
					out[i] = ((ItemStack)o).copy();
				else if (o instanceof List)
					out[i] = new ArrayList((List)o);
			}
		}
		else if (ire instanceof ShapelessRecipes) {
			ShapelessRecipes sr = (ShapelessRecipes)ire;
			for (int i = 0; i < sr.recipeItems.size(); i++) {
				ItemStack is = (ItemStack)sr.recipeItems.get(i);
				out[i] = is.copy();
			}
		}
		else if (ire instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe so = (ShapelessOreRecipe)ire;
			for (int i = 0; i < so.getRecipeSize(); i++) {
				Object o = so.getInput().get(i);
				if (o instanceof ItemStack)
					out[i] = ((ItemStack)o).copy();
				else if (o instanceof List)
					out[i] = new ArrayList((List)o);
			}
		}
		return out;
	}

	/** DISTINCT from getAllItems in that it returns a list of objects, including lists! */
	public static ArrayList<Object> getAllInputsInRecipe(IRecipe ire) {
		ArrayList<Object> li = new ArrayList();
		if (ire instanceof ShapedRecipes) {
			ShapedRecipes r = (ShapedRecipes)ire;
			for (int i = 0; i < r.recipeItems.length; i++) {
				li.add(r.recipeItems[i]);
			}
		}
		else if (ire instanceof ShapedOreRecipe) {
			ShapedOreRecipe so = (ShapedOreRecipe)ire;
			Object[] objin = so.getInput();
			for (int i = 0; i < objin.length; i++) {
				li.add(objin[i]);
			}
		}
		else if (ire instanceof ExpandedOreRecipe) {
			ExpandedOreRecipe so = (ExpandedOreRecipe)ire;
			Object[] objin = so.getInputCopy();
			for (int i = 0; i < objin.length; i++) {
				li.add(objin[i]);
			}
		}
		else if (ire instanceof ShapelessRecipes) {
			ShapelessRecipes sr = (ShapelessRecipes)ire;
			li.addAll(sr.recipeItems);
		}
		else if (ire instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe so = (ShapelessOreRecipe)ire;
			for (int i = 0; i < so.getRecipeSize(); i++) {
				Object obj = so.getInput().get(i);
				li.add(obj);
			}
		}
		return li;
	}

	public static ArrayList<ItemStack> getMutableOreDictList(String s) {
		ArrayList li = OreDictionary.getOres(s);
		ArrayList clean = new ArrayList();
		clean.addAll(li);
		return clean;
	}

	public static IRecipe getShapelessRecipeFor(ItemStack out, ItemStack... in) {
		return new ShapelessRecipes(out.copy(), ReikaJavaLibrary.makeListFrom(in));
	}

	public static boolean matchArrayToRecipe(ItemStack[] in, IRecipe ir) {
		RecipePattern r = new RecipePattern(in);
		return ir.matches(r, null);
	}

	public static boolean recipeContains(IRecipe ir, ItemStack is) {
		return ReikaItemHelper.collectionContainsItemStack(getAllItemsInRecipe(ir), is);
	}

	public static Collection<Integer> getRecipeLocationIndices(IRecipe ir, ItemStack is) {
		Collection<Integer> c = new ArrayList();
		RecipeCache r = getRecipeCacheObject(ir, false);
		for (int i = 0; i < 9; i++) {
			List<ItemStack> li = r.items[i];
			if (li != null && ReikaItemHelper.collectionContainsItemStack(li, is))
				c.add(i);
		}
		return c;
	}

	@SideOnly(Side.CLIENT)
	public static ItemHashMap<Integer> getItemCountsForDisplay(IRecipe ir) {
		ItemHashMap<Integer> map = new ItemHashMap();
		ItemStack[] items = ReikaRecipeHelper.getPermutedRecipeArray(ir);
		for (int i = 0; i < 9; i++) {
			ItemStack is = items[i];
			if (is != null) {
				Integer num = map.get(is);
				int n = num != null ? num.intValue() : 0;
				map.put(is, n+1);
			}
		}
		return map;
	}

	public static String toString(IRecipe r) {
		if (r instanceof ShapedRecipes) {
			return Arrays.toString(((ShapedRecipes)r).recipeItems);
		}
		else if (r instanceof ShapelessRecipes) {
			return ((ShapelessRecipes)r).recipeItems.toString();
		}
		else if (r instanceof ShapedOreRecipe) {
			return Arrays.toString(((ShapedOreRecipe)r).getInput());
		}
		else if (r instanceof ShapelessOreRecipe) {
			return ((ShapelessOreRecipe)r).getInput().toString();
		}
		else {
			return "Unknown_"+r.getClass().getName();
		}
	}
}

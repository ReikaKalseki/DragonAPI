/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
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
import Reika.DragonAPI.Instantiable.ExpandedOreRecipe;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class ReikaRecipeHelper extends DragonAPICore {

	private static final CraftingManager cr = CraftingManager.getInstance();
	private static final List<IRecipe> recipes = cr.getRecipeList();

	private static Field shapedOreHeight;
	private static Field shapedOreWidth;
	private static Field shapedOreInput;

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
	public static List<IRecipe> getAllRecipesByOutput(List<IRecipe> in, ItemStack out) {
		List<IRecipe> li = new ArrayList<IRecipe>();
		for (int i = 0; i < in.size(); i++) {
			IRecipe ir = in.get(i);
			//ReikaJavaLibrary.pConsole(ir.getRecipeOutput()+" == "+out);
			if (ReikaItemHelper.matchStacks(ir.getRecipeOutput(), out))
				li.add(ir);
		}
		//ReikaJavaLibrary.pConsole(li);
		return li;
	}

	/** Finds recipes by product. */
	public static List<ShapedOreRecipe> getShapedOreRecipesByOutput(List<IRecipe> in, ItemStack out) {
		List<ShapedOreRecipe> li = new ArrayList<ShapedOreRecipe>();
		for (int i = 0; i < in.size(); i++) {
			IRecipe ir = in.get(i);
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
		for (int i = 0; i < in.size(); i++) {
			IRecipe ir = in.get(i);
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
		for (int i = 0; i < in.size(); i++) {
			IRecipe ir = in.get(i);
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
		for (int i = 0; i < in.size(); i++) {
			IRecipe ir = in.get(i);
			if (ir instanceof ShapelessOreRecipe) {
				if (ReikaItemHelper.matchStacks(ir.getRecipeOutput(), out))
					li.add((ShapelessOreRecipe)ir);
			}
		}
		//ReikaJavaLibrary.pConsole(li);
		return li;
	}

	/** Turns a recipe into a 3x3 itemstack array for rendering. Args: ItemStack[] array, Recipe */
	public static void copyRecipeToItemStackArray(ItemStack[] in, IRecipe ire) {
		ItemStack[] isin = new ItemStack[9];
		int num;
		int w = 0;
		int h = 0;
		ReikaJavaLibrary.pConsole("Recipe is null!", ire == null);
		ReikaJavaLibrary.pConsole("ItemStack array is null!", in == null);
		if (ire instanceof ShapedRecipes) {
			ShapedRecipes r = (ShapedRecipes)ire;
			num = r.recipeItems.length;
			w = r.recipeWidth;
			h = r.recipeHeight;
			for (int i = 0; i < r.recipeItems.length; i++) {
				isin[i] = r.recipeItems[i];
			}

		}
		else if (ire instanceof ShapedOreRecipe) {
			ShapedOreRecipe so = (ShapedOreRecipe)ire;
			Object[] objin = so.getInput();
			//ReikaJavaLibrary.pConsole(Arrays.toString(objin));
			w = 3;
			h = 3;
			for (int i = 0; i < objin.length; i++) {
				if (objin[i] instanceof ItemStack)
					isin[i] = (ItemStack)objin[i];
				else if (objin[i] instanceof ArrayList) {
					if (!((List<IRecipe>)objin[i]).isEmpty())
						isin[i] = (ItemStack)((ArrayList)objin[i]).get(0);
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
				if (objin[i] instanceof ItemStack)
					isin[i] = (ItemStack)objin[i];
				else if (objin[i] instanceof ArrayList) {
					if (!((List<IRecipe>)objin[i]).isEmpty())
						isin[i] = (ItemStack)((ArrayList)objin[i]).get(0);
				}
			}
		}
		else if (ire instanceof ShapelessRecipes) {
			ShapelessRecipes sr = (ShapelessRecipes)ire;
			//ReikaJavaLibrary.pConsole(ire);
			for (int i = 0; i < sr.getRecipeSize(); i++) {
				in[i] = (ItemStack)sr.recipeItems.get(i);
			}
		}
		else if (ire instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe so = (ShapelessOreRecipe)ire;
			for (int i = 0; i < so.getRecipeSize(); i++) {
				Object obj = so.getInput().get(i);
				if (obj instanceof ItemStack)
					in[i] = (ItemStack)obj;
				else if (obj instanceof ArrayList) {
					in[i] = (ItemStack)((ArrayList)obj).get(0);
				}
				//ReikaJavaLibrary.pConsole(ire);
			}
		}
		if (w == 3 && h == 3) {
			for (int i = 0; i < 9; i++)
				in[i] = isin[i];
		}
		if (w == 1 && h == 1) {
			in[4] = isin[0];
		}
		if (w == 2 && h == 2) {
			in[0] = isin[0];
			in[1] = isin[1];
			in[3] = isin[2];
			in[4] = isin[3];
		}
		if (w == 1 && h == 2) {
			in[4] = isin[0];
			in[7] = isin[1];
		}
		if (w == 2 && h == 1) {
			in[0] = isin[0];
			in[1] = isin[1];
		}
		if (w == 3 && h == 1) {
			in[0] = isin[0];
			in[1] = isin[1];
			in[2] = isin[2];
		}
		if (w == 1 && h == 3) {
			in[1] = isin[0];
			in[4] = isin[1];
			in[7] = isin[2];
		}
		if (w == 2 && h == 3) {
			in[0] = isin[0];
			in[1] = isin[1];
			in[3] = isin[2];
			in[4] = isin[3];
			in[6] = isin[4];
			in[7] = isin[5];
		}
		if (w == 3 && h == 2) {
			in[3] = isin[0];
			in[4] = isin[1];
			in[5] = isin[2];
			in[6] = isin[3];
			in[7] = isin[4];
			in[8] = isin[5];
		}
		for (int i = 0; i < in.length; i++) {
			//ReikaJavaLibrary.pConsole(in[i]+" for "+i);
			if (in[i] != null) {
				if (in[i].stackSize > 1)
					in[i].stackSize = 1;//in[1] = new ItemStack(in[i].itemID, 4, in[i].getItemDamage());
			}
		}
	}

	/** Get the smelting recipe of an item by output. Args: output */
	public static ItemStack getFurnaceInput(ItemStack out) {
		HashMap m = (HashMap)FurnaceRecipes.smelting().getMetaSmeltingList();
		Set ks = m.keySet();
		Object[] ob = ks.toArray();
		for (int i = 0; i < ob.length; i++) {
			//ReikaJavaLibrary.pConsole(ob[i]);
			try {
				int id = (Integer)((List)ob[i]).get(0);
				int meta = (Integer)((List)ob[i]).get(1);
				ItemStack is = new ItemStack(id, 1, meta);
				if (ReikaItemHelper.matchStacks(FurnaceRecipes.smelting().getSmeltingResult(is), out)) {
					return is;
				}
			}
			catch (ClassCastException e) {
				ReikaJavaLibrary.pConsole(e.getMessage());
			}
		}
		return null;
	}

	/** Adds a smelting recipe. Args; Item in, item out, xp */
	public static void addSmelting(ItemStack in, ItemStack out, float xp) {
		FurnaceRecipes.smelting().addSmelting(in.itemID, in.getItemDamage(), out, xp);
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
		ArrayList<IRecipe> copies = new ArrayList();
		List<IRecipe> li = CraftingManager.getInstance().getRecipeList();
		for (int k = 0; k < li.size(); k++) {
			IRecipe ir = li.get(k);
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
						ShapedOreRecipe rec = new ShapedOreRecipe(s.getRecipeOutput(), 'B', Block.stone);
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

		for (int i = 0; i < copies.size(); i++) {
			IRecipe ir = copies.get(i);
			if (ir instanceof ShapedOreRecipe) {
				ShapedOreRecipe rec = (ShapedOreRecipe) ir;
				ReikaJavaLibrary.spamConsole(ir.getRecipeOutput().getDisplayName()+":"+Arrays.toString(rec.getInput()));
			}
		}
		CraftingManager.getInstance().getRecipeList().addAll(copies);
	}
}

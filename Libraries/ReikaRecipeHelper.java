/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
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
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBookCloning;
import net.minecraft.item.crafting.RecipeFireworks;
import net.minecraft.item.crafting.RecipesArmor;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.item.crafting.RecipesCrafting;
import net.minecraft.item.crafting.RecipesDyes;
import net.minecraft.item.crafting.RecipesFood;
import net.minecraft.item.crafting.RecipesIngots;
import net.minecraft.item.crafting.RecipesMapCloning;
import net.minecraft.item.crafting.RecipesMapExtending;
import net.minecraft.item.crafting.RecipesTools;
import net.minecraft.item.crafting.RecipesWeapons;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.ASMCalls;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Recipe.RecipePattern;
import Reika.DragonAPI.Interfaces.CustomToStringRecipe;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import appeng.api.recipes.IIngredient;
import appeng.api.storage.data.IAEItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputOreDict;

public class ReikaRecipeHelper extends DragonAPICore {

	private static final CraftingManager cr = CraftingManager.getInstance();

	private static final Random rand = new Random();

	private static final int[] permuOffsets = new int[9];

	private static final HashMap<IRecipe, RecipeCache> recipeCache = new HashMap();
	private static final HashMap<IRecipe, RecipeCache> recipeCacheClient = new HashMap();

	private static Field shapedOreHeight;
	private static Field shapedOreWidth;
	private static Field shapedOreInput;

	private static Class ic2ShapedClass;
	private static Class ic2ShapelessClass;
	private static Field shapedIc2Input;
	private static Field shapedIc2InputMirror;
	private static Field shapedIc2Height;
	private static Field shapedIc2Width;
	private static Field ic2MasksField;
	private static Field shapelessIc2Input;

	private static Class aeShapedClass;
	private static Class aeShapelessClass;
	private static Field shapedAEInput;
	private static Field shapelessAEInput;
	private static Field shapedAEHeight;
	private static Field shapedAEWidth;

	private static Class computerTurtleClass;
	private static Field computerTurtleInput;

	private static Class teNEIClass;
	private static Field teNEIWrappedRecipe;

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

	private static class UnparsableRecipeCache extends RecipeCache {

		private UnparsableRecipeCache() {
			super(new List[0], 0, 0);
		}

	}


	public static interface ReplacementCallback {

		void onReplaced(IRecipe ir, int slot, Object from, Object to);

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

		if (ModList.IC2.isLoaded()) {
			try {
				ic2ShapedClass = Class.forName("ic2.core.AdvRecipe");
				ic2ShapelessClass = Class.forName("ic2.core.AdvShapelessRecipe");

				shapedIc2Input = ic2ShapedClass.getDeclaredField("input");
				shapedIc2Input.setAccessible(true);

				shapedIc2Width = ic2ShapedClass.getDeclaredField("inputWidth");
				shapedIc2Width.setAccessible(true);

				shapedIc2Height = ic2ShapedClass.getDeclaredField("inputHeight");
				shapedIc2Height.setAccessible(true);

				shapedIc2InputMirror = ic2ShapedClass.getDeclaredField("inputMirrored");
				shapedIc2InputMirror.setAccessible(true);

				ic2MasksField = ic2ShapedClass.getDeclaredField("masks");
				ic2MasksField.setAccessible(true);

				shapelessIc2Input = ic2ShapelessClass.getDeclaredField("input");
				shapelessIc2Input.setAccessible(true);
			}
			catch (Exception e) {
				e.printStackTrace();
				DragonAPICore.logError("Could not load IC2 recipe handling!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.IC2, e);
			}
		}

		if (ModList.THERMALEXPANSION.isLoaded()) {
			try {
				teNEIClass = Class.forName("cofh.thermalexpansion.plugins.nei.handlers.NEIRecipeWrapper");
				teNEIWrappedRecipe = teNEIClass.getDeclaredField("recipe");
				teNEIWrappedRecipe.setAccessible(true);
			}
			catch (Exception e) {
				e.printStackTrace();
				DragonAPICore.logError("Could not load TE recipe handling!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THERMALEXPANSION, e);
			}
		}

		if (ModList.COMPUTERCRAFT.isLoaded()) {
			try {
				computerTurtleClass = Class.forName("dan200.computercraft.shared.turtle.recipes.TurtleRecipe");
				computerTurtleInput = computerTurtleClass.getDeclaredField("m_recipe");
				computerTurtleInput.setAccessible(true);
			}
			catch (Exception e) {
				e.printStackTrace();
				DragonAPICore.logError("Could not load ComputerCraft recipe handling!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.COMPUTERCRAFT, e);
			}
		}

		if (ModList.APPENG.isLoaded()) {
			try {
				aeShapedClass = Class.forName("appeng.recipes.game.ShapedRecipe");
				aeShapelessClass = Class.forName("appeng.recipes.game.ShapelessRecipe");

				shapedAEInput = aeShapedClass.getDeclaredField("input");
				shapedAEInput.setAccessible(true);

				shapedAEWidth = aeShapedClass.getDeclaredField("width");
				shapedAEWidth.setAccessible(true);

				shapedAEHeight = aeShapedClass.getDeclaredField("height");
				shapedAEHeight.setAccessible(true);

				shapelessAEInput = aeShapelessClass.getDeclaredField("input");
				shapelessAEInput.setAccessible(true);
			}
			catch (Exception e) {
				e.printStackTrace();
				DragonAPICore.logError("Could not load AE recipe handling!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.APPENG, e);
			}
		}
	}

	public static Class getIC2ShapedClass() {
		return ic2ShapedClass;
	}

	public static Class getIC2ShapelessClass() {
		return ic2ShapelessClass;
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
	/*
	/** Finds recipes by product. NOT PERFORMANT! *//*
	public static List<IRecipe> getRecipesByOutput(ItemStack out) {
		List<IRecipe> li = new ArrayList<IRecipe>();
		for (int i = 0; i < recipes.size(); i++) {
			IRecipe ir = recipes.get(i);
			if (ItemStack.areItemStacksEqual(ir.getRecipeOutput(), out))
				li.add(ir);
		}
		return li;
	}

	/** Finds recipes by product. NOT PERFORMANT! *//*
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

	/** Finds recipes by product. NOT PERFORMANT! *//*
	public static List<ShapedRecipes> getShapedRecipesByOutput(List<IRecipe> in, ItemStack out) {
		List<ShapedRecipes> li = new ArrayList<ShapedRecipes>();
		for (int i = 0; i < in.size(); i++) {
			IRecipe ir = in.get(i);
			//DragonAPICore.log(ir.getRecipeOutput()+" == "+out);
			if (ir instanceof ShapedRecipes) {
				if (ReikaItemHelper.matchStacks(ir.getRecipeOutput(), out))
					li.add((ShapedRecipes)ir);
			}
		}
		//DragonAPICore.log(li);
		return li;
	}
	 */
	public static boolean isUniformInput(IRecipe ir) {
		HashSet<KeyedItemStack> set = new HashSet();
		for (ItemStack is : getAllItemsInRecipe(ir)) {
			KeyedItemStack ks = new KeyedItemStack(is).setSimpleHash(true);
			set.add(ks);
		}
		return set.size() == 1;
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
			//DragonAPICore.log(ir.getRecipeOutput()+" == "+out);
			if (ReikaItemHelper.matchStacks(ir.getRecipeOutput(), out))
				li.add(ir);
		}
		//DragonAPICore.log(li);
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
			//DragonAPICore.log(ir.getRecipeOutput()+" == "+out);
			if (ir instanceof ShapedOreRecipe) {
				if (ReikaItemHelper.matchStacks(ir.getRecipeOutput(), out))
					li.add((ShapedOreRecipe)ir);
			}
		}
		//DragonAPICore.log(li);
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
		//DragonAPICore.log(li);
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
		//DragonAPICore.log(li);
		return li;
	}

	private static List<ItemStack> getRecipeItemStack(ItemStack is, boolean client) {
		if (is == null)
			return null;
		if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE && client) {
			return ReikaItemHelper.getAllMetadataPermutations(is.getItem());
		}
		else {
			return ReikaJavaLibrary.makeListFrom(is);
		}
	}

	private static List<ItemStack> getRecipeItemStacks(Collection c, boolean client) {
		ArrayList<ItemStack> ret = new ArrayList();
		for (Object o : c) {
			if (o == null)
				continue;
			if (o instanceof ItemStack) {
				ItemStack is = (ItemStack)o;
				if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE && client) {
					ret.addAll(ReikaItemHelper.getAllMetadataPermutations(is.getItem()));
				}
				else {
					ret.add(is);
				}
			}
			if (ModList.IC2.isLoaded()) {
				handleIC2Inputs(o, ret);
			}
			if (ModList.APPENG.isLoaded()) {
				handleAEInputs(o, ret);
			}
		}
		return ret;
	}

	@ModDependent(ModList.IC2)
	private static void handleIC2Inputs(Object o, ArrayList<ItemStack> ret) {
		if (o instanceof IRecipeInput) {
			ret.addAll(((IRecipeInput)o).getInputs());
		}
	}

	@ModDependent(ModList.APPENG)
	private static void handleAEInputs(Object o, ArrayList<ItemStack> ret) {
		if (o instanceof IAEItemStack) {
			ret.add(((IAEItemStack)o).getItemStack());
		}
	}

	private static RecipeCache getRecipeCacheObject(IRecipe ir, boolean client) {
		HashMap<IRecipe, RecipeCache> map = client ? recipeCacheClient : recipeCache;
		RecipeCache cache = map.get(ir);
		if (cache == null) {
			cache = calculateRecipeToItemStackArray(ir, client);
			if (!ReikaObfuscationHelper.isDeObfEnvironment())
				map.put(ir, cache);
		}
		return cache;
	}

	/** Turns a recipe into a 3x3 itemstack array. Args: Recipe */
	public static List<ItemStack>[] getRecipeArray(IRecipe ir) {
		List<ItemStack>[] lists = new List[9];
		RecipeCache c = getRecipeCacheObject(ir, false);
		if (c instanceof UnparsableRecipeCache)
			return null;
		for (int i = 0; i < 9; i++) {
			List li = c.items[i];
			if (li != null && !li.isEmpty())
				lists[i] = Collections.unmodifiableList(li);
		}
		return lists;
	}

	/** Turns a recipe into a 3x3 itemstack array, permuting it as well, usually for rendering. Args: Recipe */
	@SideOnly(Side.CLIENT)
	public static ItemStack[] getPermutedRecipeArray(IRecipe ir) {
		RecipeCache r = getRecipeCacheObject(ir, true);
		if (r instanceof UnparsableRecipeCache)
			return null;
		List<ItemStack>[] isin = r.items;

		long ttick = System.currentTimeMillis();
		if (GuiScreen.isShiftKeyDown())
			ttick *= 4;
		if (GuiScreen.isCtrlKeyDown())
			ttick /= 8;
		int time = 1000;
		for (int i = 0; i < isin.length; i++) {
			if (isin[i] != null && !isin[i].isEmpty()) {
				if (ttick%time == 0) {
					permuOffsets[i] = rand.nextInt(isin[i].size());
				}
			}
		}

		int[] indices = new int[9];
		ItemStack[] add = new ItemStack[9];
		for (int i = 0; i < 9; i++) {
			List<ItemStack> li = isin[i];
			if (li != null && !li.isEmpty()) {
				int tick = (int)(((ttick/time)+permuOffsets[i])%li.size());
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
			DragonAPICore.logError("Recipe is null!");
		if (ire == null) {
			ReikaJavaLibrary.dumpStack();
			return null;
		}

		ire = getTEWrappedRecipe(ire);

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
			//DragonAPICore.log(Arrays.toString(objin));
			w = 3;
			h = 3;
			for (int i = 0; i < objin.length; i++) {
				if (objin[i] instanceof ItemStack) {
					ItemStack is = (ItemStack)objin[i];
					isin[i] = getRecipeItemStack(is, client);
				}
				else if (objin[i] instanceof List) {
					List li = (List)objin[i];
					if (!li.isEmpty()) {
						isin[i] = getRecipeItemStacks(li, client);
					}
				}
			}
		}
		else if (ire instanceof ShapelessRecipes) {
			ShapelessRecipes sr = (ShapelessRecipes)ire;
			//DragonAPICore.log(ire);
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
					List li = (List)obj;
					if (!li.isEmpty()) {
						isin[i] = getRecipeItemStacks(li, client);
					}
				}
				else {
					DragonAPICore.log("Could not parse ingredient type "+obj.getClass()+" with value "+obj.toString());
					isin[i] = Arrays.asList(new ItemStack(Blocks.fire));
				}
				//DragonAPICore.log(ire);
			}
			w = so.getRecipeSize() >= 3 ? 3 : so.getRecipeSize();
			h = (so.getRecipeSize()+2)/3;
		}
		else if (ire.getClass() == ic2ShapedClass) {
			try {
				Object[] in = (Object[])shapedIc2Input.get(ire);
				in = padIC2CrushedArray(in, ire);
				w = Math.min(3, shapedIc2Width.getInt(ire));
				h = Math.min(3, shapedIc2Height.getInt(ire));
				for (int i = 0; i < in.length; i++) {
					Object o = in[i];
					if (o == null)
						continue;
					if (o instanceof ItemStack)
						isin[i] = getRecipeItemStack((ItemStack)o, client);
					else if (o instanceof List)
						isin[i] = getRecipeItemStacks((List)o, client);
					else if (o instanceof IRecipeInput)
						isin[i] = getRecipeItemStacks(((IRecipeInput)o).getInputs(), client);
					else {
						DragonAPICore.log("Could not parse ingredient type "+o.getClass()+" with value "+o.toString());
						isin[i] = Arrays.asList(new ItemStack(Blocks.fire));
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ire.getClass() == ic2ShapelessClass) {
			try {
				Object[] in = (Object[])shapelessIc2Input.get(ire);
				for (int i = 0; i < in.length; i++) {
					Object o = in[i];
					if (o == null)
						continue;
					if (o instanceof ItemStack)
						isin[i] = getRecipeItemStack((ItemStack)o, client);
					else if (o instanceof List)
						isin[i] = getRecipeItemStacks((List)o, client);
					else if (o instanceof IRecipeInput)
						isin[i] = getRecipeItemStacks(((IRecipeInput)o).getInputs(), client);
					else {
						DragonAPICore.log("Could not parse ingredient type "+o.getClass()+" with value "+o.toString());
						isin[i] = Arrays.asList(new ItemStack(Blocks.fire));
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ire.getClass() == aeShapedClass) {
			try {
				Object[] in = (Object[])shapedAEInput.get(ire);
				w = Math.min(3, shapedAEWidth.getInt(ire));
				h = Math.min(3, shapedAEHeight.getInt(ire));
				for (int i = 0; i < in.length; i++) {
					Object o = in[i];
					if (o == null)
						continue;
					if (o instanceof ItemStack)
						isin[i] = getRecipeItemStack((ItemStack)o, client);
					else if (o instanceof List)
						isin[i] = getRecipeItemStacks((List)o, client);
					else if (o instanceof IAEItemStack)
						isin[i] = getRecipeItemStack(((IAEItemStack)o).getItemStack(), client);
					else if (o instanceof IIngredient)
						isin[i] = getRecipeItemStacks(Arrays.asList(((IIngredient)o).getItemStackSet()), client);
					else {
						DragonAPICore.log("Could not parse ingredient type "+o.getClass()+" with value "+o.toString());
						isin[i] = Arrays.asList(new ItemStack(Blocks.fire));
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ire.getClass() == aeShapelessClass) {
			try {
				Object[] in = (Object[])shapelessAEInput.get(ire);
				for (int i = 0; i < in.length; i++) {
					Object o = in[i];
					if (o == null)
						continue;
					if (o instanceof ItemStack)
						isin[i] = getRecipeItemStack((ItemStack)o, client);
					else if (o instanceof List)
						isin[i] = getRecipeItemStacks((List)o, client);
					else if (o instanceof IAEItemStack)
						isin[i] = getRecipeItemStack(((IAEItemStack)o).getItemStack(), client);
					else if (o instanceof IIngredient)
						isin[i] = getRecipeItemStacks(Arrays.asList(((IIngredient)o).getItemStackSet()), client);
					else {
						DragonAPICore.log("Could not parse ingredient type "+o.getClass()+" with value "+o.toString());
						isin[i] = Arrays.asList(new ItemStack(Blocks.fire));
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ire.getClass() == computerTurtleClass) {
			try {
				Item[] in = (Item[])computerTurtleInput.get(ire);
				for (int i = 0; i < 3; i++) {
					for (int k = 0; k < 3; k++) {
						int idx = i*3+k;
						isin[idx] = getRecipeItemStack(new ItemStack(in[idx]), client);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			DragonAPICore.logError("Recipe "+toString(ire)+" could not be parsed!");
			return new UnparsableRecipeCache();
		}

		return new RecipeCache(isin, w, h);
	}

	@ModDependent(ModList.IC2)
	private static Object[] padIC2CrushedArray(Object[] in, IRecipe ire) throws Exception {
		int[] masks = (int[])ic2MasksField.get(ire);
		int mask = masks[0];
		int w = Math.min(3, shapedIc2Width.getInt(ire));
		int h = Math.min(3, shapedIc2Height.getInt(ire));
		boolean[] flags = ReikaArrayHelper.booleanFromBitflags(mask, w*h);
		ArrayUtils.reverse(flags);
		ArrayList<Object> li = ReikaJavaLibrary.makeListFromArray(in);
		for (int i = 0; i < flags.length; i++) {
			if (!flags[i]) {
				li.add(i, null);
			}
		}
		return li.toArray(new Object[li.size()]);
	}

	//@ModDependent(ModList.THERMALEXPANSION)
	public static IRecipe getTEWrappedRecipe(IRecipe ir) {
		if (ModList.THERMALEXPANSION.isLoaded() && ir.getClass() == teNEIClass) {
			try {
				ir = (IRecipe)teNEIWrappedRecipe.get(ir);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ir;
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
			DragonAPICore.log("Recipe for "+out.getDisplayName()+" requires missing Ore Dictionary items "+missing+", and has not been loaded.");
		return allowed;
	}

	public static boolean replaceIngredientInRecipe(ItemStack ingredient, Object replacement, IRecipe ir) {
		return replaceIngredientInRecipe(ingredient, replacement, ir, null);
	}

	public static boolean replaceIngredientInRecipe(ItemStack ingredient, Object replacement, IRecipe ir, ReplacementCallback rc) {
		if (ir == null)
			return false;
		boolean flag = false;
		if (ingredient == null)
			throw new MisuseException("You cannot replace null in recipes!");

		if (replacement instanceof String)
			replacement = OreDictionary.getOres((String)replacement);

		ir = getTEWrappedRecipe(ir);

		if (ir instanceof ShapedRecipes) {
			if (!(replacement instanceof ItemStack)) {
				throw new MisuseException("You cannot put non-single-stack entries into a basic recipe type!");
			}
			if (ReikaItemHelper.matchStacks(ingredient, replacement)) //not replacing self with self
				return false;
			ShapedRecipes s = (ShapedRecipes) ir;
			for (int i = 0; i < s.recipeItems.length; i++) {
				if (ReikaItemHelper.matchStacks(ingredient, s.recipeItems[i])) {
					flag = true;
					if (rc != null)
						rc.onReplaced(ir, i, s.recipeItems[i], replacement);
					s.recipeItems[i] = (ItemStack)replacement;
				}
			}
		}
		else if (ir instanceof ShapelessRecipes) {
			if (!(replacement instanceof ItemStack)) {
				throw new MisuseException("You cannot put non-single-stack entries into a basic recipe type!");
			}
			if (ReikaItemHelper.matchStacks(ingredient, replacement)) //not replacing self with self
				return false;
			ShapelessRecipes s = (ShapelessRecipes) ir;
			List<ItemStack> in = s.recipeItems;
			for (int i = 0; i < in.size(); i++) {
				if (ReikaItemHelper.matchStacks(ingredient, in.get(i))) {
					flag = true;
					if (rc != null)
						rc.onReplaced(ir, i, in.get(i), replacement);
					in.set(i, (ItemStack)replacement);
				}
			}
		}
		else if (ir instanceof ShapedOreRecipe) {
			ShapedOreRecipe s = (ShapedOreRecipe) ir;
			Object[] in = s.getInput();
			for (int i = 0; i < in.length; i++) {
				if (in[i] instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack) in[i])) {
					if (replacement instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, replacement))
						continue;
					flag = true;
					if (rc != null)
						rc.onReplaced(ir, i, in[i], replacement);
					in[i] = replacement;
				}
				else if (in[i] instanceof List && ReikaItemHelper.collectionContainsItemStack((List<ItemStack>)in[i], ingredient)) {
					flag = ((List)in[i]).size() != 1;
					if (rc != null)
						rc.onReplaced(ir, i, in[i], replacement);
					in[i] = replacement;
				}
			}
		}
		else if (ir instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe s = (ShapelessOreRecipe) ir;
			ArrayList in = s.getInput();
			for (int i = 0; i < in.size(); i++) {
				if (in.get(i) instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack) in.get(i))) {
					if (replacement instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, replacement))
						continue;
					flag = true;
					if (rc != null)
						rc.onReplaced(ir, i, in.get(i), replacement);
					in.set(i, replacement);
				}
				else if (in.get(i) instanceof List && ReikaItemHelper.collectionContainsItemStack((List<ItemStack>)in.get(i), ingredient)) {
					flag = ((List)in.get(i)).size() != 1;
					if (rc != null)
						rc.onReplaced(ir, i, in.get(i), replacement);
					in.set(i, replacement);
				}
			}
		}
		else if (ir.getClass() == ic2ShapedClass) {
			try {
				Object[] in = (Object[])shapedIc2Input.get(ir);
				for (int i = 0; i < in.length; i++) {
					if (in[i] instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack) in[i])) {
						flag = true;
						if (rc != null)
							rc.onReplaced(ir, i, in[i], replacement);
						in[i] = replacement;
					}
					else if (in[i] instanceof IRecipeInput && ((IRecipeInput)in[i]).matches(ingredient)) {
						flag = true;
						if (rc != null)
							rc.onReplaced(ir, i, in[i], replacement);
						in[i] = replacement;
					}
					else if (in[i] instanceof Iterable) {
						boolean repl = false;
						for (Object o : (Iterable)in[i]) {
							if (o instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack)o)) {
								repl = true;
								break;
							}
							else if (o instanceof IRecipeInput && ((IRecipeInput)o).matches(ingredient)) {
								repl = true;
								break;
							}
						}
						if (repl) {
							flag = true;
							if (rc != null)
								rc.onReplaced(ir, i, in[i], replacement);
							in[i] = replacement;
						}
					}
				}
				Object[] in2 = (Object[])shapedIc2InputMirror.get(ir);
				if (in2 != null) {
					for (int i = 0; i < in2.length; i++) {
						if (in2[i] instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack) in2[i])) {
							flag = true;
							if (rc != null)
								rc.onReplaced(ir, i, in2[i], replacement);
							in2[i] = replacement;
						}
						else if (in2[i] instanceof IRecipeInput && ((IRecipeInput)in2[i]).matches(ingredient)) {
							flag = true;
							if (rc != null)
								rc.onReplaced(ir, i, in2[i], replacement);
							in2[i] = replacement;
						}
						else if (in2[i] instanceof Iterable) {
							boolean repl = false;
							for (Object o : (Iterable)in2[i]) {
								if (o instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack)o)) {
									repl = true;
									break;
								}
								else if (o instanceof IRecipeInput && ((IRecipeInput)o).matches(ingredient)) {
									repl = true;
									break;
								}
							}
							if (repl) {
								flag = true;
								if (rc != null)
									rc.onReplaced(ir, i, in2[i], replacement);
								in2[i] = replacement;
							}
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ir.getClass() == ic2ShapelessClass) {
			try {
				Object[] in = (Object[])shapelessIc2Input.get(ir);
				for (int i = 0; i < in.length; i++) {
					if (in[i] instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack) in[i])) {
						flag = true;
						if (rc != null)
							rc.onReplaced(ir, i, in[i], replacement);
						in[i] = replacement;
					}
					else if (in[i] instanceof IRecipeInput && ((IRecipeInput)in[i]).matches(ingredient)) {
						flag = true;
						if (rc != null)
							rc.onReplaced(ir, i, in[i], replacement);
						in[i] = replacement;
					}
					else if (in[i] instanceof Iterable) {
						boolean repl = false;
						for (Object o : (Iterable)in[i]) {
							if (o instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack)o)) {
								repl = true;
								break;
							}
							else if (o instanceof IRecipeInput && ((IRecipeInput)o).matches(ingredient)) {
								repl = true;
								break;
							}
						}
						if (repl) {
							flag = true;
							if (rc != null)
								rc.onReplaced(ir, i, in[i], replacement);
							in[i] = replacement;
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ir.getClass() == aeShapedClass) {
			try {
				Object[] in = (Object[])shapedAEInput.get(ir);
				for (int i = 0; i < in.length; i++) {
					if (in[i] instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack) in[i])) {
						if (replacement instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, replacement))
							continue;
						flag = true;
						if (rc != null)
							rc.onReplaced(ir, i, in[i], replacement);
						in[i] = replacement;
					}
					else if (in[i] instanceof List && ReikaItemHelper.collectionContainsItemStack((List<ItemStack>)in[i], ingredient)) {
						flag = ((List)in[i]).size() != 1;
						if (rc != null)
							rc.onReplaced(ir, i, in[i], replacement);
						in[i] = replacement;
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ir.getClass() == aeShapelessClass) {
			try {
				ArrayList in = (ArrayList)shapelessAEInput.get(ir);
				for (int i = 0; i < in.size(); i++) {
					if (in.get(i) instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, (ItemStack) in.get(i))) {
						if (replacement instanceof ItemStack && ReikaItemHelper.matchStacks(ingredient, replacement))
							continue;
						flag = true;
						if (rc != null)
							rc.onReplaced(ir, i, in.get(i), replacement);
						in.set(i, replacement);
					}
					else if (in.get(i) instanceof List && ReikaItemHelper.collectionContainsItemStack((List<ItemStack>)in.get(i), ingredient)) {
						flag = ((List)in.get(i)).size() != 1;
						if (rc != null)
							rc.onReplaced(ir, i, in.get(i), replacement);
						in.set(i, replacement);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ir.getClass() == computerTurtleClass) {
			try {
				Item[] in = (Item[])computerTurtleInput.get(ir);
				for (int i = 0; i < 3; i++) {
					for (int k = 0; k < 3; k++) {
						int idx = i*3+k;
						if (in[idx] == ingredient.getItem()) {
							flag = true;
							if (rc != null)
								rc.onReplaced(ir, i, in[i], replacement);
							in[idx] = ((ItemStack)replacement).getItem();
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
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
					copies.add(new ShapelessRecipes(s.getRecipeOutput(), new ArrayList(in)));
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
						//DragonAPICore.log("----------------------------------------------------");
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

		HashMap hashmap = ASMCalls.parseItemMappings(i, false, in);

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

	public static int getRecipeIngredientCount(IRecipe recipe) {
		if (recipe instanceof ShapedRecipes) {
			ShapedRecipes r = (ShapedRecipes)recipe;
			int ret = 0;
			for (int i = 0; i < r.recipeItems.length; i++) {
				if (r.recipeItems[i] != null)
					ret++;
			}
			return ret;
		}
		else if (recipe instanceof ShapedOreRecipe) {
			ShapedOreRecipe so = (ShapedOreRecipe)recipe;
			Object[] objin = so.getInput();
			int ret = 0;
			for (int i = 0; i < objin.length; i++) {
				if (objin[i] != null)
					ret++;
			}
			return ret;
		}
		else if (recipe instanceof ShapelessRecipes) {
			ShapelessRecipes sr = (ShapelessRecipes)recipe;
			return sr.recipeItems.size();
		}
		else if (recipe instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe so = (ShapelessOreRecipe)recipe;
			return so.getRecipeSize();
		}
		return -1;
	}

	public static Object[] getInputArrayCopy(IRecipe ire) {
		Object[] out = new Object[9];

		ire = getTEWrappedRecipe(ire);

		if (ire instanceof ShapedRecipes) {
			ShapedRecipes r = (ShapedRecipes)ire;
			for (int i = 0; i < Math.min(3, r.recipeWidth); i++) {
				for (int k = 0; k < Math.min(3, r.recipeHeight); k++) {
					int idx = i+k*r.recipeWidth;
					int idx2 = i+k*3;
					if (r.recipeItems[idx] != null)
						out[idx2] = r.recipeItems[idx].copy();
				}
			}
		}
		else if (ire instanceof ShapedOreRecipe) {
			ShapedOreRecipe so = (ShapedOreRecipe)ire;
			Object[] objin = so.getInput();
			int w = Math.min(3, getOreRecipeWidth(so));
			int h = Math.min(3, getOreRecipeHeight(so));
			for (int i = 0; i < w; i++) {
				for (int k = 0; k < h; k++) {
					int idx = i*w+k;
					int idx2 = i*3+k;
					Object o = objin[idx];
					if (o instanceof ItemStack)
						out[idx2] = ((ItemStack)o).copy();
					else if (o instanceof List)
						out[idx2] = new ArrayList((List)o);
				}
			}
			//ReikaJavaLibrary.pConsole(w+" & "+h+"  > "+Arrays.toString(so.getInput())+" & "+Arrays.toString(out));
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
		else if (ire.getClass() == ic2ShapedClass) {
			try {
				Object[] in = (Object[])shapedIc2Input.get(ire);
				in = padIC2CrushedArray(in, ire);
				int w = Math.min(3, shapedIc2Width.getInt(ire));
				int h = Math.min(3, shapedIc2Height.getInt(ire));
				for (int i = 0; i < w; i++) {
					for (int k = 0; k < h; k++) {
						int idx = i*w+k;
						int idx2 = i*3+k;
						Object o = in[idx];
						if (o instanceof ItemStack)
							out[idx2] = ((ItemStack)o).copy();
						else if (o instanceof List)
							out[idx2] = new ArrayList((List)o);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ire.getClass() == ic2ShapelessClass) {
			try {
				Object[] in = (Object[])shapelessIc2Input.get(ire);
				for (int i = 0; i < in.length; i++) {
					Object o = in[i];
					if (o instanceof ItemStack)
						out[i] = ((ItemStack)o).copy();
					else if (o instanceof List)
						out[i] = new ArrayList((List)o);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ire.getClass() == aeShapedClass) {
			try {
				Object[] in = (Object[])shapedAEInput.get(ire);
				int w = Math.min(3, shapedAEWidth.getInt(ire));
				int h = Math.min(3, shapedAEHeight.getInt(ire));
				for (int i = 0; i < w; i++) {
					for (int k = 0; k < h; k++) {
						int idx = i*w+k;
						int idx2 = i*3+k;
						Object o = in[idx];
						if (o instanceof ItemStack)
							out[idx2] = ((ItemStack)o).copy();
						else if (o instanceof List)
							out[idx2] = new ArrayList((List)o);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ire.getClass() == aeShapelessClass) {
			try {
				List<Object> in = (List<Object>)shapelessAEInput.get(ire);
				for (int i = 0; i < in.size(); i++) {
					Object o = in.get(i);
					if (o instanceof ItemStack)
						out[i] = ((ItemStack)o).copy();
					else if (o instanceof List)
						out[i] = new ArrayList((List)o);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ire.getClass() == computerTurtleClass) {
			try {
				Item[] in = (Item[])computerTurtleInput.get(ire);
				for (int i = 0; i < 3; i++) {
					for (int k = 0; k < 3; k++) {
						int idx = i*3+k;
						out[idx] = new ItemStack(in[idx]);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
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
		if (r instanceof UnparsableRecipeCache)
			return c;
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
		if (items == null)
			return map;
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
		if (r == null) {
			return "<NULL>";
		}

		r = getTEWrappedRecipe(r);

		if (r instanceof ShapedRecipes) {
			return "Shaped "+Arrays.toString(((ShapedRecipes)r).recipeItems)+" > "+r.getRecipeOutput();
		}
		else if (r instanceof ShapelessRecipes) {
			return "Shapeless "+((ShapelessRecipes)r).recipeItems.toString()+" > "+r.getRecipeOutput();
		}
		else if (r instanceof ShapedOreRecipe) {
			return "Shaped Ore "+Arrays.toString(((ShapedOreRecipe)r).getInput())+" > "+r.getRecipeOutput();
		}
		else if (r instanceof ShapelessOreRecipe) {
			return "Shapeless Ore "+((ShapelessOreRecipe)r).getInput().toString()+" > "+r.getRecipeOutput();
		}
		else if (r.getClass() == ic2ShapedClass) {
			try {
				Object[] in = (Object[])shapedIc2Input.get(r);
				return "Shaped IC2 "+Arrays.deepToString(in)+" > "+r.getRecipeOutput();
			}
			catch (Exception e) {
				e.printStackTrace();
				return e.toString();
			}
		}
		else if (r.getClass() == ic2ShapelessClass) {
			try {
				Object[] in = (Object[])shapelessIc2Input.get(r);
				return "Shapeless IC2 "+Arrays.deepToString(in)+" > "+r.getRecipeOutput();
			}
			catch (Exception e) {
				e.printStackTrace();
				return e.toString();
			}
		}
		else if (r.getClass() == aeShapedClass) {
			try {
				Object[] in = (Object[])shapedAEInput.get(r);
				return "Shaped AE "+Arrays.deepToString(in)+" > "+r.getRecipeOutput();
			}
			catch (Exception e) {
				e.printStackTrace();
				return e.toString();
			}
		}
		else if (r.getClass() == aeShapelessClass) {
			try {
				List<Object> in = (List<Object>)shapelessAEInput.get(r);
				return "Shapeless AE "+in.toString()+" > "+r.getRecipeOutput();
			}
			catch (Exception e) {
				e.printStackTrace();
				return e.toString();
			}
		}
		else if (r.getClass() == computerTurtleClass) {
			try {
				Item[] in = (Item[])computerTurtleInput.get(r);
				return "CC Turtle "+Arrays.deepToString(in)+" > "+r.getRecipeOutput();
			}
			catch (Exception e) {
				e.printStackTrace();
				return e.toString();
			}
		}
		else if (r instanceof CustomToStringRecipe) {
			return ((CustomToStringRecipe)r).toDisplayString();
		}
		else {
			return "Unknown '"+r.getClass().getName()+"'"+" > "+r.getRecipeOutput();
		}
	}

	/** Rather slower than toString, so only use this where necessary. */
	public static String toDeterministicString(IRecipe r) {
		if (r instanceof ShapedRecipes) {
			ItemStack[] arr = Arrays.copyOf(((ShapedRecipes)r).recipeItems, ((ShapedRecipes)r).recipeItems.length);
			//Arrays.sort(arr, ReikaItemHelper.comparator); DO NOT CHANGE RECIPE ORDER
			return "Shaped "+Arrays.toString(arr)+" > "+r.getRecipeOutput();
		}
		else if (r instanceof ShapelessRecipes) {
			ArrayList<ItemStack> li = new ArrayList(((ShapelessRecipes)r).recipeItems);
			Collections.sort(li, ReikaItemHelper.comparator);
			return "Shapeless "+li.toString()+" > "+r.getRecipeOutput();
		}
		else if (r instanceof ShapedOreRecipe) {
			Object[] arr = Arrays.copyOf(((ShapedOreRecipe)r).getInput(), ((ShapedOreRecipe)r).getInput().length);
			//Arrays.sort(arr, ReikaItemHelper.itemListComparator);
			for (int i = 0; i < arr.length; i++) {
				Object o = arr[i];
				if (o instanceof List) {
					o = new ArrayList((List)o);
					Collections.sort((List)o, ReikaItemHelper.comparator);
					arr[i] = o;
				}
			}
			return "Shaped Ore "+Arrays.toString(arr)+" > "+r.getRecipeOutput();
		}
		else if (r instanceof ShapelessOreRecipe) {
			ArrayList<Object> li = new ArrayList(((ShapelessOreRecipe)r).getInput());
			Collections.sort(li, ReikaItemHelper.itemListComparator);
			for (int i = 0; i < li.size(); i++) {
				Object o = li.get(i);
				if (o instanceof List) {
					o = new ArrayList((List)o);
					Collections.sort((List)o, ReikaItemHelper.comparator);
					li.set(i, o);
				}
			}
			return "Shapeless Ore "+li.toString()+" > "+r.getRecipeOutput();
		}
		else if (r instanceof CustomToStringRecipe) {
			return ((CustomToStringRecipe)r).toDeterministicString();
		}
		else {
			return "Unknown '"+r.getClass().getName()+"'"+" > "+r.getRecipeOutput();
		}
	}

	public static boolean isNonVForgeRecipeClass(IRecipe r) {
		Class c = r.getClass();
		if (c == ShapedRecipes.class || c == ShapelessRecipes.class)
			return false;
		if (c == RecipeBookCloning.class || c == RecipeFireworks.class || c == RecipesArmor.class || c == RecipesArmorDyes.class)
			return false;
		if (c == RecipesCrafting.class || c == RecipesDyes.class || c == RecipesFood.class || c == RecipesIngots.class)
			return false;
		if (c == RecipesMapCloning.class || c == RecipesMapExtending.class || c == RecipesTools.class || c == RecipesWeapons.class)
			return false;
		if (c == ShapedOreRecipe.class || c == ShapelessOreRecipe.class)
			return false;
		return true;
	}

	public static boolean verifyRecipe(IRecipe r) {
		if (!ReikaItemHelper.verifyItemStack(r.getRecipeOutput(), true))
			return false;
		if (r instanceof ShapedRecipes) {
			ItemStack[] in = ((ShapedRecipes)r).recipeItems;
			for (int i = 0; i < in.length; i++) {
				ItemStack is = in[i];
				if (!ReikaItemHelper.verifyItemStack(is, false)) {
					return false;
				}
			}
		}
		if (r instanceof ShapelessRecipes) {
			List<ItemStack> in = ((ShapelessRecipes)r).recipeItems;
			for (ItemStack is : in) {
				if (!ReikaItemHelper.verifyItemStack(is, false)) {
					return false;
				}
			}
		}
		if (r instanceof ShapedOreRecipe) {
			Object[] in = ((ShapedOreRecipe)r).getInput();
			for (int i = 0; i < in.length; i++) {
				Object o = in[i];
				if (o instanceof ItemStack) {
					if (!ReikaItemHelper.verifyItemStack((ItemStack)o, false)) {
						return false;
					}
				}
				else if (o instanceof List) {
					for (ItemStack is : ((List<ItemStack>)o)) {
						if (!ReikaItemHelper.verifyItemStack(is, false)) {
							return false;
						}
					}
				}
			}
		}
		if (r instanceof ShapelessOreRecipe) {
			List in = ((ShapelessOreRecipe)r).getInput();
			for (Object o : in) {
				if (o instanceof ItemStack) {
					if (!ReikaItemHelper.verifyItemStack((ItemStack)o, false)) {
						return false;
					}
				}
				else if (o instanceof List) {
					for (ItemStack is : ((List<ItemStack>)o)) {
						if (!ReikaItemHelper.verifyItemStack(is, false)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public static Object[] decode1DArray(Object[] array, int w, int h) {
		if (array.length != w*h)
			throw new IllegalArgumentException("Recipe size does not match array length!");
		ArrayList li = new ArrayList();
		char[][] input = new char[h][w];
		for (int i = 0; i < w; i++) {
			for (int k = 0; k < h; k++) {
				int idx = i+k*w;
				Object at = parseIngredient(array[idx]);
				char c = at == null ? ' ' : (char)('a'+idx);
				input[k][i] = c;
				if (at != null) {
					li.add(c);
					li.add(at);
				}
			}
		}
		ArrayList<String> shape = new ArrayList();
		for (char[] line : input) {
			StringBuilder sb = new StringBuilder();
			for (char c : line) {
				sb.append(c);
			}
			shape.add(sb.toString());
		}
		li.addAll(0, shape);
		return li.toArray(new Object[li.size()]);
	}

	public static Object parseIngredient(Object o) {
		if (o instanceof Collection) {
			Collection<ItemStack> c = (Collection)o;
			if (c.isEmpty())
				throw new IllegalArgumentException("Recipe had an empty collection ingredient?!");
			return getOreNameForCollection(c);
		}
		if (ModList.IC2.isLoaded()) {
			o = parseIc2Ingredient(o);
		}
		if (ModList.APPENG.isLoaded()) {
			o = parseAEIngredient(o);
		}
		return o;
	}

	private static String getOreNameForCollection(Collection<ItemStack> c) {
		ItemStack is = c.iterator().next();
		HashSet<String> set = ReikaItemHelper.getOreNames(is);
		for (ItemStack is2 : c) {
			set.retainAll(ReikaItemHelper.getOreNames(is2));
		}
		if (set.isEmpty())
			throw new IllegalArgumentException("Recipe had a collection ingredient, with no shared ore tags?!");
		return set.iterator().next();
	}

	@ModDependent(ModList.IC2)
	private static Object parseIc2Ingredient(Object o) {
		if (o instanceof IRecipeInput) {
			if (o instanceof RecipeInputOreDict) {
				return ((RecipeInputOreDict)o).input;
			}
			List<ItemStack> li = ((IRecipeInput)o).getInputs();
			return li.size() == 1 ? li.get(0) : getOreNameForCollection(li);
		}
		return o;
	}

	@ModDependent(ModList.APPENG)
	private static Object parseAEIngredient(Object o) {
		if (o instanceof IAEItemStack) {
			return ((IAEItemStack)o).getItemStack();
		}
		if (o instanceof IIngredient) {
			IIngredient ii = (IIngredient)o;
			ItemStack[] is;
			try {
				is = ii.getItemStackSet();
			}
			catch (Exception e) {
				e.printStackTrace();
				return o;
			}
			if (is.length == 1) {
				return is[0];
			}
			else {
				List<ItemStack> li = Arrays.asList(is);
				return getOreNameForCollection(li);
			}
		}
		return o;
	}

	public static Object[] decode2DArray(Object[][] array) {
		String[] input = new String[array.length];
		ArrayList objects = new ArrayList();
		ArrayList entries = new ArrayList();
		for (int i = 0; i < array.length; i++) {
			StringBuilder sb = new StringBuilder();
			for (int k = 0; k < array[i].length; k++) {
				Object o = array[i][k];
				char c = o == null ? ' ' : (char)('a'+(i*3+k));
				sb.append(String.valueOf(c));
				if (o != null) {
					entries.add(c);
					entries.add(o);
				}
			}
			input[i] = sb.toString();
		}
		for (int i = 0; i < input.length; i++)
			objects.add(input[i]);
		objects.addAll(entries);
		return objects.toArray(new Object[objects.size()]);
	}

	public static ItemStack getShapelessCraftResult(ItemStack... in) {
		if (in.length > 9)
			throw new MisuseException("Too many input items!");
		RecipePattern ic = new RecipePattern(in);
		return CraftingManager.getInstance().findMatchingRecipe(ic, ReikaWorldHelper.getBasicReferenceWorld());
	}

	public static IRecipe convertRecipeToOre(IRecipe ire) {
		ire = getTEWrappedRecipe(ire);
		if (ire instanceof ShapedRecipes) {
			ShapedRecipes r = (ShapedRecipes)ire;
			return new ShapedOreRecipe(ire.getRecipeOutput(), decode1DArray(r.recipeItems, r.recipeWidth, r.recipeHeight));
		}
		else if (ire instanceof ShapelessRecipes) {
			ShapelessRecipes sr = (ShapelessRecipes)ire;
			List<Object> in = sr.recipeItems;
			Object[] ingredients = new Object[in.size()];
			for (int i = 0; i < in.size(); i++) {
				ingredients[i] = parseIngredient(in.get(i));
			}
			return new ShapelessOreRecipe(ire.getRecipeOutput(), ingredients);
		}
		return ire;
	}

	@Deprecated
	public static IRecipe copyRecipe(IRecipe ire) {
		try {
			ire = getTEWrappedRecipe(ire);
			if (ire instanceof ShapedRecipes) {
				ShapedRecipes r = (ShapedRecipes)ire;
				return getShapedRecipeFor(ire.getRecipeOutput(), decode1DArray(r.recipeItems, r.recipeWidth, r.recipeHeight));
			}
			else if (ire instanceof ShapedOreRecipe) {
				ShapedOreRecipe so = (ShapedOreRecipe)ire;
				return new ShapedOreRecipe(ire.getRecipeOutput(), decode1DArray(so.getInput(), getOreRecipeWidth(so), getOreRecipeHeight(so)));
			}
			else if (ire instanceof ShapelessRecipes) {
				ShapelessRecipes sr = (ShapelessRecipes)ire;
				return new ShapelessRecipes(ire.getRecipeOutput(), new ArrayList(sr.recipeItems));
			}
			else if (ire instanceof ShapelessOreRecipe) {
				ShapelessOreRecipe sr = (ShapelessOreRecipe)ire;
				ArrayList<Object> in = sr.getInput();
				Object[] ingredients = new Object[in.size()];
				for (int i = 0; i < in.size(); i++) {
					ingredients[i] = parseIngredient(in.get(i));
				}
				return new ShapelessOreRecipe(ire.getRecipeOutput(), ingredients);
			}
			else if (ire.getClass() == ic2ShapedClass) {
				try {
					Object[] in = (Object[])shapedIc2Input.get(ire);
					in = padIC2CrushedArray(in, ire);
					int w = shapedIc2Width.getInt(ire);
					int h = shapedIc2Height.getInt(ire);
					if (w*h != in.length) {
						DragonAPICore.logError("Error parsing IC2 recipe: input array does not match reported height and width values!");
					}
					return new ShapedOreRecipe(ire.getRecipeOutput(), decode1DArray(in, w, h));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (ire.getClass() == ic2ShapelessClass) {
				try {
					Object[] in = (Object[])shapelessIc2Input.get(ire);
					Object[] ingredients = new Object[in.length];
					for (int i = 0; i < in.length; i++) {
						ingredients[i] = parseIngredient(in[i]);
					}
					return new ShapelessOreRecipe(ire.getRecipeOutput(), ingredients);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (ire.getClass() == aeShapedClass) {
				try {
					Object[] in = (Object[])shapedAEInput.get(ire);
					int w = shapedAEWidth.getInt(ire);
					int h = shapedAEHeight.getInt(ire);
					return new ShapedOreRecipe(ire.getRecipeOutput(), decode1DArray(in, w, h));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (ire.getClass() == aeShapelessClass) {
				try {
					ArrayList<Object> in = (ArrayList<Object>)shapelessAEInput.get(ire);
					Object[] ingredients = new Object[in.size()];
					for (int i = 0; i < in.size(); i++) {
						ingredients[i] = parseIngredient(in.get(i));
					}
					return new ShapelessOreRecipe(ire.getRecipeOutput(), ingredients);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (ire.getClass() == computerTurtleClass) {
				try {
					Item[] in = (Item[])computerTurtleInput.get(ire);
					return getShapedRecipeFor(ire.getRecipeOutput(), decode1DArray(in, 3, 3));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not copy recipe "+toString(ire));
			e.printStackTrace();
		}
		return null;
	}

	public static boolean matchRecipes(IRecipe r1, IRecipe r2) {
		if (r1 == null && r2 == null)
			return true;
		if (r1 == null || r2 == null)
			return false;
		if (r1.getClass() != r2.getClass())
			return false;
		r1 = getTEWrappedRecipe(r1);
		r2 = getTEWrappedRecipe(r2);
		if (!ItemStack.areItemStacksEqual(r1.getRecipeOutput(), r2.getRecipeOutput()))
			return false;
		if (r1 instanceof ShapedRecipes) {
			ShapedRecipes sr1 = (ShapedRecipes)r1;
			ShapedRecipes sr2 = (ShapedRecipes)r2;
			return ReikaItemHelper.matchStackCollections(Arrays.asList(sr1.recipeItems), Arrays.asList(sr2.recipeItems));
		}
		else if (r1 instanceof ShapedOreRecipe) {
			ShapedOreRecipe so1 = (ShapedOreRecipe)r1;
			ShapedOreRecipe so2 = (ShapedOreRecipe)r2;
			return matchIngredientCollections(Arrays.asList(so1.getInput()), Arrays.asList(so2.getInput()));
		}
		else if (r1 instanceof ShapelessRecipes) {
			ShapelessRecipes sr1 = (ShapelessRecipes)r1;
			ShapelessRecipes sr2 = (ShapelessRecipes)r2;
			return ReikaItemHelper.matchStackCollections(sr1.recipeItems, sr2.recipeItems);
		}
		else if (r1 instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe sr1 = (ShapelessOreRecipe)r1;
			ShapelessOreRecipe sr2 = (ShapelessOreRecipe)r2;
			return matchIngredientCollections(sr1.getInput(), sr2.getInput());
		}
		else if (r1.getClass() == ic2ShapedClass) {
			try {
				Object[] in1 = (Object[])shapedIc2Input.get(r1);
				Object[] in2 = (Object[])shapedIc2Input.get(r2);
				//in1 = padIC2CrushedArray(in1, r1);
				//in2 = padIC2CrushedArray(in2, r2);
				return matchIngredientCollections(Arrays.asList(in1), Arrays.asList(in2));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (r1.getClass() == ic2ShapelessClass) {
			try {
				Object[] in1 = (Object[])shapelessIc2Input.get(r1);
				Object[] in2 = (Object[])shapelessIc2Input.get(r2);
				return matchIngredientCollections(Arrays.asList(in1), Arrays.asList(in2));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (r1.getClass() == aeShapedClass) {
			try {
				Object[] in1 = (Object[])shapedAEInput.get(r1);
				Object[] in2 = (Object[])shapedAEInput.get(r2);
				return matchIngredientCollections(Arrays.asList(in1), Arrays.asList(in2));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (r1.getClass() == aeShapelessClass) {
			try {
				List<Object> in1 = (List<Object>)shapelessAEInput.get(r1);
				List<Object> in2 = (List<Object>)shapelessAEInput.get(r2);
				return matchIngredientCollections(in1, in2);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (r1.getClass() == computerTurtleClass) {
			try {
				Item[] in1 = (Item[])computerTurtleInput.get(r1);
				Item[] in2 = (Item[])computerTurtleInput.get(r2);
				if (in1.length != in2.length)
					return false;
				for (int i = 0; i < in1.length; i++) {
					if (in1[i] != in2[i])
						return false;
				}
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private static boolean matchIngredientCollections(List<Object> input, List<Object> input2) {
		if (input.size() != input2.size())
			return false;
		for (int i = 0; i < input.size(); i++) {
			Object o1 = input.get(i);
			Object o2 = input2.get(i);
			if (o1 == null && o2 == null)
				continue;
			if (o1 == null || o2 == null)
				return false;
			if (o1.getClass() != o2.getClass())
				return false;
			if (o1 instanceof ItemStack) {
				if (!ReikaItemHelper.matchStacks((ItemStack)o1, (ItemStack)o2))
					return false;
			}
			else { //if (o1 instanceof Collection || o1 instanceof String)
				if (!o1.equals(o2))
					return false;
			}
		}
		return true;
	}

	public static double getRecipeSimilarityValue(IRecipe r1, IRecipe r2) {
		if (r1.getClass() != r2.getClass())
			return 0;
		double score = 0;
		if (r1 instanceof ShapedRecipes) {
			ShapedRecipes sr1 = (ShapedRecipes)r1;
			ShapedRecipes sr2 = (ShapedRecipes)r2;
			if (sr1.recipeHeight != sr2.recipeHeight || sr1.recipeWidth != sr2.recipeWidth)
				return 0;
			for (int i = 0; i < sr1.recipeWidth; i++) {
				for (int k = 0; k < sr1.recipeHeight; k++) {
					ItemStack is1 = sr1.recipeItems[i+k*sr1.recipeWidth];
					ItemStack is2 = sr2.recipeItems[i+k*sr2.recipeWidth];
					if (ReikaItemHelper.matchStacks(is1, is2)) {
						score += 5;
					}
				}
			}
			return score/(sr1.recipeHeight*sr1.recipeWidth);
		}
		else if (r1 instanceof ShapedOreRecipe) {
			ShapedOreRecipe so1 = (ShapedOreRecipe)r1;
			ShapedOreRecipe so2 = (ShapedOreRecipe)r2;
			int h1 = getOreRecipeHeight(so1);
			int w1 = getOreRecipeWidth(so1);
			if (h1 != getOreRecipeHeight(so2) || w1 != getOreRecipeWidth(so2))
				return 0;
			for (int i = 0; i < w1; i++) {
				for (int k = 0; k < h1; k++) {
					Object is1 = so1.getInput()[i+k*w1];
					Object is2 = so2.getInput()[i+k*w1];
					if (is1 == is2)
						score += 5;
					if (is1 == null || is2 == null)
						continue;
					if (is1.getClass() != is2.getClass())
						continue;
					if (is1 instanceof ItemStack) {
						if (ReikaItemHelper.matchStacks((ItemStack)is1, (ItemStack)is2)) {
							score += 5;
						}
					}
					else if (is1 instanceof Collection) {
						Collection<ItemStack> c1 = (Collection<ItemStack>)is1;
						Collection<ItemStack> c2 = (Collection<ItemStack>)is2;
						HashSet<KeyedItemStack> s1 = new HashSet();
						HashSet<KeyedItemStack> s2 = new HashSet();
						for (ItemStack is : c1) {
							s1.add(new KeyedItemStack(is).setIgnoreNBT(true).setSized(false).setIgnoreMetadata(false).setSimpleHash(true));
						}
						for (ItemStack is : c2) {
							s2.add(new KeyedItemStack(is).setIgnoreNBT(true).setSized(false).setIgnoreMetadata(false).setSimpleHash(true));
						}
						if (s1.equals(s2))
							score += 5;
					}
				}
			}
			return score/(h1*w1);
		}
		else if (r1 instanceof ShapelessRecipes) {
			ShapelessRecipes sr1 = (ShapelessRecipes)r1;
			ShapelessRecipes sr2 = (ShapelessRecipes)r2;
			ArrayList<ItemStack> c1 = new ArrayList(sr1.recipeItems);
			ArrayList<ItemStack> c2 = new ArrayList(sr2.recipeItems);
			ArrayList<ItemStack> lg = c1.size() > c2.size() ? c1 : c2;
			ArrayList<ItemStack> sm = c1.size() > c2.size() ? c2 : c1;
			for (ItemStack is : lg) {
				int idx = ReikaItemHelper.getIndexOf(sm, is);
				if (idx >= 0) {
					score += 5;
					sm.remove(idx);
				}
			}
			return score/lg.size();
		}
		else if (r1 instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe sr1 = (ShapelessOreRecipe)r1;
			ShapelessOreRecipe sr2 = (ShapelessOreRecipe)r2;
			ArrayList<Object> c1 = new ArrayList(sr1.getInput());
			ArrayList<Object> c2 = new ArrayList(sr2.getInput());
			ArrayList<Object> lg = c1.size() > c2.size() ? c1 : c2;
			ArrayList<Object> sm = c1.size() > c2.size() ? c2 : c1;
			for (Object o : lg) {
				int idx = -1;
				if (o instanceof ItemStack)
					idx = ReikaItemHelper.getIndexOf(sm, (ItemStack)o);
				else
					idx = sm.indexOf(o);
				if (idx >= 0) {
					score += 5;
					sm.remove(idx);
				}
			}
			return score/lg.size();
		}
		return 0;
	}

	public static Object[] parseMinetweakerInput(String rec) { //[[0, 1, 2], [3, 4, 5], [6, 7, 8]]
		rec = rec.replace(" ", "").replace("<", "").replace(">", "");
		String[] parts = rec.split("[\\\\[,\\\\]]");
		ArrayList<String> li = new ArrayList();
		for (String s : parts) {
			s = s.replace("[", "").replace("]", "");
			if (!s.isEmpty())
				li.add(s);
		}
		parts = li.toArray(new String[li.size()]);
		Object[] ret = new Object[parts.length];
		for (int i = 0; i < parts.length; i++) {
			String s = parts[i];
			if (s.equals("null")) {
				continue;
			}
			else if (s.startsWith("ore:")) {
				ret[i] = s.substring(4);
			}
			else {
				ret[i] = ReikaItemHelper.lookupItem(s);
			}
		}
		return ret;
	}
}

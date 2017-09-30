/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.ConcurrencyDeterminator;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.ListFactory;
import Reika.DragonAPI.Instantiable.Event.AddRecipeEvent;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class ReplacementCraftingHandler {

	private static final MultiMap<Integer, IRecipe> mainData = new MultiMap(new ListFactory(), new ConcurrencyDeterminator());
	private static final ArrayList<IRecipe> nonCategorizableRecipes = new ArrayList();

	private static BackWritableList<IRecipe> cachedRecipeList;
	private static final ArrayList<ListChange> directListChanges = new ArrayList();

	public static void addRecipe(IRecipe ir) {
		if (ir.getClass() == ShapedRecipes.class) {
			mainData.addValue(calculateRecipeKey((ShapedRecipes)ir), ir);
		}
		else if (ir.getClass() == ShapelessRecipes.class) {
			mainData.addValue(calculateRecipeKey((ShapelessRecipes)ir), ir);
		}
		else if (ir.getClass() == ShapedOreRecipe.class) {
			mainData.addValue(calculateRecipeKey((ShapedOreRecipe)ir), ir);
		}
		else if (ir.getClass() == ShapelessOreRecipe.class) {
			mainData.addValue(calculateRecipeKey((ShapelessOreRecipe)ir), ir);
		}
		else {
			nonCategorizableRecipes.add(ir);
		}
		cachedRecipeList = null;
	}

	public static void removeRecipe(IRecipe obj) {
		if (!nonCategorizableRecipes.remove(obj)) {
			mainData.remove(calculateRecipeKey(obj), obj);
		}
		cachedRecipeList = null;
	}

	private static int calculateRecipeKey(IRecipe ir) {
		if (ir.getClass() == ShapedRecipes.class) {
			return calculateRecipeKey((ShapedRecipes)ir);
		}
		else if (ir.getClass() == ShapelessRecipes.class) {
			return calculateRecipeKey((ShapelessRecipes)ir);
		}
		else if (ir.getClass() == ShapedOreRecipe.class) {
			return calculateRecipeKey((ShapedOreRecipe)ir);
		}
		else if (ir.getClass() == ShapelessOreRecipe.class) {
			return calculateRecipeKey((ShapelessOreRecipe)ir);
		}
		return -1;
	}

	private static int calculateRecipeKey(ShapedRecipes ir) {
		return calculateRecipeKey(ir.recipeItems);
	}

	private static int calculateRecipeKey(Object[] input) {
		int c = 0;
		for (int i = 0; i < input.length; i++) {
			if (input[i] != null)
				c++;
		}
		return c;
	}

	private static int calculateRecipeKey(ShapelessRecipes ir) {
		return ir.getRecipeSize();
	}

	private static int calculateRecipeKey(ShapedOreRecipe ir) {
		return calculateRecipeKey(ir.getInput());
	}

	private static int calculateRecipeKey(ShapelessOreRecipe ir) {
		return ir.getRecipeSize();
	}

	private static int calculateRecipeKey(InventoryCrafting ic) {
		return ReikaInventoryHelper.convertCraftToItemList(ic).size();
	}

	public static ItemStack getRecipe(InventoryCrafting ic, World world) {
		handleDirectMapChanges();
		Collection<IRecipe> c = mainData.get(calculateRecipeKey(ic));
		for (IRecipe ir : c) {
			if (ir.matches(ic, world)) {
				return ir.getCraftingResult(ic);
			}
		}
		for (IRecipe ir : nonCategorizableRecipes) {
			if (ir.matches(ic, world)) {
				return ir.getCraftingResult(ic);
			}
		}
		return null;
	}

	public static List<IRecipe> getRecipeList() { //Pass recipe objects directly by reference to allow easy reflection of edits
		//ReikaJavaLibrary.pConsole("DRAGONAPI: Providing requested recipe list...");
		//long time = System.currentTimeMillis();
		if (cachedRecipeList != null) {
			return cachedRecipeList;
		}
		BackWritableList<IRecipe> li = new BackWritableList();
		li.addAll(mainData.allValues(false));
		li.addAll(nonCategorizableRecipes);
		li.tracking = true;
		cachedRecipeList = li;
		//ReikaJavaLibrary.pConsole("DRAGONAPI: Recipe list completed in "+(System.currentTimeMillis()-time)+" ms.");
		return li;
	}

	private static void handleDirectMapChanges() {
		if (!directListChanges.isEmpty()) {
			for (ListChange chg : directListChanges) {
				switch(chg.operation) {
					case ADD:
						addRecipe((IRecipe)chg.recipe);
						break;
					case REMOVE:
						removeRecipe((IRecipe)chg.recipe);
						break;
				}
			}
		}
	}

	public static void fireEventsForVanillaRecipes() {
		AddRecipeEvent.isVanillaPass = true;
		for (IRecipe r : getRecipeList()) {
			removeRecipe(r);
			if (ReikaRecipeHelper.verifyRecipe(r)) {
				if (ReikaRecipeHelper.isNonVForgeRecipeClass(r)) {
					DragonAPICore.log("Found a modded recipe registered in pre-init! This is a design error, as it can trigger a Forge bug and break the recipe! Recipe="+ReikaRecipeHelper.toString(r));
				}
				AddRecipeEvent evt = new AddRecipeEvent(r);
				MinecraftForge.EVENT_BUS.post(evt);
				addRecipe(r);
			}
			else {
				DragonAPICore.logError("Found an invalid recipe in the list, with either nulled inputs or outputs! This is invalid! Class="+r.getClass());
			}
		}
		AddRecipeEvent.isVanillaPass = false;
	}

	public static void sortRecipes() {
		DragonAPICore.log("Sorting crafting recipes...");
		long time = System.currentTimeMillis();
		mainData.sort(RecipeSorter.INSTANCE);
		Collections.sort(nonCategorizableRecipes, RecipeSorter.INSTANCE);
		DragonAPICore.log("Recipe sort completed in "+(System.currentTimeMillis()-time)+" ms.");
	}

	public static class RecipeList extends ArrayList {

		@Override
		public boolean add(Object o) {
			AddRecipeEvent evt = new AddRecipeEvent((IRecipe)o);
			if (!MinecraftForge.EVENT_BUS.post(evt)) {
				ReplacementCraftingHandler.addRecipe(evt.recipe);
				return true;
			}
			return false;
		}

	}

	private static class BackWritableList<E> extends ArrayList<E> {

		private boolean tracking = false;

		@Override
		public boolean add(E obj) {
			if (tracking) {
				directListChanges.add(new ListChange(Operations.ADD, obj));
				this.log("Logged a direct crafting recipe list operation, adding "+ReikaRecipeHelper.toString((IRecipe)obj));
			}
			return super.add(obj);
		}

		@Override
		public void add(int pos, E obj) {
			this.add(obj); //returned recipe list ordering is irrelevant
		}

		@Override
		public E set(int idx, E obj) { //again, no order caring
			E ret = this.remove(idx);
			this.add(obj);
			return ret;
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			if (tracking) {
				for (E obj : c) {
					directListChanges.add(new ListChange(Operations.ADD, obj));
					this.log("Logged a direct crafting recipe list operation, adding "+ReikaRecipeHelper.toString((IRecipe)obj));
				}
			}
			return super.addAll(c);
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException(".....Why?!");
		}

		@Override
		public boolean remove(Object obj) {
			if (tracking) {
				directListChanges.add(new ListChange(Operations.REMOVE, obj));
				this.log("Logged a direct crafting recipe list operation, removing "+ReikaRecipeHelper.toString((IRecipe)obj));
			}
			return super.remove(obj);
		}

		@Override
		public E remove(int idx) {
			if (tracking) {
				Object obj = this.get(idx);
				directListChanges.add(new ListChange(Operations.REMOVE, obj));
				this.log("Logged a direct crafting recipe list operation, removing "+ReikaRecipeHelper.toString((IRecipe)obj));
			}
			return super.remove(idx);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			if (tracking) {
				for (Object obj : c) {
					directListChanges.add(new ListChange(Operations.REMOVE, obj));
					this.log("Logged a direct crafting recipe list operation, removing "+ReikaRecipeHelper.toString((IRecipe)obj));
				}
			}
			return super.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException(".....Why?!");
		}

		private void log(String s) {
			//if (DragonAPIInit.instance != null && DragonAPIInit.instance.getModLogger() != null) { //called too early to be safe
			//	DragonAPICore.log(s);
			//}
			//else {
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+s);
			//}
		}

	}

	private static class ListChange<E> {

		private final Operations operation;
		private final E recipe;

		private ListChange(Operations o, E obj) {
			operation = o;
			recipe = obj;
		}

	}

	private static enum Operations {
		ADD(),
		REMOVE();
	}
}

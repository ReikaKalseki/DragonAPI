/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.RecipeHandlers;

import java.util.Collection;
import java.util.Map;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Instantiable.Data.Collections.ChancedOutputList;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.RecipeManagers;

public class ForestryRecipeHelper extends ModHandlerBase {

	private static final ForestryRecipeHelper instance = new ForestryRecipeHelper();

	public static final ForestryRecipeHelper getInstance() {
		return instance;
	}

	private final ItemHashMap<ChancedOutputList> centrifuge = new ItemHashMap();

	private ForestryRecipeHelper() {
		super();

		if (this.hasMod()) {
			Collection<ICentrifugeRecipe> c = RecipeManagers.centrifugeManager.recipes();
			for (ICentrifugeRecipe r : c) {
				ItemStack in = r.getInput();
				ChancedOutputList outputs = new ChancedOutputList();
				Map<ItemStack, Float> out = r.getAllProducts();
				for (ItemStack is : out.keySet()) {
					float chance = out.get(is)*100;
					outputs.addItem(is, chance);
				}
				centrifuge.put(in, outputs);
			}
			/*
			try {
				String pre = "forestry.factory.gadgets.MachineCentrifuge$";
				Class centri = Class.forName(pre+"RecipeManager");
				boolean p6 = SemanticVersionParser.isVersionAtLeast(this.getMod().getVersion(), "3.6");
				String rec = p6 ? pre+"CentrifugeRecipe" : pre+"Recipe";
				Class recipe = Class.forName(rec);
				Field list = centri.getDeclaredField("recipes"); //version safe
				list.setAccessible(true);
				Field input = recipe.getDeclaredField(p6 ? "input" : "resource");
				input.setAccessible(true);
				Field output = recipe.getDeclaredField(p6 ? "outputs" : "products");
				output.setAccessible(true);
				ArrayList li = (ArrayList)list.get(null);
				for (Object r : li) {
					ItemStack in = (ItemStack)input.get(r);
					Map<ItemStack, Number> out = (Map)output.get(r);
					ChancedOutputList outputs = new ChancedOutputList();
					for (ItemStack item : out.keySet()) {
						Number chance = out.get(item);
						outputs.addItem(item, p6 ? chance.floatValue()*100 : chance.intValue()); //he changed the %/1 thing again T_T
					}
					outputs.lock();
					centrifuge.put(in, outputs);
				}
			}
			catch (ClassNotFoundException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (ClassCastException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
				DragonAPICore.logError(this.getMod()+" classcast! "+e.getMessage());
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
				DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
				DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
				DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
				DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (NullPointerException e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
			}
			 */
		}
		else {
			this.noMod();
		}
	}

	public Collection<ItemStack> getCentrifugeRecipes() {
		return centrifuge.keySet();
	}

	public ChancedOutputList getRecipeOutput(ItemStack in) {
		return centrifuge.get(in).copy();
	}

	@Override
	public boolean initializedProperly() {
		return !centrifuge.isEmpty();
	}

	@Override
	public ModList getMod() {
		return ModList.FORESTRY;
	}

}

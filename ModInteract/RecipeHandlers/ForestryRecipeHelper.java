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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Instantiable.Data.Collections.ChancedOutputList;
import Reika.DragonAPI.Libraries.Java.SemanticVersionParser;

public class ForestryRecipeHelper extends ModHandlerBase {

	private static final ForestryRecipeHelper instance = new ForestryRecipeHelper();

	public static final ForestryRecipeHelper getInstance() {
		return instance;
	}

	private final HashMap<ItemStack, ChancedOutputList> centrifuge = new HashMap();

	private ForestryRecipeHelper() {
		super();

		if (this.hasMod()) {
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
				for (int i = 0; i < li.size(); i++) {
					Object r = li.get(i);
					ItemStack in = (ItemStack)input.get(r);
					HashMap<ItemStack, Number> out = (HashMap)output.get(r);
					ChancedOutputList outputs = new ChancedOutputList();
					for (ItemStack item : out.keySet()) {
						Number chance = out.get(item);
						outputs.addItem(item, p6 ? chance.floatValue() : chance.intValue());
					}
					outputs.lock();
					centrifuge.put(in, outputs);
				}
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (NullPointerException e) {
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}
	}

	public Map<ItemStack, ChancedOutputList> getCentrifugeRecipes() {
		return Collections.unmodifiableMap(centrifuge);
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

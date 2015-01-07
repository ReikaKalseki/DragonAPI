/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Instantiable.Data.ChancedOutputList;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ForestryRecipeHelper extends ModHandlerBase {

	private static final ForestryRecipeHelper instance = new ForestryRecipeHelper();

	public static final ForestryRecipeHelper getInstance() {
		return instance;
	}

	private HashMap<ItemStack, ChancedOutputList> centrifuge = new HashMap();

	private ForestryRecipeHelper() {
		super();

		if (this.hasMod()) {
			try {
				Class centri = Class.forName("forestry.factory.gadgets.MachineCentrifuge$RecipeManager");
				Class recipe = Class.forName("forestry.factory.gadgets.MachineCentrifuge$Recipe");
				Field list = centri.getField("recipes");
				Field input = recipe.getField("resource");
				Field output = recipe.getField("products");
				ArrayList li = (ArrayList)list.get(null);
				for (int i = 0; i < li.size(); i++) {
					Object r = li.get(i);
					ItemStack in = (ItemStack)input.get(r);
					HashMap<ItemStack, Integer> out = (HashMap)output.get(r);
					ChancedOutputList outputs = new ChancedOutputList();
					for (ItemStack item : out.keySet()) {
						int chance = out.get(item);
						outputs.addItem(item, chance);
					}
					outputs.lock();
					centrifuge.put(in, outputs);
				}
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal argument for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (NullPointerException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
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

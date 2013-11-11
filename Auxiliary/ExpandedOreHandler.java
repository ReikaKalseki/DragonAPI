package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import Reika.DragonAPI.Instantiable.ExpandedOreRecipe;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class ExpandedOreHandler extends TemplateRecipeHandler {

	public class ExpandedOreDictionaryRecipe extends CachedRecipe {

		private List<ExpandedOreRecipe> recipes;
		private final ItemStack output;

		private final boolean crafting;

		public ExpandedOreDictionaryRecipe(List<ExpandedOreRecipe> li, ItemStack result) {
			recipes = li;
			output = result;
			crafting = true;
		}

		public ExpandedOreDictionaryRecipe(List<ExpandedOreRecipe> li) {
			recipes = li;
			output = null;
			crafting = false;
		}

		@Override
		public List<PositionedStack> getIngredients()
		{
			ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
			ExpandedOreRecipe ir = this.getRecipe();
			ItemStack[] in = new ItemStack[9];
			ReikaRecipeHelper.copyRecipeToItemStackArray(in, ir);
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					int x = 25+j*18;
					int y = 6+i*18;
					ItemStack is = in[i*3+j];
					if (is != null)
						stacks.add(new PositionedStack(is, x, y));
				}
			}
			return stacks;
		}

		@Override
		public PositionedStack getResult() {
			ItemStack is = crafting ? output : this.getRecipe().getRecipeOutput();
			return is != null ? new PositionedStack(is, 119, 24) : null;
		}

		private int getTick() {
			return (int)((System.nanoTime()/1000000000)%recipes.size());
		}

		private ExpandedOreRecipe getRecipe() {
			return recipes.get(this.getTick());
		}
	}

	private static final List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();

	@Override
	public String getRecipeName() {
		return "Shaped Ore Crafting";
	}

	@Override
	public String getGuiTexture() {
		return "textures/gui/container/crafting_table.png";
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		List<ExpandedOreRecipe> li = ReikaRecipeHelper.getExpandedOreRecipesByOutput(recipes, result);
		if (li != null && !li.isEmpty())
			arecipes.add(new ExpandedOreDictionaryRecipe(li, result));
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		List<ExpandedOreRecipe> li = new ArrayList();
		for (int i = 0; i < recipes.size(); i++) {
			IRecipe ir = recipes.get(i);
			if (ir instanceof ExpandedOreRecipe) {
				ExpandedOreRecipe er = (ExpandedOreRecipe)ir;
				if (ReikaItemHelper.matchStacks(er.getRecipeOutput(), ingredient))
					li.add(er);
			}
		}
		if (li != null && !li.isEmpty())
			arecipes.add(new ExpandedOreDictionaryRecipe(li));
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiCrafting.class;
	}


}

package Reika.DragonAPI.Instantiable;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.DragonAPI.Instantiable.GUI.DummyContainer;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TemporaryInventoryCrafting extends InventoryCrafting {

	public final int width;
	public final int height;

	public TemporaryInventoryCrafting(int w, int h) {
		super(new DummyContainer(), w, h);
		width = w;
		height = h;
	}

	public TemporaryInventoryCrafting(ItemStack[][] in) {
		this(in.length, in[0].length);
		for (int i = 0; i < in.length; i++) {
			for (int k = 0; k < in[i].length; k++) {
				this.setItem(k, i, in[i][k]);
			}
		}
	}

	public TemporaryInventoryCrafting setItem(int x, int y, ItemStack is) {
		int slot = y*width+x;
		this.setInventorySlotContents(slot, is);
		return this;
	}

	@SideOnly(Side.CLIENT)
	public TemporaryInventoryCrafting setItems(IRecipe ir) {
		ItemStack[] disp = ReikaRecipeHelper.getPermutedRecipeArray(ir);
		for (int i = 0; i < disp.length; i++) {
			this.setInventorySlotContents(i, disp[i]);
		}
		return this;
	}

}

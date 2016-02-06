package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.item.crafting.IRecipe;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class AddRecipeEvent extends Event {

	public final IRecipe recipe;
	public static boolean isVanillaPass;

	public AddRecipeEvent(IRecipe r) {
		this(r, isVanillaPass);
	}

	public AddRecipeEvent(IRecipe r, boolean v) {
		recipe = r;
		isVanillaPass = v;
	}

}

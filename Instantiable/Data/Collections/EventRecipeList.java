package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.ArrayList;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Instantiable.Event.AddRecipeEvent;


public class EventRecipeList extends ArrayList {

	@Override
	public boolean add(Object o) {
		AddRecipeEvent evt = new AddRecipeEvent((IRecipe)o);
		if (!MinecraftForge.EVENT_BUS.post(evt)) {
			super.add(o);
			return true;
		}
		return false;
	}

}

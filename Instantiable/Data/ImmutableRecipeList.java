package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.crafting.IRecipe;

public class ImmutableRecipeList extends ArrayList {

	@Override
	public Object remove(int o) {
		throw new UnsupportedOperationException("You cannot remove recipes from this list!");
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("You cannot remove recipes from this list!");
	}

	@Override
	public boolean removeAll(Collection c)  {
		throw new UnsupportedOperationException("You cannot remove recipes from this list!");
	}

	@Override
	public boolean add(Object o) {
		if (o instanceof IRecipe) {
			return super.add(o);
		}
		else {
			throw new IllegalArgumentException("Invalid recipe object "+o+"!");
		}
	}

	@Override
	public void add(int i, Object o) {
		if (o instanceof IRecipe) {
			super.add(i, o);
		}
		else {
			throw new IllegalArgumentException("Invalid recipe object "+o+"!");
		}
	}
}

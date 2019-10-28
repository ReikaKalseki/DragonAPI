package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class RecipeStackParsingShaped extends RecipeStackParser {

	public RecipeStackParsingShaped() {
		super("net.minecraft.item.crafting.CraftingManager", "afe");
	}

	@Override
	public int getIndexVar() {
		return 4;
	}

	@Override
	public int getMapVar() {
		return 7;
	}

	@Override
	protected boolean supportsOre() {
		return false;
	}

	@Override
	public String getArrayType() {
		return "net/minecraft/item/ItemStack";
	}

	@Override
	protected MethodNode getMethod(ClassNode cn) {
		return ReikaASMHelper.getMethodByName(cn, "func_92103_a", "addRecipe", "(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)Lnet/minecraft/item/crafting/ShapedRecipes;");
	}



}

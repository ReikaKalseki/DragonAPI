package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class RecipeStackParsingOre extends RecipeStackParser {

	public RecipeStackParsingOre() {
		super("net.minecraftforge.oredict.ShapedOreRecipe");
	}

	@Override
	public int getIndexVar() {
		return 4;
	}

	@Override
	public int getMapVar() {
		return 5;
	}

	@Override
	protected boolean supportsOre() {
		return true;
	}

	@Override
	public String getArrayType() {
		return "java/lang/Object";
	}

	@Override
	protected MethodNode getMethod(ClassNode cn) {
		return ReikaASMHelper.getMethodByName(cn, "<init>", "(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)V");
	}



}

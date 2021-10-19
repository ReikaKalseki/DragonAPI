package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class DynamicEnchantability extends Patcher {

	public DynamicEnchantability() {
		super("net.minecraft.enchantment.EnumEnchantmentType", "afu");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_77557_a", "canEnchantItem", "(Lnet/minecraft/item/Item;)Z");
	}

}

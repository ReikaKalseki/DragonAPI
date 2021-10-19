package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class DynamicEnchantability extends Patcher {

	public DynamicEnchantability() {
		super("net.minecraft.enchantment.Enchantment", "aft");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_92089_a", "canApply", "(Lnet/minecraft/item/ItemStack;)Z");
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/ASMCalls", "isEnchantValidForItem", "(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)Z", false));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));
	}

}

package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

/** Vanilla relies on isOpaqueCube, which is dumb */
public class SnowAccumulationFix extends Patcher {

	public SnowAccumulationFix() {
		super("net.minecraft.block.BlockSnow", "ann");
	}

	@Override
	protected void apply(ClassNode cn) {
		String sig = "(Lnet/minecraft/world/World;III)Z";
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_149742_c", "canPlaceBlockAt", sig);
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/ASMCalls", "canSnowAccumulate", sig, false));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));
	}
}

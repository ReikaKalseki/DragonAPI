package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class ControllableVillageLists extends Patcher {

	public ControllableVillageLists() {
		super("net.minecraft.world.gen.structure.MapGenVillage$Start", "avo");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "<init>", "(Lnet/minecraft/world/World;Ljava/util/Random;III)V");
		MethodInsnNode min = (MethodInsnNode)ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.INVOKESTATIC);
		min.owner = "Reika/DragonAPI/ASM/ASMCalls";
		ReikaASMHelper.addTrailingArgument(min, "L"+cn.name.replace(".", "/")+";");
		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
	}

}

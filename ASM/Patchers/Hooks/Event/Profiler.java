package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class Profiler extends Patcher {

	public Profiler() {
		super("net.minecraft.profiler.Profiler", "qi");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_76320_a", "startSection", "(Ljava/lang/String;)V");
		m.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/ProfileEvent", "fire", "(Ljava/lang/String;)V", false));
		m.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 1));
	}
}

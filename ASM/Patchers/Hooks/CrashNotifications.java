package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class CrashNotifications extends Patcher {

	public CrashNotifications() {
		super("net.minecraft.crash.CrashReport", "b");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_71504_g", "populateEnvironment", "()V");
		InsnList li = new InsnList();
		li.add(new FieldInsnNode(Opcodes.GETSTATIC, "Reika/DragonAPI/Auxiliary/Trackers/CrashNotifications", "instance", "LReika/DragonAPI/Auxiliary/Trackers/CrashNotifications;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/Auxiliary/Trackers/CrashNotifications", "notifyCrash", "(Lnet/minecraft/crash/CrashReport;)V", false));
		m.instructions.insertBefore(m.instructions.getLast(), li);
	}
}

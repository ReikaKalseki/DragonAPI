package Reika.DragonAPI.ASM.Patchers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class ModIDCalc extends Patcher {

	public ModIDCalc() {
		super("Reika.DragonAPI.Auxiliary.Trackers.ModLockController");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "hash", "(LReika/DragonAPI/Base/DragonAPIMod;)Ljava/lang/String;");
		m.instructions.clear();


		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/Base/DragonAPIMod", "getTechnicalName", "()Ljava/lang/String;", false));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false));
		m.instructions.add(new VarInsnNode(Opcodes.ISTORE, 2));

		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/Base/DragonAPIMod", "getModVersion", "()LReika/DragonAPI/Extras/ModVersion;", false));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/Extras/ModVersion", "hashCode", "()I", false));
		m.instructions.add(new InsnNode(Opcodes.ICONST_M1));
		m.instructions.add(new InsnNode(Opcodes.IXOR));
		m.instructions.add(new VarInsnNode(Opcodes.ISTORE, 3));

		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/Base/DragonAPIMod", "getModAuthorName", "()Ljava/lang/String;", false));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false));
		m.instructions.add(new VarInsnNode(Opcodes.ISTORE, 4));

		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
		m.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 16));
		m.instructions.add(new InsnNode(Opcodes.ISHL));
		m.instructions.add(new InsnNode(Opcodes.IXOR));
		m.instructions.add(new InsnNode(Opcodes.I2L));
		m.instructions.add(new VarInsnNode(Opcodes.LSTORE, 5));

		m.instructions.add(new VarInsnNode(Opcodes.LLOAD, 5));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
		m.instructions.add(new InsnNode(Opcodes.I2L));
		m.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 48));
		m.instructions.add(new InsnNode(Opcodes.LSHL));
		m.instructions.add(new InsnNode(Opcodes.LXOR));
		m.instructions.add(new VarInsnNode(Opcodes.LSTORE, 5));

		m.instructions.add(new VarInsnNode(Opcodes.LLOAD, 5));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Long", "toHexString", "(J)Ljava/lang/String;", false));
		m.instructions.add(new InsnNode(Opcodes.ARETURN));
	}

}

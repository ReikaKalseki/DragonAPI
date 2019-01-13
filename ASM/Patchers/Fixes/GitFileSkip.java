package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class GitFileSkip extends Patcher {

	public GitFileSkip() {
		super("cpw.mods.fml.common.registry.LanguageRegistry");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "searchDirForLanguages", "(Ljava/io/File;Ljava/lang/String;Lcpw/mods/fml/relauncher/Side;)V");
		JumpInsnNode jin = (JumpInsnNode)ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.IFEQ);
		VarInsnNode vin = (VarInsnNode)ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(jin), Opcodes.ALOAD);
		LabelNode l = jin.label;

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, vin.var));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/ASMCalls", "isGitFile", "(Ljava/io/File;)Z", false));
		li.add(new JumpInsnNode(Opcodes.IFNE, l));
		m.instructions.insert(jin, li);
	}

}

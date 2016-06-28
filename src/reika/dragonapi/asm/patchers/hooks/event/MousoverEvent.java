package reika.dragonapi.asm.patchers.hooks.event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.auxiliary.CoreModDetection;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class MousoverEvent extends Patcher {

	public MousoverEvent() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78473_a", "getMouseOver", "(F)V");
		AbstractInsnNode ain = ReikaASMHelper.getNthOpcode(m.instructions, Opcodes.PUTFIELD, 2);
		m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/GetMouseoverEvent", "fire", "(F)V", false));
		m.instructions.insert(ain, new VarInsnNode(Opcodes.FLOAD, 1));
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}

	@Override
	public boolean runWithCoreMod(CoreModDetection c) {
		return c != CoreModDetection.VIVE;
	}
}

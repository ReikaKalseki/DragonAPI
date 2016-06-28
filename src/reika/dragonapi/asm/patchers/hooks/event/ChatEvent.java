package reika.dragonapi.asm.patchers.hooks.event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class ChatEvent extends Patcher {

	public ChatEvent() {
		super("net.minecraft.client.gui.GuiNewChat", "bcc");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146234_a", "printChatMessageWithOptionalDeletion", "(Lnet/minecraft/util/IChatComponent;I)V");
		m.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/ChatEvent", "firePre", "(Lnet/minecraft/util/IChatComponent;)V", false));
		m.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 1));

		AbstractInsnNode loc = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.INVOKEINTERFACE);
		m.instructions.insert(loc, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/ChatEvent", "firePost", "(Lnet/minecraft/util/IChatComponent;)V", false));
		m.instructions.insert(loc, new VarInsnNode(Opcodes.ALOAD, 1));
	}
}

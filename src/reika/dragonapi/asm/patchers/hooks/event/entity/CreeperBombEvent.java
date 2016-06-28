package reika.dragonapi.asm.patchers.hooks.event.entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class CreeperBombEvent extends Patcher {

	public CreeperBombEvent() {
		super("net.minecraft.entity.monster.EntityCreeper", "xz");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146077_cc", "func_146077_cc", "()V");
		AbstractInsnNode pos = null;
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.IFNE) {
				pos = ain;
				break;
			}
		}
		while (pos.getNext() instanceof LineNumberNode || pos.getNext() instanceof LabelNode) {
			pos = pos.getNext();
		}
		m.instructions.insert(pos, new InsnNode(Opcodes.POP));
		m.instructions.insert(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		m.instructions.insert(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/CreeperExplodeEvent", "<init>", "(Lnet/minecraft/entity/monster/EntityCreeper;)V", false));
		m.instructions.insert(pos, new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.insert(pos, new InsnNode(Opcodes.DUP));
		m.instructions.insert(pos, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/CreeperExplodeEvent"));
		m.instructions.insert(pos, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
	}

}

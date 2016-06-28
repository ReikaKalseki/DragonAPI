package reika.dragonapi.asm.patchers.hooks.event.render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class CloudRenderEvent1 extends Patcher {

	public CloudRenderEvent1() {
		super("net.minecraft.client.settings.GameSettings", "bbj");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_74309_c", "shouldRenderClouds", "()Z");
		m.instructions.clear();
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/CloudRenderEvent", "fire", "()Z", false));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));
	}
}

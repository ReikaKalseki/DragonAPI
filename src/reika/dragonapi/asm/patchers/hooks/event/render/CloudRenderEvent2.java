package reika.dragonapi.asm.patchers.hooks.event.render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class CloudRenderEvent2 extends Patcher {

	public CloudRenderEvent2() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82829_a", "renderCloudsCheck", "(Lnet/minecraft/client/renderer/RenderGlobal;F)V");
		AbstractInsnNode loc = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.IFEQ);
		while (loc.getPrevious() instanceof FieldInsnNode || loc.getPrevious() instanceof MethodInsnNode) {
			m.instructions.remove(loc.getPrevious());
		}
		m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/CloudRenderEvent", "fire", "()Z", false));
	}
}

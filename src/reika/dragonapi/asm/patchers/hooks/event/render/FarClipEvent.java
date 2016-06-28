package reika.dragonapi.asm.patchers.hooks.event.render;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class FarClipEvent extends Patcher {

	public FarClipEvent() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78479_a", "setupCameraTransform", "(FI)V");
		String fd = FMLForgePlugin.RUNTIME_DEOBF ? "field_78530_s" : "farPlaneDistance";
		InsnList add = new InsnList();
		add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/FarClippingPlaneEvent"));
		add.add(new InsnNode(Opcodes.DUP));
		add.add(new VarInsnNode(Opcodes.FLOAD, 1));
		add.add(new VarInsnNode(Opcodes.ILOAD, 2));
		add.add(new VarInsnNode(Opcodes.ALOAD, 0));
		add.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", fd, "F"));
		add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/FarClippingPlaneEvent", "<init>", "(FIF)V", false));
		add.add(new VarInsnNode(Opcodes.ASTORE, 3));
		add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		add.add(new VarInsnNode(Opcodes.ALOAD, 3));
		add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		add.add(new InsnNode(Opcodes.POP));
		add.add(new VarInsnNode(Opcodes.ALOAD, 0));
		add.add(new VarInsnNode(Opcodes.ALOAD, 3));
		add.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/Client/FarClippingPlaneEvent", "farClippingPlaneDistance", "F"));
		add.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/EntityRenderer", fd, "F"));
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.PUTFIELD) {
				m.instructions.insert(ain, add);
				break;
			}
		}
	}
}

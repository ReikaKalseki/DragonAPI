package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

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

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class EntityRender extends Patcher {

	public EntityRender() {
		super("net.minecraft.client.renderer.entity.RenderManager", "bnn");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147939_a", "func_147939_a", "(Lnet/minecraft/entity/Entity;DDDFFZ)Z");
		InsnList fire = new InsnList();
		fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		fire.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/EntityRenderEvent"));
		fire.add(new InsnNode(Opcodes.DUP));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 1));
		fire.add(new VarInsnNode(Opcodes.DLOAD, 2));
		fire.add(new VarInsnNode(Opcodes.DLOAD, 4));
		fire.add(new VarInsnNode(Opcodes.DLOAD, 6));
		fire.add(new VarInsnNode(Opcodes.FLOAD, 8));
		fire.add(new VarInsnNode(Opcodes.FLOAD, 9));
		fire.add(new VarInsnNode(Opcodes.ILOAD, 10));
		fire.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/EntityRenderEvent", "<init>", "(Lnet/minecraft/entity/Entity;DDDFFZ)V", false));
		fire.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		fire.add(new InsnNode(Opcodes.POP));
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_76986_a" : "doRender";
				if (min.name.equals(func)) {
					m.instructions.insert(min, fire);
					break;
				}
			}
		}
	}
}

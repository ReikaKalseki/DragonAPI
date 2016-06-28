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

public class WorldRender extends Patcher {

	public WorldRender() {
		super("net.minecraft.client.renderer.RenderGlobal", "bma");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147589_a", "renderEntities", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/culling/ICamera;F)V");
		InsnList fire = new InsnList();
		fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		fire.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/EntityRenderingLoopEvent"));
		fire.add(new InsnNode(Opcodes.DUP));
		fire.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/EntityRenderingLoopEvent", "<init>", "()V", false));
		fire.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		fire.add(new InsnNode(Opcodes.POP));
		AbstractInsnNode loc = null;
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_78483_a" : "disableLightmap";
				if (min.name.equals(func)) {
					while (loc == null) {
						ain = ain.getPrevious();
						if (ain.getOpcode() == Opcodes.ALOAD && ((VarInsnNode)ain).var == 0)
							loc = ain;
					}
					m.instructions.insertBefore(loc, fire);
					break;
				}
			}
		}
	}
}

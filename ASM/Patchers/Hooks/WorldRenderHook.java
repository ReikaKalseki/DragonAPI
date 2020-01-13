package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class WorldRenderHook extends Patcher {

	public WorldRenderHook() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78480_b", "updateCameraAndRender", "(F)V");
		String seek = FMLForgePlugin.RUNTIME_DEOBF ? "func_78471_a" : "renderWorld";
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.name.equals(seek)) {
					min.owner = "Reika/DragonAPI/ASM/ASMCallsClient";
					min.name = "onRenderWorld";
					min.setOpcode(Opcodes.INVOKESTATIC);
					ReikaASMHelper.addLeadingArgument(min, ReikaASMHelper.convertClassName(cn, true));
				}
			}
		}
	}

}

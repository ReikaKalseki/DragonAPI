package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class WorldGLLists extends Patcher {

	public WorldGLLists() {
		super("net.minecraft.client.renderer.RenderList", "bmd");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78419_a", "callLists", "()V");
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, "glCallLists");
		min.owner = "Reika/DragonAPI/ASM/ASMCallsClient";
		min.name = "callWorldGlLists";
		min.setOpcode(Opcodes.INVOKESTATIC);
	}

}

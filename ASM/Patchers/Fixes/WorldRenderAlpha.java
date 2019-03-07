package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.tree.ClassNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;


public class WorldRenderAlpha extends Patcher {

	public WorldRenderAlpha() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		//MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78471_a", "renderWorld", "(FJ)V");
		//MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, "glAlphaFunc");

		//LdcInsnNode ldc = (LdcInsnNode)min.getPrevious();
		//ldc.cst = 1/255F; //0.5F by default!? nuts!

		//IntInsnNode iin = (IntInsnNode)ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(min), Opcodes.SIPUSH, 3008);
		//ReikaASMHelper.deleteFrom(m.instructions, iin, min);

		//MethodInsnNode enable = (MethodInsnNode)ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(min)-1, Opcodes.INVOKESTATIC);
		//enable.name = "glDisable";
		//m.instructions.remove(min.getPrevious().getPrevious());
		//m.instructions.remove(min.getPrevious());
		//m.instructions.remove(min);

		//MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78471_a", "renderWorld", "(FJ)V");

		//  SIPUSH 518
		// LDC 0.003921569
		//  INVOKESTATIC org/lwjgl/opengl/GL11.glAlphaFunc(IF)V

		//ReikaASMHelper.log(ReikaASMHelper.clearString(m.instructions));
	}

}

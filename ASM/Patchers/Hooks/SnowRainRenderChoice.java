package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class SnowRainRenderChoice extends Patcher {

	public SnowRainRenderChoice() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78474_d", "renderRainSnow", "(F)V");
		String seek = FMLForgePlugin.RUNTIME_DEOBF ? "func_76939_a" : "getTemperatureAtHeight";
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, seek);
		VarInsnNode world = (VarInsnNode)ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(min), Opcodes.ALOAD);
		VarInsnNode height = (VarInsnNode)min.getPrevious();
		VarInsnNode temp = (VarInsnNode)height.getPrevious();
		AbstractInsnNode ref = ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(min), Opcodes.FSTORE);
		VarInsnNode z = (VarInsnNode)ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(ref), Opcodes.ILOAD);
		VarInsnNode y = (VarInsnNode)z.getPrevious();
		VarInsnNode x = (VarInsnNode)y.getPrevious();
		VarInsnNode biome = (VarInsnNode)x.getPrevious();
		InsnList li = new InsnList();
		li.add(ReikaASMHelper.copyInstruction(world));
		li.add(ReikaASMHelper.copyInstruction(biome));
		li.add(ReikaASMHelper.copyInstruction(x));
		li.add(ReikaASMHelper.copyInstruction(y));
		li.add(ReikaASMHelper.copyInstruction(z));
		li.add(ReikaASMHelper.copyInstruction(temp));
		li.add(ReikaASMHelper.copyInstruction(height));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/ASMCalls", "shouldRenderRainInsteadOfSnow", "(Lnet/minecraft/client/multiplayer/WorldClient;Lnet/minecraft/world/biome/BiomeGenBase;IIIFI)Z", false));

		JumpInsnNode jump = (JumpInsnNode)ReikaASMHelper.getFirstOpcodeAfter(m.instructions, m.instructions.indexOf(min), Opcodes.IFLT);
		ReikaASMHelper.deleteFrom(cn, m.instructions, world, jump.getPrevious());
		m.instructions.insertBefore(jump, li);
		jump.setOpcode(Opcodes.IFEQ);
	}
}

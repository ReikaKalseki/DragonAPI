package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

@Deprecated
public class CalcLightEvent extends Patcher {

	public CalcLightEvent() {
		super("net.minecraft.world.World%%", "ahb%%");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147463_c", "updateLightByType", "(Lnet/minecraft/world/EnumSkyBlock;III)Z");

		InsnList li = new InsnList();

		LabelNode L3 = new LabelNode();

		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/LightCalculationEvent", "fire", "(Lnet/minecraft/world/World;IIILnet/minecraft/world/EnumSkyBlock;)Z", false));
		li.add(new JumpInsnNode(Opcodes.IFEQ, L3));
		li.add(new InsnNode(Opcodes.ICONST_0));
		li.add(new InsnNode(Opcodes.IRETURN));
		li.add(L3);

		AbstractInsnNode ref = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.ISTORE, 5).getPrevious();
		m.instructions.insertBefore(ref, li);
	}

	@Override
	public boolean computeFrames() {
		return true;
	}
}

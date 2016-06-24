package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class FireSpreadEvent extends Patcher {

	public FireSpreadEvent() {
		super("net.minecraft.block.BlockFire", "alb");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_149674_a", "updateTick", "(Lnet/minecraft/world/World;IIILjava/util/Random;)V");
		AbstractInsnNode ain = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.LDC);
		JumpInsnNode test = (JumpInsnNode)ain.getNext().getNext();
		m.instructions.remove(ain.getPrevious());
		m.instructions.remove(ain.getNext());
		m.instructions.remove(ain);
		m.instructions.insertBefore(test, new VarInsnNode(Opcodes.ILOAD, 2));
		m.instructions.insertBefore(test, new VarInsnNode(Opcodes.ILOAD, 3));
		m.instructions.insertBefore(test, new VarInsnNode(Opcodes.ILOAD, 4));
		m.instructions.insertBefore(test, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/FireSpreadEvent", "fire", "(Lnet/minecraft/world/World;III)Z", false));
	}
}

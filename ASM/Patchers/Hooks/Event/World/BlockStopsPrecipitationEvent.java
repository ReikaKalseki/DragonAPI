package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class BlockStopsPrecipitationEvent extends Patcher {

	public BlockStopsPrecipitationEvent() {
		super("net.minecraft.world.chunk.Chunk", "apx");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_76626_d", "getPrecipitationHeight", "(II)I");
		AbstractInsnNode ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.ALOAD, 7);
		AbstractInsnNode next = ReikaASMHelper.getFirstOpcodeAfter(m.instructions, m.instructions.indexOf(ain), Opcodes.INVOKEVIRTUAL);
		next = ReikaASMHelper.getFirstOpcodeAfter(m.instructions, m.instructions.indexOf(next)+1, Opcodes.INVOKEVIRTUAL);
		JumpInsnNode jin = (JumpInsnNode)next.getNext();
		ReikaASMHelper.deleteFrom(cn, m.instructions, ain, next);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 6));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 5));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BlockStopsPrecipitationEvent", "fire", "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/block/Block;III)Z", false));

		m.instructions.insertBefore(jin, li);
	}

}



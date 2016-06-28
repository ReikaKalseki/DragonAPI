package reika.dragonapi.asm.patchers.hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class StopChunkLoadWorld extends Patcher {

	public StopChunkLoadWorld() {
		super("net.minecraft.world.World", "ahb");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72903_x", "setActivePlayerChunksAndCheckLight", "()V");

		/* String fieldName = FMLForgePlugin.RUNTIME_DEOBF ? "field_72993_I" :
		 * "activeChunkSet"; FieldInsnNode fieldNode =
		 * ReikaASMHelper.getNthFieldCall(cn, m, "net/minecraft/world/World",
		 * fieldName, 3); AbstractInsnNode before = fieldNode.getPrevious();
		 * LabelNode jumpTo = ReikaASMHelper.getFirstLabelAfter(m.instructions,
		 * m.instructions.indexOf(before));
		 * 
		 * m.instructions.insertBefore(before, new VarInsnNode(Opcodes.ALOAD,
		 * 2)); m.instructions.insertBefore(before, new
		 * MethodInsnNode(Opcodes.INVOKESTATIC,
		 * "Reika/DragonAPI/Auxiliary/Trackers/PlayerChunkTracker",
		 * "shouldStopChunkloadingFor",
		 * "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
		 * m.instructions.insertBefore(before, new JumpInsnNode(Opcodes.IFNE,
		 * jumpTo)); */

		AbstractInsnNode checkCastPlayer = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.CHECKCAST);
		AbstractInsnNode toAddBefore = checkCastPlayer.getNext().getNext();
		// the 10th is the one we're looking for. Awkward, but well...
		LabelNode jmpLabel = ReikaASMHelper.getFirstLabelAfter(m.instructions, m.instructions.indexOf(checkCastPlayer));
		for (int i = 0; i < 10; i++) {
			jmpLabel = ReikaASMHelper.getFirstLabelAfter(m.instructions, m.instructions.indexOf(jmpLabel) + 1); // 2nd
																												// label
																												// before
																												// checkCast.
		}
		m.instructions.insertBefore(toAddBefore, new VarInsnNode(Opcodes.ALOAD, 2));
		m.instructions.insertBefore(toAddBefore, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/PlayerChunkTracker", "shouldStopChunkloadingFor", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
		m.instructions.insertBefore(toAddBefore, new JumpInsnNode(Opcodes.IFNE, jmpLabel));
	}

}

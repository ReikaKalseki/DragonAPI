/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Auxiliary.CoreModDetection;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class StopChunkLoadWorld extends Patcher {

	public StopChunkLoadWorld() {
		super("net.minecraft.world.World", "ahb");
	}

	@Override
	public void apply(ClassNode cn) { //TODO could do by redirecting func_152379_p and making it return -1 (so the loops never run)
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72903_x", "setActivePlayerChunksAndCheckLight", "()V");
		if (CoreModDetection.BUKKIT.isInstalled()) {
			this.applyBukkit(m);
			return;
		}

		AbstractInsnNode checkCastPlayer = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.CHECKCAST);
		AbstractInsnNode toAddBefore = checkCastPlayer.getNext().getNext();
		LabelNode jmpLabel = ReikaASMHelper.getFirstLabelAfter(m.instructions, m.instructions.indexOf(checkCastPlayer));
		for (int i = 0; i < 10; i++) {
			jmpLabel = ReikaASMHelper.getFirstLabelAfter(m.instructions, m.instructions.indexOf(jmpLabel) + 1);
		}
		m.instructions.insertBefore(toAddBefore, new VarInsnNode(Opcodes.ALOAD, 2));
		m.instructions.insertBefore(toAddBefore, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/PlayerChunkTracker", "shouldStopChunkloadingFor", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
		m.instructions.insertBefore(toAddBefore, new JumpInsnNode(Opcodes.IFNE, jmpLabel));

		if (CoreModDetection.FASTCRAFT.isInstalled()) {
			checkCastPlayer = ReikaASMHelper.getFirstOpcodeAfter(m.instructions, m.instructions.indexOf(toAddBefore), Opcodes.CHECKCAST);
			toAddBefore = checkCastPlayer.getNext().getNext();
			jmpLabel = ReikaASMHelper.getFirstLabelAfter(m.instructions, m.instructions.indexOf(checkCastPlayer));
			for (int i = 0; i < 4; i++) {
				jmpLabel = ReikaASMHelper.getFirstLabelAfter(m.instructions, m.instructions.indexOf(jmpLabel) + 1);
			}
			m.instructions.insertBefore(toAddBefore, new VarInsnNode(Opcodes.ALOAD, 2));
			m.instructions.insertBefore(toAddBefore, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/PlayerChunkTracker", "shouldStopChunkloadingFor", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
			m.instructions.insertBefore(toAddBefore, new JumpInsnNode(Opcodes.IFNE, jmpLabel));
		}
	}

	//Credit to MozG/Brain for the bukkit-compatible implementation; all comments are theirs (though translated from Russian)
	private void applyBukkit(MethodNode m) {
		AbstractInsnNode checkCastPlayer = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.CHECKCAST, "net/minecraft/entity/player/EntityPlayer");
		AbstractInsnNode toAddBefore = checkCastPlayer.getNext().getNext();
		// let's change the search logic a bit, since all possible kernels change below this loop for instructions
		// therefore we won't look for the next LabelNode, but for the previous one, the one from which the for loop was started. It has one LabelNode through it all
		LabelNode jmpLabel = ReikaASMHelper.getFirstLabelBefore(m.instructions, m.instructions.indexOf(checkCastPlayer) - 2);
		jmpLabel = ReikaASMHelper.getFirstLabelBefore(m.instructions, m.instructions.indexOf(jmpLabel) - 1);
		//now let's find next Jump which will bind to a found LabelNode
		JumpInsnNode fromJump = ReikaASMHelper.getFirstJumpFromLabel(m.instructions, m.instructions.indexOf(jmpLabel), jmpLabel);
		// now we've found the LabelNode we need
		jmpLabel = ReikaASMHelper.getFirstLabelBefore(m.instructions, m.instructions.indexOf(fromJump) - 1);

		// Spigot, CraftBukkit, Cauldron hacking fixes. And shift this parameter to 7 or another memory fingerprint
		int aloadVar = 2;
		AbstractInsnNode astore = toAddBefore.getPrevious();
		if (astore instanceof VarInsnNode && ((VarInsnNode) astore).getOpcode() == Opcodes.ASTORE)
			aloadVar = ((VarInsnNode) astore).var;

		m.instructions.insertBefore(toAddBefore, new VarInsnNode(Opcodes.ALOAD, aloadVar));
		m.instructions.insertBefore(toAddBefore, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/PlayerChunkTracker", "shouldStopChunkloadingFor", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
		m.instructions.insertBefore(toAddBefore, new JumpInsnNode(Opcodes.IFNE, jmpLabel));

		// Let's make this method like above, but without comments :)
		if (CoreModDetection.FASTCRAFT.isInstalled()) {
			checkCastPlayer = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(toAddBefore), Opcodes.CHECKCAST, "net/minecraft/entity/player/EntityPlayer");
			toAddBefore = checkCastPlayer.getNext();

			jmpLabel = ReikaASMHelper.getFirstLabelBefore(m.instructions, m.instructions.indexOf(checkCastPlayer) - 2);
			jmpLabel = ReikaASMHelper.getFirstLabelBefore(m.instructions, m.instructions.indexOf(jmpLabel) - 1);

			fromJump = ReikaASMHelper.getFirstJumpFromLabel(m.instructions, m.instructions.indexOf(jmpLabel), jmpLabel);
			jmpLabel = ReikaASMHelper.getFirstLabelBefore(m.instructions, m.instructions.indexOf(fromJump) - 1);

			aloadVar = 2;
			astore = toAddBefore.getPrevious();
			if (astore instanceof VarInsnNode && ((VarInsnNode) astore).getOpcode() == Opcodes.ASTORE)
				aloadVar = ((VarInsnNode) astore).var;

			m.instructions.insertBefore(toAddBefore, new VarInsnNode(Opcodes.ALOAD, aloadVar));
			m.instructions.insertBefore(toAddBefore, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/PlayerChunkTracker", "shouldStopChunkloadingFor", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
			m.instructions.insertBefore(toAddBefore, new JumpInsnNode(Opcodes.IFNE, jmpLabel));
		}

	}

	@Override
	public boolean computeFrames() {
		return true;
	}

}

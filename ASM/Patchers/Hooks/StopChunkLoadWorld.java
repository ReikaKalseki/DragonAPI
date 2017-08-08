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
	public void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72903_x", "setActivePlayerChunksAndCheckLight", "()V");

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

}

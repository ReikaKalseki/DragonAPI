/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class StopChunkLoadPacket extends Patcher {

	public StopChunkLoadPacket() {
		super("net.minecraft.server.management.ServerConfigurationManager", "oi");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72358_d", "updatePlayerPertinentChunks", "(Lnet/minecraft/entity/player/EntityPlayerMP;)V");

		AbstractInsnNode before = ReikaASMHelper.getFirstLabelAfter(m.instructions, 0).getNext();
		LabelNode jmpTo = ReikaASMHelper.getFirstLabelAfter(m.instructions, m.instructions.indexOf(before) + 1);

		m.instructions.insertBefore(before, new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.insertBefore(before, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/PlayerChunkTracker", "shouldStopChunkloadingFor", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
		m.instructions.insertBefore(before, new JumpInsnNode(Opcodes.IFNE, jmpTo));
	}

}

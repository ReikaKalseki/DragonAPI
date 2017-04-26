/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World.Gen;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class ChunkGenerationEvent extends Patcher {

	public ChunkGenerationEvent() {
		super("net.minecraft.world.gen.ChunkProviderServer", "ms");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "originalLoadChunk", "(II)Lnet/minecraft/world/chunk/Chunk;");
		AbstractInsnNode ain = ReikaASMHelper.getLastInsn(m.instructions, Opcodes.ASTORE, 5);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 5));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/ChunkGenerationEvent", "fire", "(Lnet/minecraft/world/chunk/Chunk;)V", false));
		m.instructions.insert(ain, li);
	}

}

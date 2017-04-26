/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Block;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public abstract class BlockSpreadingEvent extends Patcher {

	public BlockSpreadingEvent(String deobf, String obf) {
		super(deobf, obf);
	}

	@Override
	protected final void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_149674_a", "updateTick", "(Lnet/minecraft/world/World;IIILjava/util/Random;)V");

		/*
		AbstractInsnNode ref = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.INVOKEVIRTUAL);
		AbstractInsnNode first = ref;//.getPrevious();//keep the block arg
		AbstractInsnNode last = ref.getNext();
		ref = ref.getPrevious();//.getPrevious();

		ReikaASMHelper.deleteFrom(m.instructions, first, last);

		m.instructions.insert(ref, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BlockSpreadEvent", "fire", "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;)V", false));

		 */
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BlockSpreadEvent", "fire", "(Lnet/minecraft/world/World;IIILjava/util/Random;Lnet/minecraft/block/Block;)V", false));
		m.instructions.add(new InsnNode(Opcodes.RETURN));
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class AttackAggroEvent1 extends Patcher {

	public AttackAggroEvent1() {
		super("net.minecraft.entity.monster.EntityMob", "yg");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70097_a", "attackEntityFrom", "(Lnet/minecraft/util/DamageSource;F)Z");
		AbstractInsnNode loc = ReikaASMHelper.getNthOfOpcodes(m.instructions, 3, Opcodes.IF_ACMPEQ, Opcodes.IF_ACMPNE);

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.FLOAD, 2));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/AttackAggroEvent", "fire", "(Lnet/minecraft/entity/monster/EntityMob;Lnet/minecraft/util/DamageSource;F)Z", false));

		ReikaASMHelper.changeOpcode(loc, Opcodes.IFEQ);
		m.instructions.insertBefore(loc.getPrevious().getPrevious(), li);
		m.instructions.remove(loc.getPrevious().getPrevious());
		m.instructions.remove(loc.getPrevious());

		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}
}

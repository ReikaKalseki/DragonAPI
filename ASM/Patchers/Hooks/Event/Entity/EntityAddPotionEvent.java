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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class EntityAddPotionEvent extends Patcher {

	public EntityAddPotionEvent() {
		super("net.minecraft.entity.EntityLivingBase", "sv");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70690_d", "addPotionEffect", "(Lnet/minecraft/potion/PotionEffect;)V");
		JumpInsnNode skip = (JumpInsnNode)ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.IFEQ);
		JumpInsnNode nul = new JumpInsnNode(Opcodes.IFNULL, skip.label);

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/ApplyPotionEvent", "fire", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/PotionEffect;)Lnet/minecraft/potion/PotionEffect;", false));
		li.add(new VarInsnNode(Opcodes.ASTORE, 1));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(nul);

		m.instructions.insertBefore(ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.ALOAD, 0), li);
	}
}

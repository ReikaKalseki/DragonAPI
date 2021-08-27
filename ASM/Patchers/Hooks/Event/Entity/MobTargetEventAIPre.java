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
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class MobTargetEventAIPre extends Patcher {

	public MobTargetEventAIPre() {
		super("net.minecraft.entity.ai.EntityAINearestAttackableTarget$1", "vp"); //anony class
	}

	@Override
	protected void apply(ClassNode cn) {
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_75296_a" : "isSuitableTarget";
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/ai/EntityAINearestAttackableTarget$1", "this$0", "Lnet/minecraft/entity/ai/EntityAINearestAttackableTarget;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/MobTargetingEvent", "fireAIPre", "(ZLnet/minecraft/entity/ai/EntityAINearestAttackableTarget;Lnet/minecraft/entity/Entity;)Z", false));
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82704_a", "isEntityApplicable", "(Lnet/minecraft/entity/Entity;)Z");
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, func);
		m.instructions.insert(min, li);
	}
}

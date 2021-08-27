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

public class MobTargetEventAIPost extends Patcher {

	public MobTargetEventAIPost() {
		super("net.minecraft.entity.ai.EntityAINearestAttackableTarget", "vo");
	}

	@Override
	protected void apply(ClassNode cn) {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/MobTargetingEvent", "fireAIPost", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/ai/EntityAINearestAttackableTarget;)Lnet/minecraft/entity/EntityLivingBase;", false));
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_75250_a", "shouldExecute", "()Z");
		FieldInsnNode fin = ReikaASMHelper.getFirstFieldCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "field_75309_a" : "targetEntity");
		m.instructions.insertBefore(fin, li);
	}
}

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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class PushEntityOut extends Patcher {

	public PushEntityOut() {
		super("net.minecraft.entity.Entity", "sa");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_145771_j", "func_145771_j", "(DDD)Z");
		/*
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_147461_a" : "func_147461_a");
		min.owner = "Reika/DragonAPI/Instantiable/Event/EntityPushOutOfBlocksEvent";
		min.name = "checkAABBs";
		ReikaASMHelper.addLeadingArgument(min, "Lnet/minecraft/world/World;");
		ReikaASMHelper.addTrailingArgument(min, ReikaASMHelper.convertClassName(cn, true));
		min.setOpcode(Opcodes.INVOKESTATIC);
		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
		 */
		int var = 16;
		AbstractInsnNode loc1 = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.ALOAD, var);
		AbstractInsnNode loc2 = ReikaASMHelper.getFirstMethodCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_147469_q" : "func_147469_q");
		AbstractInsnNode label = loc2.getNext();
		ReikaASMHelper.changeOpcode(label, Opcodes.IFEQ);
		ReikaASMHelper.deleteFrom(cn, m.instructions, loc1, loc2);
		m.instructions.insertBefore(label, new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.insertBefore(label, new VarInsnNode(Opcodes.ALOAD, var));
		m.instructions.insertBefore(label, new VarInsnNode(Opcodes.ILOAD, 7)); //ijk
		m.instructions.insertBefore(label, new VarInsnNode(Opcodes.ILOAD, 8));
		m.instructions.insertBefore(label, new VarInsnNode(Opcodes.ILOAD, 9));
		m.instructions.insertBefore(label, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/EntityPushOutOfBlocksEvent", "fire", "(Lnet/minecraft/entity/Entity;Ljava/util/List;III)Z", false));
	}
}

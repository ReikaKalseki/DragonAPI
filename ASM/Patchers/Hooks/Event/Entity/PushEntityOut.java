/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class PushEntityOut extends Patcher {

	public PushEntityOut() {
		super("net.minecraft.entity.Entity", "sa");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_145771_j", "func_145771_j", "(DDD)Z");
		InsnList add = new InsnList();
		LabelNode L1 = new LabelNode();
		LabelNode L2 = new LabelNode();
		add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/EntityPushOutOfBlocksEvent"));
		add.add(new InsnNode(Opcodes.DUP));
		add.add(new VarInsnNode(Opcodes.ALOAD, 0));
		add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/EntityPushOutOfBlocksEvent", "<init>", "(Lnet/minecraft/entity/Entity;)V", false));
		add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		add.add(new JumpInsnNode(Opcodes.IFEQ, L1));
		add.add(L2);
		add.add(new InsnNode(Opcodes.ICONST_0));
		add.add(new InsnNode(Opcodes.IRETURN));
		add.add(L1);
		m.instructions.insert(add);
	}
}

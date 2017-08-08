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
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class ItemUpdate extends Patcher {

	public ItemUpdate() {
		super("net.minecraft.entity.item.EntityItem", "xk");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70071_h_", "onUpdate", "()V");
		InsnList fire = new InsnList();
		fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		fire.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/ItemUpdateEvent"));
		fire.add(new InsnNode(Opcodes.DUP));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 0));
		fire.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/ItemUpdateEvent", "<init>", "(Lnet/minecraft/entity/item/EntityItem;)V", false));
		fire.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		fire.add(new InsnNode(Opcodes.POP));

		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.name.equals(m.name) && min.owner.equals("net/minecraft/entity/Entity")) {
					m.instructions.insert(min, fire);
					break;
				}
			}
		}
	}
}

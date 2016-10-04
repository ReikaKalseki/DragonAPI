/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Item;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class ItemStackUpdateEvent extends Patcher {

	public ItemStackUpdateEvent() {
		super("net.minecraft.item.ItemStack", "add");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_77945_a", "updateAnimation", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V");
		AbstractInsnNode update = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.INVOKEVIRTUAL);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/ItemStackUpdateEvent", "fire", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/Entity;IZ)V", false));
		m.instructions.insert(update, li);
	}

}

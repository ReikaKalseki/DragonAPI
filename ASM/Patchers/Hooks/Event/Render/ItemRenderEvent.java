/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class ItemRenderEvent extends Patcher {

	public ItemRenderEvent() {
		super("net.minecraft.client.gui.inventory.GuiContainer", "bex");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146977_a", "func_146977_a", "(Lnet/minecraft/inventory/Slot;)V");
		AbstractInsnNode pos = m.instructions.getFirst();
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/RenderItemInSlotEvent", "firePre", "(Lnet/minecraft/client/gui/inventory/GuiContainer;Lnet/minecraft/inventory/Slot;)V", false));

		AbstractInsnNode ref = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.LDC, -2130706433);
		VarInsnNode stack = (VarInsnNode)ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(ref), Opcodes.ALOAD, 4);
		InsnList call = new InsnList();
		call.add(new VarInsnNode(Opcodes.ALOAD, 0)); //gui
		call.add(new VarInsnNode(Opcodes.ALOAD, 1)); //slot
		call.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/RenderItemInSlotEvent", "fireMid", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/gui/inventory/GuiContainer;Lnet/minecraft/inventory/Slot;)Lnet/minecraft/item/ItemStack;", false));
		while (stack != null) {
			m.instructions.insert(stack, ReikaASMHelper.copyInsnList(call));
			stack = (VarInsnNode)ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(stack)+1, stack.getOpcode(), stack.var);
		}

		Collection<AbstractInsnNode> li = new ArrayList();

		for (int i = 0; i < m.instructions.size(); i++) {
			pos = m.instructions.get(i);
			if (pos.getOpcode() == Opcodes.RETURN) {
				li.add(pos);
			}
		}

		for (AbstractInsnNode pos2 : li) {
			m.instructions.insertBefore(pos2, new VarInsnNode(Opcodes.ALOAD, 0));
			m.instructions.insertBefore(pos2, new VarInsnNode(Opcodes.ALOAD, 1));
			m.instructions.insertBefore(pos2, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/RenderItemInSlotEvent", "firePost", "(Lnet/minecraft/client/gui/inventory/GuiContainer;Lnet/minecraft/inventory/Slot;)V", false));
		}
	}

}

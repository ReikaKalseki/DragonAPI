/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
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
		m.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		m.instructions.insertBefore(pos, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/RenderItemInSlotEvent"));
		m.instructions.insertBefore(pos, new InsnNode(Opcodes.DUP));
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/RenderItemInSlotEvent", "<init>", "(Lnet/minecraft/client/gui/inventory/GuiContainer;Lnet/minecraft/inventory/Slot;)V", false));
		m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		m.instructions.insertBefore(pos, new InsnNode(Opcodes.POP));
	}

}

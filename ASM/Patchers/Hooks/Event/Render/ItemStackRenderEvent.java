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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class ItemStackRenderEvent extends Patcher {

	public ItemStackRenderEvent() {
		super("net.minecraft.client.renderer.entity.RenderItem", "bny");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82406_b", "renderItemAndEffectIntoGUI", "(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;II)V");
		AbstractInsnNode pos = m.instructions.getFirst();
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 3));
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ILOAD, 4));
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ILOAD, 5));
		m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/RenderItemStackEvent", "firePre", "(Lnet/minecraft/item/ItemStack;II)V", false));

		pos = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.RETURN);
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 3));
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ILOAD, 4));
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ILOAD, 5));
		m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/RenderItemStackEvent", "firePost", "(Lnet/minecraft/item/ItemStack;II)V", false));

	}

}

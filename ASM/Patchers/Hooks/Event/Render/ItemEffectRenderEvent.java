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

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class ItemEffectRenderEvent extends Patcher {

	public ItemEffectRenderEvent() {
		super("net.minecraft.client.renderer.entity.RenderItem", "bny");
	}

	@Override
	protected void apply(ClassNode cn) {
		//forge
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "renderDroppedItem", "(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/IIcon;IFFFFI)V");
		String name = "hasEffect"; //Forge one //FMLForgePlugin.RUNTIME_DEOBF ? "func_77962_s" : "hasEffect";
		/*
		Object[] patt = {
				new VarInsnNode(Opcodes.ALOAD, 19),
				new VarInsnNode(Opcodes.ILOAD, 8),
				Opcodes.INVOKEVIRTUAL,
				Opcodes.IFEQ,
		};
		AbstractInsnNode ain = ReikaASMHelper.getPattern(m.instructions, patt);
		if (ain == null) {
			ReikaASMHelper.throwConflict(this.toString(), cn, m, "Could not find instruction pattern for ItemStack.hasEffect(int pass), Insns:\n"+ReikaASMHelper.clearString(m.instructions));
		}
		MethodInsnNode min = (MethodInsnNode)ain.getNext().getNext();//ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/item/ItemStack", name, "(I)Z");
		 */
		this.patchMethod(m, name);

		//forge
		m = ReikaASMHelper.getMethodByName(cn, "renderItemIntoGUI", "(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IIZ)V");
		this.patchMethod(m, name);
	}

	private void patchMethod(MethodNode m, String name) {
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.name.equals(name) && min.desc.equals("(I)Z")) {
					min.owner = "Reika/DragonAPI/Instantiable/Event/Client/ItemEffectRenderEvent";
					min.name = "fire";
					min.desc = "(Lnet/minecraft/item/ItemStack;I)Z";
					min.setOpcode(Opcodes.INVOKESTATIC);
					//ReikaASMHelper.log("Replaced ItemStack.hasEffect(I)Z call, neighbors =\n"+ReikaASMHelper.clearString(min.getPrevious().getPrevious())+ReikaASMHelper.clearString(min.getPrevious())+"\t[call]\n"+ReikaASMHelper.clearString(min.getNext()));
				}
			}
		}
	}

}

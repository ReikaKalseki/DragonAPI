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

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class HeldRenderEvent extends Patcher {

	public HeldRenderEvent() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78476_b", "renderHand", "(FI)V");
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_78440_a" : "renderItemInFirstPerson";
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.name.equals(func)) {
					m.instructions.insert(ain, new InsnNode(Opcodes.POP));
					m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/RenderFirstPersonItemEvent", "<init>", "()V", false));
					m.instructions.insert(ain, new InsnNode(Opcodes.DUP));
					m.instructions.insert(ain, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/RenderFirstPersonItemEvent"));
					m.instructions.insert(ain, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					break;
				}
			}
		}
	}

}

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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class CloudRenderEvent2 extends Patcher {

	public CloudRenderEvent2() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82829_a", "renderCloudsCheck", "(Lnet/minecraft/client/renderer/RenderGlobal;F)V");
		AbstractInsnNode loc = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.IFEQ);
		while (loc.getPrevious() instanceof FieldInsnNode || loc.getPrevious() instanceof MethodInsnNode) {
			m.instructions.remove(loc.getPrevious());
		}
		m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/CloudRenderEvent", "fire", "()Z", false));
	}
}

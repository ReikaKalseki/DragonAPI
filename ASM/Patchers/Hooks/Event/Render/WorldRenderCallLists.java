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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class WorldRenderCallLists extends Patcher {

	public WorldRenderCallLists() {
		super("net.minecraft.client.renderer.RenderGlobal", "bma");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72724_a", "renderSortedRenderers", "(IIID)I");
		MethodInsnNode min = (MethodInsnNode)ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.INVOKEVIRTUAL);
		min.owner = "Reika/DragonAPI/ASM/ASMCalls";
		min.name = "onCallChunkRenderLists";
		min.setOpcode(Opcodes.INVOKESTATIC);
		ReikaASMHelper.addLeadingArgument(min, ReikaASMHelper.convertClassName(cn, true));
	}
}

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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class WorldRenderGetLists extends Patcher {

	public WorldRenderGetLists() {
		super("net.minecraft.client.renderer.WorldRenderer", "blo");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78909_a", "getGLCallListForPass", "(I)I");
		//m.instructions.clear();
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		//li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		//li.add(new VarInsnNode(Opcodes.GETFIELD, ReikaASMHelper.convertClassName(cn, false)));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/ChunkWorldRenderEvent", "fire", "(ILnet/minecraft/client/renderer/WorldRenderer;I)I", false));
		AbstractInsnNode loc = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.IRETURN);
		m.instructions.insertBefore(loc, li);
	}
}

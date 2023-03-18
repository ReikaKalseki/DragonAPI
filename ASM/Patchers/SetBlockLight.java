/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Auxiliary.CoreModDetection;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class SetBlockLight extends Patcher {

	public SetBlockLight() {
		super("net.minecraft.world.World", "ahb");
	}

	@Override
	protected void apply(ClassNode cn) {
		if (CoreModDetection.FASTCRAFT.isInstalled()) {
			ReikaASMHelper.log("Skipping " + this + " ASM handler 1; not compatible with FastCraft");
		}
		else {
			MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147465_d", "setBlock", "(IIILnet/minecraft/block/Block;II)Z");
			//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
			String cl = /*CoreModDetection.fastCraftInstalled() ? "fastcraft/J" : */cn.name;
			String func = /*CoreModDetection.fastCraftInstalled() ? "d" : */"func_147451_t";
			String sig = /*CoreModDetection.fastCraftInstalled() ? "(Lnet/minecraft/world/World;III)Z" : */"(III)Z";
			MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, cl, func, sig);
			min.owner = "Reika/DragonAPI/ASM/DragonAPIClassTransformer";
			min.name = "updateSetBlockLighting";
			min.desc = "(IIILnet/minecraft/world/World;I)Z";
			min.setOpcode(Opcodes.INVOKESTATIC);
			m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
			m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 5));
			ReikaASMHelper.log("Applied " + this + " ASM handler 1a!");
		}

		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147451_t", "func_147451_t", "(III)Z");
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/DragonAPIClassTransformer", "doLightUpdate", "(Lnet/minecraft/world/World;III)Z", false));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));
		ReikaASMHelper.log("Applied " + this + " ASM handler 2!");
	}

	@Override
	public boolean computeFrames() {
		return true;
	}

	@Override
	public boolean runWithCoreMod(CoreModDetection c) {
		return true;//c != CoreModDetection.FASTCRAFT;
	}
}

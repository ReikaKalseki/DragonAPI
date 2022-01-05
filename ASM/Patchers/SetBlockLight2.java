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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.Auxiliary.CoreModDetection;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class SetBlockLight2 extends Patcher {

	public SetBlockLight2() {
		super("net.minecraft.world.chunk.Chunk", "apx");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_150807_a", "(IIILnet/minecraft/block/Block;I)Z");
		String cl = /*CoreModDetection.fastCraftInstalled() ? "fastcraft/J" : */cn.name;
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_76615_h" : "relightBlock";
		String sig = /*CoreModDetection.fastCraftInstalled() ? "(Lnet/minecraft/world/World;III)Z" : */"(III)V";
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain instanceof MethodInsnNode) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.name.equals(func)) {
					min.owner = "Reika/DragonAPI/ASM/DragonAPIClassTransformer";
					min.name = "updateSetBlockRelight";
					min.desc = "(Lnet/minecraft/world/chunk/Chunk;IIII)V";
					min.setOpcode(Opcodes.INVOKESTATIC);
					m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 5));
					ReikaASMHelper.log("Applied " + this + " ASM handler @ "+i+"!");
					i += 3;
				}
			}
		}
	}

	@Override
	public boolean runWithCoreMod(CoreModDetection c) {
		return c != CoreModDetection.FASTCRAFT;
	}
}

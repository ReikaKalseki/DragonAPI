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

import net.minecraft.world.biome.BiomeGenBase;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class DebugASM extends Patcher {

	public DebugASM() {
		super("",""/*"net.minecraft.world.biome.BiomeGenBase", "ahu"*/);
	}

	@Override
	protected void apply(ClassNode cn) {
		String sig = "(I)Lnet/minecraft/world/biome/BiomeGenBase;";
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_150568_d", "getBiome", sig);
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/Patchers/DebugASM", "lookup", sig, false));
		m.instructions.add(new InsnNode(Opcodes.ARETURN));
	}

	public static BiomeGenBase lookup(int id) {
		if (id < 0 || id > 255)
			throw new IllegalArgumentException("Tried to fetch biome out of bounds!");
		if (BiomeGenBase.biomeList[id] == null)
			throw new IllegalArgumentException("Tried to fetch biome ID "+id+", which does not exist!!");
		return BiomeGenBase.biomeList[id];
	}

}

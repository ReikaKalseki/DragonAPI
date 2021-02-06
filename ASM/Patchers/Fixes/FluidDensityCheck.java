/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

/** Fixes Forge only checking for BlockFluidBase, which neither vanilla, CoFH's overwrites, nor some mod fluids extend. Also
 * allows location-specific density, which would be the only way to implement temperature-specific density (as real fluids have).*/
public class FluidDensityCheck extends Patcher {

	public FluidDensityCheck() {
		super("net.minecraftforge.fluids.BlockFluidBase");
	}

	@Override
	protected void apply(ClassNode cn) {
		String sig = "(Lnet/minecraft/world/IBlockAccess;III)I";
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "getDensity", sig);
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/ASMCalls", "getDensityOverride", sig, false));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));
	}

}

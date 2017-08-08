/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class PermutedBiomeReg extends Patcher {

	public PermutedBiomeReg() {
		super("net.minecraftforge.common.BiomeDictionary");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "registerVanillaBiomes", "()V");
		m.instructions.insertBefore(m.instructions.getLast(), new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/DragonAPIClassTransformer", "registerPermutedBiomesToDictionary", "()V", false));
	}
}

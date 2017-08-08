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

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class PotionPacketID2 extends Patcher {

	public PotionPacketID2() {
		super("net.minecraft.client.network.NetHandlerPlayClient", "bjb");
	}

	@Override
	protected void apply(ClassNode cn) { // Changes the call to func_149427_e,
		// which otherwise looks for ()B and
		// NSMEs
		// if (Loader.isModLoaded("Potion ID Helper"))
		// break;
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147260_a", "handleEntityEffect", "(Lnet/minecraft/network/play/server/S1DPacketEntityEffect;)V");
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_149427_e" : "func_149427_e";
				if (min.name.equals(func)) {
					min.desc = "()I";
					break;
				}
			}
		}
	}

}

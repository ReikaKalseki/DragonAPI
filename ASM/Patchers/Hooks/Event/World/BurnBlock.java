/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class BurnBlock extends Patcher {

	public BurnBlock() {
		super("net.minecraft.block.BlockFire", "alb");
	}

	private void redirectMethod(ClassNode cn, MethodNode m, String func, String sig, String call) {
		String world = "net/minecraft/world/World";
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, world, func, sig);
		min.setOpcode(Opcodes.INVOKESTATIC);
		min.owner = "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent";
		min.name = call;
		ReikaASMHelper.addLeadingArgument(min, "L"+world+";");
	}

	private void redirectSetBlock(ClassNode cn, MethodNode m) {
		this.redirectMethod(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_147465_d" : "setBlock", "(IIILnet/minecraft/block/Block;II)Z", "fireFromSetBlock");
	}

	private void redirectSetBlockAir(ClassNode cn, MethodNode m) {
		this.redirectMethod(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_147468_f" : "setBlockToAir", "(III)Z", "fireFromSetBlockAir");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_149674_a", "updateTick", "(Lnet/minecraft/world/World;IIILjava/util/Random;)V");
		this.redirectSetBlock(cn, m);


		m = ReikaASMHelper.getMethodByName(cn, /*"func_149841_a", */"tryCatchFire", "(Lnet/minecraft/world/World;IIIILjava/util/Random;ILnet/minecraftforge/common/util/ForgeDirection;)V"); // Forge one
		this.redirectSetBlock(cn, m);
		this.redirectSetBlockAir(cn, m);
	}
}

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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class BlockTickEvent extends Patcher {

	private final String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_149674_a" : "updateTick";
	//private final String sig = "(Lnet/minecraft/world/World;IIILjava/util/Random;)V";

	public BlockTickEvent() {
		super("net.minecraft.world.WorldServer", "mt");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147456_g", "func_147456_g", "()V");
		this.redirectTickMethodCall(m, "NATURAL");

		m = ReikaASMHelper.getMethodByName(cn, "func_147454_a", "scheduleBlockUpdateWithPriority", "(IIILnet/minecraft/block/Block;II)V");
		this.redirectTickMethodCall(m, "SCHEDULED");

		m = ReikaASMHelper.getMethodByName(cn, "func_72955_a", "tickUpdates", "(Z)Z");
		this.redirectTickMethodCall(m, "SCHEDULED");
	}

	private void redirectTickMethodCall(MethodNode m, String flagType) {
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain instanceof MethodInsnNode) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.name.equals(func)) {
					min.setOpcode(Opcodes.INVOKESTATIC);
					min.owner = "Reika/DragonAPI/Instantiable/Event/BlockTickEvent";
					min.name = "fire";
					ReikaASMHelper.addLeadingArgument(min, "Lnet/minecraft/block/Block;");
					ReikaASMHelper.addTrailingArgument(min, "LReika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags;");
					FieldInsnNode flag = new FieldInsnNode(Opcodes.GETSTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", flagType, "LReika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags;");
					m.instructions.insertBefore(min, flag);
				}
			}
		}
	}
}

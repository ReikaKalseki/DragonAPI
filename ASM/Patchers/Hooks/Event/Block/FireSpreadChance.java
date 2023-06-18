/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Block;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Auxiliary.CoreModDetection;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class FireSpreadChance extends Patcher {

	public FireSpreadChance() {
		super("net.minecraft.block.BlockFire", "alb");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = null;
		m = ReikaASMHelper.getMethodByName(cn, "tryCatchFire", "(Lnet/minecraft/world/World;IIIILjava/util/Random;ILnet/minecraftforge/common/util/ForgeDirection;)V");
		AbstractInsnNode ain = CoreModDetection.BUKKIT.isInstalled() ? ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.ISTORE) : ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.ISTORE, 9);

		InsnList li = new InsnList();

		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new VarInsnNode(Opcodes.ALOAD, 8));
		li.add(new VarInsnNode(Opcodes.ILOAD, 9));
		String sig = "(Lnet/minecraft/world/World;IIILnet/minecraftforge/common/util/ForgeDirection;I)I";
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/FireChanceEvent", "fire", sig, false));
		li.add(new VarInsnNode(Opcodes.ISTORE, 9));

		//li.add(new VarInsnNode(Opcodes.ILOAD, 7));
		//li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/FireChanceEvent", "pass2", "(I)I", false));
		//li.add(new VarInsnNode(Opcodes.ISTORE, 7));

		m.instructions.insert(ain, li);

		ain = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.ICONST_5);
		m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/FireChanceEvent", "pass2", "(I)I", false));
	}

}

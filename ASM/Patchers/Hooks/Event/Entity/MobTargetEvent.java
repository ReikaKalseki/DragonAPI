/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class MobTargetEvent extends Patcher {

	public MobTargetEvent() {
		super("net.minecraft.world.World", "ahb");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72846_b", "getClosestVulnerablePlayer", "(DDDD)Lnet/minecraft/entity/player/EntityPlayer;");
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 3)); //+2 since double is 2 spots
		m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 5));
		m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 7));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Libraries/World/ReikaWorldHelper", "getClosestVulnerablePlayer", "(Lnet/minecraft/world/World;DDDD)Lnet/minecraft/entity/player/EntityPlayer;", false));
		m.instructions.add(new InsnNode(Opcodes.ARETURN));
	}
}

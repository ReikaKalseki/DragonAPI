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


public class TamedMobBeds extends Patcher {

	public TamedMobBeds() {
		super("net.minecraft.entity.player.EntityPlayer", "yz");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_71018_a", "sleepInBedAt", "(III)Lnet/minecraft/entity/player/EntityPlayer$EnumStatus;");
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, "java/util/List", "isEmpty", "()Z");
		min.setOpcode(Opcodes.INVOKESTATIC);
		min.owner = "Reika/DragonAPI/ASM/DragonAPIClassTransformer";
		min.name = "allowMobSleeping";
		min.desc = "(Ljava/util/List;)Z";
		min.itf = false;
	}

}

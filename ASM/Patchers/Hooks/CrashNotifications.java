/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class CrashNotifications extends Patcher {

	public CrashNotifications() {
		super("net.minecraft.crash.CrashReport", "b");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_71504_g", "populateEnvironment", "()V");

		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, "enhanceCrashReport");
		min.owner = "Reika/DragonAPI/Auxiliary/Trackers/CrashNotifications";
		min.name = "fire";
		min.desc = "(Lcpw/mods/fml/common/FMLCommonHandler;Lnet/minecraft/crash/CrashReport;Lnet/minecraft/crash/CrashReportCategory;)V";
		min.setOpcode(Opcodes.INVOKESTATIC);
	}
}

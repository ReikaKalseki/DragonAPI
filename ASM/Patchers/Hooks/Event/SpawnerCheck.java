/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class SpawnerCheck extends Patcher {

	public SpawnerCheck() {
		super("net.minecraft.tileentity.MobSpawnerBaseLogic", "agq");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_98278_g", "updateSpawner", "()V");
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_70601_bi" : "getCanSpawnHere";
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/entity/EntityLiving", func, "()Z");
		min.owner = "Reika/DragonAPI/Instantiable/Event/EntitySpawnerCheckEvent";
		min.name = "fire";
		min.desc = "(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/tileentity/MobSpawnerBaseLogic;)Z";
		min.setOpcode(Opcodes.INVOKESTATIC);
		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
	}
}

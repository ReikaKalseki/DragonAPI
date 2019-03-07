/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public abstract class RaytracePatcher extends Patcher {

	public RaytracePatcher(String deobf, String obf) {
		super(deobf, obf);
	}

	@Override
	protected final void apply(ClassNode cn) {
		/*
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70071_h_", "onUpdate", "()V");

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/EntityAboutToRayTraceEvent", "fire", "(Lnet/minecraft/entity/Entity;)V", false));

		String func1 = FMLForgePlugin.RUNTIME_DEOBF ? "func_149668_a" : "getCollisionBoundingBoxFromPool";
		String func2 = FMLForgePlugin.RUNTIME_DEOBF ? "func_72933_a" : "rayTraceBlocks";
		String func3 = FMLForgePlugin.RUNTIME_DEOBF ? "func_147447_a" : "func_147447_a";

		String world = FMLForgePlugin.RUNTIME_DEOBF ? "field_70170_p" : "worldObj";

		AbstractInsnNode min1 = null;
		AbstractInsnNode min2 = null;
		AbstractInsnNode min3 = null;

		try {
			min1 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/block/Block", func1, "(Lnet/minecraft/world/World;III)Lnet/minecraft/util/AxisAlignedBB;");
		}
		catch (NoSuchASMMethodInstructionException e) {

		}
		try {
			min2 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/world/World", func2, "(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;");
		}
		catch (NoSuchASMMethodInstructionException e) {

		}
		try {
			min3 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/world/World", func3, "(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;ZZZ)Lnet/minecraft/util/MovingObjectPosition;");
		}
		catch (NoSuchASMMethodInstructionException e) {

		}

		AbstractInsnNode pre1 = min1 != null ? ReikaASMHelper.getLastNonZeroALOADBefore(m.instructions, m.instructions.indexOf(min1)) : null;
		AbstractInsnNode pre2 = min2 != null ? ReikaASMHelper.getLastFieldRefBefore(m.instructions, m.instructions.indexOf(min2), world).getPrevious() : null;
		AbstractInsnNode pre3 = min3 != null ? ReikaASMHelper.getLastFieldRefBefore(m.instructions, m.instructions.indexOf(min3), world).getPrevious() : null;

		if (min1 == null && min2 == null && min3 == null) {
			ReikaASMHelper.log("WARNING: Found no raytrace hooks?!");
			ReikaASMHelper.log(ReikaASMHelper.clearString(m.instructions));
		}
		if (min1 != null && pre1 == null) {
			ReikaASMHelper.log("WARNING: Have injectable raytrace code but no landmark 1!");
		}
		if (min2 != null && pre2 == null) {
			ReikaASMHelper.log("WARNING: Have injectable raytrace code but no landmark 2!");
		}
		if (min3 != null && pre3 == null) {
			ReikaASMHelper.log("WARNING: Have injectable raytrace code but no landmark 3!");
		}

		if (pre1 != null) {
			m.instructions.insertBefore(pre1, ReikaASMHelper.copyInsnList(li));
		}
		if (pre2 != null) {
			m.instructions.insertBefore(pre2, ReikaASMHelper.copyInsnList(li));
		}
		if (pre3 != null) {
			m.instructions.insertBefore(pre3, ReikaASMHelper.copyInsnList(li));
		}

		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));*/


		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70071_h_", "onUpdate", "()V");

		String func1 = FMLForgePlugin.RUNTIME_DEOBF ? "func_149668_a" : "getCollisionBoundingBoxFromPool";
		String func2 = FMLForgePlugin.RUNTIME_DEOBF ? "func_72933_a" : "rayTraceBlocks";
		String func3 = FMLForgePlugin.RUNTIME_DEOBF ? "func_147447_a" : "func_147447_a";

		String world = FMLForgePlugin.RUNTIME_DEOBF ? "field_70170_p" : "worldObj";

		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.name.equals(func1)) {
					VarInsnNode pre = (VarInsnNode)ReikaASMHelper.getLastNonZeroALOADBefore(m.instructions, i);
					//m.instructions.remove(pre);
					pre.var = 0;
					min.owner = "Reika/DragonAPI/Instantiable/Event/EntityCollisionEvents";
					ReikaASMHelper.addLeadingArgument(min, "Lnet/minecraft/entity/Entity;");
					min.name = "getInterceptedCollisionBox";
					ReikaASMHelper.changeOpcode(min, Opcodes.INVOKESTATIC);
				}
				else if (min.name.equals(func2)) {
					AbstractInsnNode pre = ReikaASMHelper.getLastFieldRefBefore(m.instructions, i, world);
					m.instructions.remove(pre);
					min.owner = "Reika/DragonAPI/Instantiable/Event/EntityCollisionEvents";
					ReikaASMHelper.addLeadingArgument(min, "Lnet/minecraft/entity/Entity;");
					min.name = "getInterceptedRaytrace";
					ReikaASMHelper.changeOpcode(min, Opcodes.INVOKESTATIC);
				}
				else if (min.name.equals(func3)) {
					AbstractInsnNode pre = ReikaASMHelper.getLastFieldRefBefore(m.instructions, i, world);
					m.instructions.remove(pre);
					min.owner = "Reika/DragonAPI/Instantiable/Event/EntityCollisionEvents";
					ReikaASMHelper.addLeadingArgument(min, "Lnet/minecraft/entity/Entity;");
					min.name = "getInterceptedRaytrace";
					ReikaASMHelper.changeOpcode(min, Opcodes.INVOKESTATIC);
				}
			}
		}
	}

}

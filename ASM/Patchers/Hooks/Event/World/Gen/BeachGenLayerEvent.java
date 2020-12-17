/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World.Gen;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class BeachGenLayerEvent extends Patcher {

	public BeachGenLayerEvent() {
		super("net.minecraft.world.gen.layer.GenLayer", "axb");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_75901_a", "initializeAllBiomeGenerators", "(JLnet/minecraft/world/WorldType;)[Lnet/minecraft/world/gen/layer/GenLayer;");
		for (int i = m.instructions.size()-1; i >= 0; i--) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.NEW) {
				TypeInsnNode tin = (TypeInsnNode)ain;
				tin.desc = this.redirectOwnerAsNecessary(tin.desc);
			}
			else if (ain instanceof MethodInsnNode) {
				MethodInsnNode min = (MethodInsnNode)ain;
				min.owner = this.redirectOwnerAsNecessary(min.owner);
			}
			else if (ain instanceof FieldInsnNode) {
				FieldInsnNode fin = (FieldInsnNode)ain;
				fin.owner = this.redirectOwnerAsNecessary(fin.owner);
			}
		}
	}

	private String redirectOwnerAsNecessary(String owner) {
		if ("net/minecraft/world/gen/layer/GenLayerShore".equals(owner))
			owner = "Reika/DragonAPI/Extras/GenLayerControllableShore";
		return owner;
	}

	/*
	@Override
	public boolean computeFrames() {
		return true;
	}
	 */
}

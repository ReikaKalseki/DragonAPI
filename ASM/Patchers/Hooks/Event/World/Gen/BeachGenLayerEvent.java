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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class BeachGenLayerEvent extends Patcher {

	public BeachGenLayerEvent() {
		super("net.minecraft.world.gen.layer.GenLayerShore", "axw");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_75904_a", "getInts", "(IIII)[I");
		this.applyToMethod(cn, m);
		m = ReikaASMHelper.getMethodByName(cn, "func_151632_a", "func_151632_a", "([I[IIIIII)V");
		this.applyToMethod(cn, m);
	}

	private void applyToMethod(ClassNode cn, MethodNode m) {
		for (int i = m.instructions.size()-1; i >= 0; i--) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.IASTORE) {
				this.addHook(cn, m, ain, i);
			}
		}
		//ReikaASMHelper.log(ReikaASMHelper.clearString(m.instructions));
	}

	private int addHook(ClassNode cn, MethodNode m, AbstractInsnNode ain, int idx) {
		AbstractInsnNode ref = ain.getPrevious(); //loading the beach ID
		if (ref.getOpcode() == Opcodes.GETFIELD || ref.getOpcode() == Opcodes.INVOKEVIRTUAL)
			ref = ref.getPrevious();
		InsnList li = new InsnList();
		li.add(new InsnNode(Opcodes.SWAP));
		li.add(new InsnNode(Opcodes.DUP));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GenLayerBeachEvent", "setIntCache", "([I)V", false));
		li.add(new InsnNode(Opcodes.SWAP));
		li.add(new InsnNode(Opcodes.DUP));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GenLayerBeachEvent", "setIntIndex", "(I)V", false));
		m.instructions.insertBefore(ref, li);
		m.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GenLayerBeachEvent", "fire", "(I)I", false));
		return idx+li.size()+2;
	}

	/*
	@Override
	public boolean computeFrames() {
		return true;
	}
	 */
}

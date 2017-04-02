/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World.Gen;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class BeachGenLayerEvent extends Patcher {

	public BeachGenLayerEvent() {
		super("net.minecraft.world.gen.layer.GenLayerShore", "axw");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_75904_a", "getInts", "(IIII)[I");

		//ReikaASMHelper.writeClassFile(cn, "C:/GenLayerShore_preASM");
		//ReikaASMHelper.log(ReikaASMHelper.clearString(m.instructions));

		int version = ReikaASMHelper.forgeVersion_Build;

		if (version == 1614) {
			this.apply_1614(m);
		}
		else if (version == 1558) {
			this.apply_1558(m);
		}
		else {
			throw new InstallationException("DragonAPI", "Unsupported Forge version! Use either 1558 or 1614!");
		}
	}

	private void apply_1558(MethodNode m) {
		String s = FMLForgePlugin.RUNTIME_DEOBF ? "field_150574_L" : "jungleEdge";
		AbstractInsnNode ref = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.GETSTATIC, "net/minecraft/world/biome/BiomeGenBase", s, "Lnet/minecraft/world/biome/BiomeGenBase;");
		int idx = m.instructions.indexOf(ref);
		AbstractInsnNode first = ReikaASMHelper.getFirstOpcodeAfter(m.instructions, idx, Opcodes.ILOAD);
		JumpInsnNode last = (JumpInsnNode)ReikaASMHelper.getNthOpcodeAfter(m.instructions, 3, idx, Opcodes.IF_ICMPEQ);

		ReikaASMHelper.deleteFrom(m.instructions, first.getNext(), last.getPrevious());
		InsnList li = new InsnList();
		//li.add(new VarInsnNode(Opcodes.ILOAD, 9));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GenLayerBeachEvent", "fire", "(I)Z", false));
		//li.add(new JumpInsnNode(Opcodes.IFEQ, ));

		last.setOpcode(Opcodes.IFEQ);
		m.instructions.insert(first, li);

		first = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.GETSTATIC);
		ref = first.getPrevious();
		m.instructions.remove(first.getNext());
		m.instructions.remove(first);

		li = new InsnList();

		li.add(new VarInsnNode(Opcodes.ILOAD, 9));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GenLayerBeachEvent$BeachTypeEvent", "fire", "(I)I", false));

		m.instructions.insert(ref, li);
	}

	private void apply_1614(MethodNode m) {
		AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.GETFIELD);
		AbstractInsnNode ref = ain.getNext();
		m.instructions.remove(ain.getPrevious());
		m.instructions.remove(ain);

		InsnList li = new InsnList();

		li.add(new VarInsnNode(Opcodes.ILOAD, 9));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GenLayerBeachEvent$BeachTypeEvent", "fire", "(I)I", false));

		m.instructions.insertBefore(ref, li);
	}

	/*
	@Override
	public boolean computeFrames() {
		return true;
	}
	 */
}

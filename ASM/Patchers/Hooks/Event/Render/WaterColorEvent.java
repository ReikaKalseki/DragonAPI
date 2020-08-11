/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.base.Throwables;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

import cpw.mods.fml.relauncher.Side;


public class WaterColorEvent extends Patcher {

	public WaterColorEvent() {
		super("net.minecraft.block.BlockLiquid", "alw");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = null;
		NoSuchASMMethodException e = null;
		try {
			m = ReikaASMHelper.getMethodByName(cn, "func_149720_d", "colorMultiplier", "(Lnet/minecraft/world/IBlockAccess;III)I");
		}
		catch (NoSuchASMMethodException ex) {
			e = ex;
			try {
				m = ReikaASMHelper.getMethodByName(cn, "colorMultiplierOld", "(Lnet/minecraft/world/IBlockAccess;III)I");
			}
			catch (NoSuchASMMethodException ex2) {

			}
		}
		if (m == null)
			Throwables.propagate(e);

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/WaterColorEvent", "fire", "(Lnet/minecraft/world/IBlockAccess;III)I", false));

		AbstractInsnNode first = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.ICONST_0);
		AbstractInsnNode ref = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.IRETURN);
		AbstractInsnNode last = ref.getPrevious();

		ReikaASMHelper.deleteFrom(cn, m.instructions, first, last);
		m.instructions.insertBefore(ref, li);
	}

	@Override
	public boolean runsOnSide(Side s) {
		return s == Side.CLIENT;
	}
}

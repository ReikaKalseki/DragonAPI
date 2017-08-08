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

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import cpw.mods.fml.relauncher.Side;


public class FluidIcons extends Patcher {

	public FluidIcons() {
		super("net.minecraftforge.fluids.RenderBlockFluid");
	}

	@Override
	protected void apply(ClassNode cn) {
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_149691_a" : "getIcon";
		String name2 = FMLForgePlugin.RUNTIME_DEOBF ? "func_149673_e" : "getIcon";
		InsnList args = new InsnList();
		args.add(new VarInsnNode(Opcodes.ALOAD, 1));
		args.add(new VarInsnNode(Opcodes.ILOAD, 2));
		args.add(new VarInsnNode(Opcodes.ILOAD, 3));
		args.add(new VarInsnNode(Opcodes.ILOAD, 4));
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "renderWorldBlock", "(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraft/block/Block;ILnet/minecraft/client/renderer/RenderBlocks;)Z");
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.owner.equals("net/minecraft/block/Block") && min.name.equals(name) && min.desc.equals("(II)Lnet/minecraft/util/IIcon;")) {
					min.desc = ("(Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;");
					min.name = name2;
					m.instructions.remove(min.getPrevious()); //metadata
					AbstractInsnNode side = min.getPrevious();
					if (side.getOpcode() == Opcodes.IADD)
						side = side.getPrevious().getPrevious();
					m.instructions.insertBefore(side, ReikaASMHelper.copyInsnList(args));
				}
			}
		}
	}

	@Override
	public boolean runsOnSide(Side s) {
		return s == Side.CLIENT;
	}

}

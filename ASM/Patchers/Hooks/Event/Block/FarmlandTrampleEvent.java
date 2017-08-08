/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Block;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class FarmlandTrampleEvent extends Patcher {

	public FarmlandTrampleEvent() {
		super("net.minecraft.block.BlockFarmland", "aky");
	}

	@Override
	protected void apply(ClassNode cn) {
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_149746_a" : "onFallenUpon";
		String sig = "(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;F)V";
		MethodNode m = ReikaASMHelper.getMethodByName(cn, name, sig);
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
		m.instructions.add(new VarInsnNode(Opcodes.FLOAD, 6));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/FarmlandTrampleEvent", "fire", sig, false));
		m.instructions.add(new InsnNode(Opcodes.RETURN));
	}

}

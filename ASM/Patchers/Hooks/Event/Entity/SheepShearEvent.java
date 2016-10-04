/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class SheepShearEvent extends Patcher {

	public SheepShearEvent() {
		super("net.minecraft.entity.passive.EntitySheep", "wp");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "onSheared", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/IBlockAccess;IIII)Ljava/util/ArrayList;");
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/SheepShearEvent", "fire", "(Lnet/minecraft/entity/passive/EntitySheep;Lnet/minecraft/item/ItemStack;I)Ljava/util/ArrayList;", false));
		m.instructions.add(new InsnNode(Opcodes.ARETURN));
	}
}

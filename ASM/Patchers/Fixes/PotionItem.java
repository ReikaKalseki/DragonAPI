/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class PotionItem extends Patcher {

	public PotionItem() {
		super("net.minecraft.entity.projectile.EntityPotion", "zo");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70184_a", "onImpact", "(Lnet/minecraft/util/MovingObjectPosition;)V");
		AbstractInsnNode start = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.GETSTATIC);
		AbstractInsnNode end = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.ASTORE);
		AbstractInsnNode pre = start.getPrevious();
		String item = FMLForgePlugin.RUNTIME_DEOBF ? "field_151068_bn" : "potionitem";
		String stack = FMLForgePlugin.RUNTIME_DEOBF ? "field_70197_d" : "potionDamage";
		String getItem = FMLForgePlugin.RUNTIME_DEOBF ? "func_77973_b" : "getItem";
		String getFX = FMLForgePlugin.RUNTIME_DEOBF ? "func_77832_l" : "getEffects";

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/projectile/EntityPotion", stack, "Lnet/minecraft/item/ItemStack;"));
		li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", getItem, "()Lnet/minecraft/item/Item;", false));
		li.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/item/ItemPotion"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/projectile/EntityPotion", stack, "Lnet/minecraft/item/ItemStack;"));
		li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemPotion", getFX, "(Lnet/minecraft/item/ItemStack;)Ljava/util/List;", false));
		li.add(new VarInsnNode(Opcodes.ASTORE, 2));

		ReikaASMHelper.deleteFrom(m.instructions, start, end);

		m.instructions.insert(pre, li);
	}
}

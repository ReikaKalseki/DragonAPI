/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class MetadataSpecificTrades extends Patcher {

	public MetadataSpecificTrades() {
		super("net.minecraft.village.MerchantRecipeList", "ago");
	}

	@Override
	protected void apply(ClassNode cn) {
		String sig = "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/village/MerchantRecipe;";
		String sig2 = ReikaASMHelper.addLeadingArgument(sig, ReikaASMHelper.convertClassName(cn, true));
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_77203_a", "canRecipeBeUsed", sig);
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/ASMCalls", "getMatchingTrade", sig2, false));
		m.instructions.add(new InsnNode(Opcodes.ARETURN));
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class PlayerSpecificTrades extends Patcher {

	public PlayerSpecificTrades() {
		super("net.minecraft.entity.passive.EntityVillager", "yv");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70934_b", "getRecipes", "(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/village/MerchantRecipeList;");
		AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.ARETURN);
		m.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Interfaces/PlayerSpecificTrade$MerchantRecipeHooks", "filterRecipeList", "(Lnet/minecraft/village/MerchantRecipeList;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/village/MerchantRecipeList;", false));
	}

}

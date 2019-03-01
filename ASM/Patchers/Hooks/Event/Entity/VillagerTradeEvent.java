/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class VillagerTradeEvent extends Patcher {

	public VillagerTradeEvent() {
		super("net.minecraft.inventory.SlotMerchantResult", "aau");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82870_a", "onPickupFromSlot", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V");

		AbstractInsnNode ain = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/entity/IMerchant", FMLForgePlugin.RUNTIME_DEOBF ? "func_70933_a" : "useRecipe", "(Lnet/minecraft/village/MerchantRecipe;)V");
		ain = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(ain), Opcodes.ALOAD, 0);

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/inventory/SlotMerchantResult", FMLForgePlugin.RUNTIME_DEOBF ? "field_75234_h" : "theMerchant", "Lnet/minecraft/entity/IMerchant;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ALOAD, 3));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/VillagerTradeEvent", "fire", "(Lnet/minecraft/entity/IMerchant;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/village/MerchantRecipe;)V", false));

		m.instructions.insertBefore(ain, li);
	}

}

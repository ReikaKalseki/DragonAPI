/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity.Player;

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

public class PostItemUseEvent2 extends Patcher {

	public PostItemUseEvent2() {
		super("net.minecraftforge.common.ForgeHooks");
	}

	@Override
	protected void apply(ClassNode cn) {
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_71064_a" : "addStat";
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "onPlaceItemIntoWorld", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z");
		AbstractInsnNode ain = ReikaASMHelper.getLastInsn(m.instructions, Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", func, "(Lnet/minecraft/stats/StatBase;I)V", false);
		if (ain == null)
			throw new NullPointerException("addStat() Instruction not found!");
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ALOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new VarInsnNode(Opcodes.ILOAD, 5));
		li.add(new VarInsnNode(Opcodes.ILOAD, 6));
		li.add(new VarInsnNode(Opcodes.FLOAD, 7));
		li.add(new VarInsnNode(Opcodes.FLOAD, 8));
		li.add(new VarInsnNode(Opcodes.FLOAD, 9));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PostItemUseEvent", "fire", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)V", false));
		m.instructions.insert(ain, li);
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}
}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Item;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class HarvestLevelEvent extends Patcher {

	public HarvestLevelEvent() {
		super("net.minecraftforge.common.ForgeHooks");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "canHarvestBlock", "(Lnet/minecraft/block/Block;Lnet/minecraft/entity/player/EntityPlayer;I)Z");
		AbstractInsnNode ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.ISTORE, 5);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ALOAD, 4));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/HarvestLevelEvent", "fire", "(ILnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)I", false));
		m.instructions.insertBefore(ain, li);
	}

}

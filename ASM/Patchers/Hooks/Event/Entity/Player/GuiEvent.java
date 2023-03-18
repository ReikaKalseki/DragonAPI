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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class GuiEvent extends Patcher {

	public GuiEvent() {
		super("net.minecraft.entity.player.EntityPlayer", "yz");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "openGui", "(Ljava/lang/Object;ILnet/minecraft/world/World;III)V");
		MethodInsnNode min = (MethodInsnNode)ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.INVOKESTATIC);
		min.owner = "Reika/DragonAPI/Instantiable/Event/PlayerOpenGuiEvent";
		min.name = "fire";
	}
}

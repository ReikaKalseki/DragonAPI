/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class KeepInvEvent2 extends Patcher {

	public KeepInvEvent2() {
		super("net.minecraft.entity.player.EntityPlayerMP", "mw");
	}

	@Override
	protected void apply(ClassNode cn) {
		InsnList li = new InsnList();
		//li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PlayerKeepInventoryEvent", "fire", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));

		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70645_a", "onDeath", "(Lnet/minecraft/util/DamageSource;)V");
		if (ReikaASMHelper.checkForClass("api.player.forge.PlayerAPITransformer")) {
			m = ReikaASMHelper.getMethodByName(cn, "localOnDeath", "(Lnet/minecraft/util/DamageSource;)V"); //Try his method instead
		}

		AbstractInsnNode ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.LDC, "keepInventory");
		if (ain == null)
			ReikaASMHelper.throwConflict(this.toString(), cn, m, "Could not find 'keepInventory' gamerule lookup");
		AbstractInsnNode load = ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(ain), Opcodes.ALOAD);
		ReikaASMHelper.deleteFrom(m.instructions, load.getNext(), ain.getNext());
		m.instructions.insert(load, ReikaASMHelper.copyInsnList(li));
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}
}

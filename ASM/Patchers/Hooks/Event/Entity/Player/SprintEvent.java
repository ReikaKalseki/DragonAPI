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

public class SprintEvent extends Patcher {

	public SprintEvent() {
		super("net.minecraft.network.NetHandlerPlayServer", "nh");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147357_a", "processEntityAction", "(Lnet/minecraft/network/play/client/C0BPacketEntityAction;)V");
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_70031_b" : "setSprinting";
		AbstractInsnNode call = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/entity/player/EntityPlayerMP", func, "(Z)V");
		InsnList evt = new InsnList();
		evt.add(new VarInsnNode(Opcodes.ALOAD, 0));
		String field = FMLForgePlugin.RUNTIME_DEOBF ? "field_147369_b" : "playerEntity";
		evt.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/NetHandlerPlayServer", field, "Lnet/minecraft/entity/player/EntityPlayerMP;"));
		evt.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PlayerSprintEvent", "fire", "(Lnet/minecraft/entity/player/EntityPlayer;)V", false));
		m.instructions.insert(call, evt);
	}
}

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
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class GuiEvent extends Patcher {

	public GuiEvent() {
		super("net.minecraft.entity.player.EntityPlayer", "yz");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "openGui", "(Ljava/lang/Object;ILnet/minecraft/world/World;III)V");
		InsnList pre = new InsnList();
		LabelNode L1 = new LabelNode();
		LabelNode L2 = new LabelNode();
		pre.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		pre.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/PlayerOpenGuiEvent"));
		pre.add(new InsnNode(Opcodes.DUP));
		pre.add(new VarInsnNode(Opcodes.ALOAD, 0));
		pre.add(new VarInsnNode(Opcodes.ALOAD, 1));
		pre.add(new VarInsnNode(Opcodes.ALOAD, 3));
		pre.add(new VarInsnNode(Opcodes.ILOAD, 4));
		pre.add(new VarInsnNode(Opcodes.ILOAD, 5));
		pre.add(new VarInsnNode(Opcodes.ILOAD, 6));
		pre.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/PlayerOpenGuiEvent", "<init>", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;Lnet/minecraft/world/World;III)V", false));
		pre.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		pre.add(new JumpInsnNode(Opcodes.IFNE, L1));
		pre.add(L2);
		m.instructions.insert(pre);
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.RETURN) {
				m.instructions.insertBefore(ain, L1);
				break;
			}
		}
	}
}

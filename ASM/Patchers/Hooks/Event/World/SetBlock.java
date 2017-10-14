/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class SetBlock extends Patcher {

	public SetBlock() {
		super("net.minecraft.world.chunk.Chunk", "apx");
	}

	@Override
	protected void apply(ClassNode cn) {

		// Look for IRETURN immediately after an ICONST_1; this is a
		// "return true"
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_150807_a", "func_150807_a", "(IIILnet/minecraft/block/Block;I)Z");
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.IRETURN) {
				if (ain.getPrevious().getOpcode() == Opcodes.ICONST_1) {
					AbstractInsnNode loc = ain.getPrevious();
					m.instructions.insertBefore(loc, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					m.instructions.insertBefore(loc, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/SetBlockEvent$Post"));
					m.instructions.insertBefore(loc, new InsnNode(Opcodes.DUP));
					m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 1));
					m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 2));
					m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 3));
					m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/SetBlockEvent$Post", "<init>", "(Lnet/minecraft/world/chunk/Chunk;III)V", false));
					m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					m.instructions.insertBefore(loc, new InsnNode(Opcodes.POP));
					break;
				}
			}
		}

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ALOAD, 4));
		li.add(new VarInsnNode(Opcodes.ILOAD, 5));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/SetBlockEvent$Pre", "fire", "(Lnet/minecraft/world/chunk/Chunk;IIILnet/minecraft/block/Block;I)V", false));

		m.instructions.insert(li);

		m = ReikaASMHelper.getMethodByName(cn, "func_76589_b", "setBlockMetadata", "(IIII)Z");
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.IRETURN) {
				if (ain.getPrevious().getOpcode() == Opcodes.ICONST_1) {
					AbstractInsnNode loc = ain.getPrevious();
					m.instructions.insertBefore(loc, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					m.instructions.insertBefore(loc, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/SetBlockEvent$Post"));
					m.instructions.insertBefore(loc, new InsnNode(Opcodes.DUP));
					m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 1));
					m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 2));
					m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 3));
					m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/SetBlockEvent$Post", "<init>", "(Lnet/minecraft/world/chunk/Chunk;III)V", false));
					m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					m.instructions.insertBefore(loc, new InsnNode(Opcodes.POP));
					break;
				}
			}
		}

		li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/SetBlockEvent$Pre", "fire_meta", "(Lnet/minecraft/world/chunk/Chunk;IIII)V", false));

		m.instructions.insert(li);
	}

}

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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class IceCancel extends Patcher {

	public IceCancel() {
		super("net.minecraft.world.World", "ahb");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72834_c", "canBlockFreeze", "(IIIZ)Z");
		/*LabelNode l4 = new LabelNode(new Label()); LabelNode l6 = new
		 * LabelNode(new Label()); m.instructions.clear();
		 * m.instructions.add(new TypeInsnNode(Opcodes.NEW,
		 * "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent"));
		 * m.instructions.add(new InsnNode(Opcodes.DUP)); m.instructions.add(new
		 * VarInsnNode(Opcodes.ALOAD, 0)); m.instructions.add(new
		 * VarInsnNode(Opcodes.ILOAD, 1)); m.instructions.add(new
		 * VarInsnNode(Opcodes.ILOAD, 2)); m.instructions.add(new
		 * VarInsnNode(Opcodes.ILOAD, 3)); m.instructions.add(new
		 * VarInsnNode(Opcodes.ILOAD, 4)); m.instructions.add(new
		 * MethodInsnNode(Opcodes.INVOKESPECIAL,
		 * "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent", "<init>",
		 * "(Lnet/minecraft/world/World;IIIZ)V", false)); m.instructions.add(new
		 * VarInsnNode(Opcodes.ASTORE, 5)); m.instructions.add(new
		 * FieldInsnNode(Opcodes.GETSTATIC,
		 * "net/minecraftforge/common/MinecraftForge", "EVENT_BUS",
		 * "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		 * m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
		 * m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
		 * "cpw/mods/fml/common/eventhandler/EventBus", "post",
		 * "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		 * m.instructions.add(new InsnNode(Opcodes.POP)); m.instructions.add(new
		 * VarInsnNode(Opcodes.ALOAD, 5)); m.instructions.add(new
		 * MethodInsnNode(Opcodes.INVOKEVIRTUAL,
		 * "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent", "getResult",
		 * "()Lcpw/mods/fml/common/eventhandler/Event$Result;", false));
		 * m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 6));
		 * m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
		 * m.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC,
		 * "cpw/mods/fml/common/eventhandler/Event$Result", "ALLOW",
		 * "Lcpw/mods/fml/common/eventhandler/Event$Result;"));
		 * m.instructions.add(new JumpInsnNode(Opcodes.IF_ACMPNE, l4));
		 * m.instructions.add(new InsnNode(Opcodes.ICONST_1));
		 * m.instructions.add(new InsnNode(Opcodes.IRETURN));
		 * m.instructions.add(l4); //FRAME APPEND
		 * [Reika/DragonAPI/Instantiable/Event/IceFreezeEvent
		 * cpw/mods/fml/common/eventhandler/Event$Result] m.instructions.add(new
		 * VarInsnNode(Opcodes.ALOAD, 6)); m.instructions.add(new
		 * FieldInsnNode(Opcodes.GETSTATIC,
		 * "cpw/mods/fml/common/eventhandler/Event$Result", "DENY",
		 * "Lcpw/mods/fml/common/eventhandler/Event$Result;"));
		 * m.instructions.add(new JumpInsnNode(Opcodes.IF_ACMPNE, l6));
		 * m.instructions.add(new InsnNode(Opcodes.ICONST_0));
		 * m.instructions.add(new InsnNode(Opcodes.IRETURN));
		 * m.instructions.add(l6); //FRAME SAME m.instructions.add(new
		 * VarInsnNode(Opcodes.ALOAD, 5)); m.instructions.add(new
		 * MethodInsnNode(Opcodes.INVOKEVIRTUAL,
		 * "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent",
		 * "wouldFreezeNaturally", "()Z", false)); m.instructions.add(new
		 * InsnNode(Opcodes.IRETURN)); */

		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent", "fire", "(Lnet/minecraft/world/World;IIIZ)Z", false));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));
	}

}

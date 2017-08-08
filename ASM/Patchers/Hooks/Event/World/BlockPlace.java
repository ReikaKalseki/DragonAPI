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

public class BlockPlace extends Patcher {

	public BlockPlace() {
		super("net.minecraft.item.ItemBlock", "abh");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "placeBlockAt", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFFI)Z"); // Forge
																																																// func,
																																																// so
																																																// no
																																																// srg
		/*for (int i = 0; i < m.instructions.size(); i++) { AbstractInsnNode ain
		 * = m.instructions.get(i); if (ain.getOpcode() ==
		 * Opcodes.INVOKEVIRTUAL) { MethodInsnNode min = (MethodInsnNode)ain;
		 * String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_149689_a" :
		 * "onBlockPlacedBy"; if (min.name.equals(func)) {
		 * m.instructions.insert(min, new InsnNode(Opcodes.POP));
		 * m.instructions.insert(min, new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
		 * "cpw/mods/fml/common/eventhandler/EventBus", "post",
		 * "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		 * m.instructions.insert(min, new MethodInsnNode(Opcodes.INVOKESPECIAL,
		 * "Reika/DragonAPI/Instantiable/Event/PlayerPlaceBlockEvent", "<init>",
		 * "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;ILnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;)V"
		 * , false)); m.instructions.insert(min, new VarInsnNode(Opcodes.ALOAD,
		 * 2)); m.instructions.insert(min, new VarInsnNode(Opcodes.ALOAD, 1));
		 * m.instructions.insert(min, new VarInsnNode(Opcodes.ILOAD, 11));
		 * m.instructions.insert(min, new FieldInsnNode(Opcodes.GETFIELD,
		 * "net/minecraft/item/ItemBlock", "field_150939_a",
		 * "Lnet/minecraft/block/Block;")); m.instructions.insert(min, new
		 * VarInsnNode(Opcodes.ALOAD, 0)); m.instructions.insert(min, new
		 * VarInsnNode(Opcodes.ILOAD, 6)); m.instructions.insert(min, new
		 * VarInsnNode(Opcodes.ILOAD, 5)); m.instructions.insert(min, new
		 * VarInsnNode(Opcodes.ILOAD, 4)); m.instructions.insert(min, new
		 * VarInsnNode(Opcodes.ALOAD, 3)); m.instructions.insert(min, new
		 * InsnNode(Opcodes.DUP)); m.instructions.insert(min, new
		 * TypeInsnNode(Opcodes.NEW,
		 * "Reika/DragonAPI/Instantiable/Event/PlayerPlaceBlockEvent"));
		 * m.instructions.insert(min, new FieldInsnNode(Opcodes.GETSTATIC,
		 * "net/minecraftforge/common/MinecraftForge", "EVENT_BUS",
		 * "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		 * ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
		 * break; } } } */

		InsnList pre = new InsnList();
		LabelNode L1 = new LabelNode();
		LabelNode L2 = new LabelNode();
		pre.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		pre.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/PlayerPlaceBlockEvent"));
		pre.add(new InsnNode(Opcodes.DUP));
		pre.add(new VarInsnNode(Opcodes.ALOAD, 3));
		pre.add(new VarInsnNode(Opcodes.ILOAD, 4));
		pre.add(new VarInsnNode(Opcodes.ILOAD, 5));
		pre.add(new VarInsnNode(Opcodes.ILOAD, 6));
		pre.add(new VarInsnNode(Opcodes.ALOAD, 0));
		pre.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemBlock", "field_150939_a", "Lnet/minecraft/block/Block;"));
		pre.add(new VarInsnNode(Opcodes.ILOAD, 11));
		pre.add(new VarInsnNode(Opcodes.ALOAD, 1));
		pre.add(new VarInsnNode(Opcodes.ALOAD, 2));
		pre.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/PlayerPlaceBlockEvent", "<init>", "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;ILnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;)V", false));
		pre.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		pre.add(new JumpInsnNode(Opcodes.IFEQ, L1));
		pre.add(L2);
		pre.add(new InsnNode(Opcodes.ICONST_0));
		pre.add(new InsnNode(Opcodes.IRETURN));
		pre.add(L1);
		m.instructions.insert(pre);
	}

}

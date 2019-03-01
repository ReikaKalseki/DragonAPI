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
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class SlotClickEvent extends Patcher {

	public SlotClickEvent() {
		super("net.minecraft.inventory.Slot", "aay");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82870_a", "onPickupFromSlot", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V");
		AbstractInsnNode pos = m.instructions.getFirst();
		m.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		m.instructions.insertBefore(pos, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/SlotEvent$RemoveFromSlotEvent"));
		m.instructions.insertBefore(pos, new InsnNode(Opcodes.DUP));
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/inventory/Slot", "getSlotIndex", "()I", false)); // do
		// not
		// obfuscate,
		// is
		// Forge
		// func
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/inventory/Slot", FMLForgePlugin.RUNTIME_DEOBF ? "field_75224_c" : "inventory", "Lnet/minecraft/inventory/IInventory;"));
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 2));
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/SlotEvent$RemoveFromSlotEvent", "<init>", "(ILnet/minecraft/inventory/IInventory;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;)V", false));
		m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		m.instructions.insertBefore(pos, new InsnNode(Opcodes.POP));


		m = ReikaASMHelper.getMethodByName(cn, "func_75215_d", "putStack", "(Lnet/minecraft/item/ItemStack;)V");
		pos = m.instructions.getFirst();

		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/SlotEvent$AddToSlotEvent", "fire", "(Lnet/minecraft/inventory/Slot;Lnet/minecraft/item/ItemStack;)V", false));
	}

}

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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class ContainerSlotClickEvent extends Patcher {

	public ContainerSlotClickEvent() {
		super("net.minecraft.inventory.Container", "zs");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_75144_a", "slotClick", "(IIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ALOAD, 4));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/SlotEvent$InitialClickEvent", "fire", "(Lnet/minecraft/inventory/Container;IIILnet/minecraft/entity/player/EntityPlayer;)V", false));
		m.instructions.insert(li);
	}

	static class test extends Container {
		@Override
		public ItemStack slotClick(int p_75144_1_, int p_75144_2_, int p_75144_3_, EntityPlayer p_75144_4_)
		{
			ItemStack itemstack = null;
			InventoryPlayer inventoryplayer = p_75144_4_.inventory;
			int i1;
			ItemStack itemstack3;

			if (p_75144_3_ == 5)
			{
				int l = field_94536_g;
				field_94536_g = func_94532_c(p_75144_2_);

				if ((l != 1 || field_94536_g != 2) && l != field_94536_g)
				{
					this.func_94533_d();
				}
			}

		}

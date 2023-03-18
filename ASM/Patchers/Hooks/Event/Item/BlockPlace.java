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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class BlockPlace extends Patcher {

	public BlockPlace() {
		super("net.minecraft.item.ItemBlock", "abh");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "placeBlockAt", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFFI)Z");

		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_147465_d" : "setBlock");
		min.owner = "Reika/DragonAPI/Instantiable/Event/PlayerPlaceBlockEvent";
		min.name = "fireTryPlace";
		min.desc = "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;IIILnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)Z";
		min.setOpcode(Opcodes.INVOKESTATIC);
		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 7));
		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 2));
		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 1));
	}

}

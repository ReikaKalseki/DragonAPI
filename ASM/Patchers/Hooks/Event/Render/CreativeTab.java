/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class CreativeTab extends Patcher {

	public CreativeTab() {
		super("net.minecraft.client.gui.inventory.GuiContainerCreative", "bfl");
	}

	@Override
	protected void apply(ClassNode cn) {
		InsnList add = new InsnList();
		add.add(new VarInsnNode(Opcodes.ALOAD, 4));
		boolean obf = FMLForgePlugin.RUNTIME_DEOBF;
		add.add(new VarInsnNode(Opcodes.ALOAD, 0));
		String fd1 = obf ? "field_147062_A" : "searchField";
		add.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainerCreative", fd1, "Lnet/minecraft/client/gui/GuiTextField;"));
		String fd2 = obf ? "tabPage" : "tabPage"; //Forge?
		add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/gui/inventory/GuiContainerCreative", fd2, "I"));

		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146976_a", "drawGuiContainerBackgroundLayer", "(FII)V");
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_73729_b" : "drawTexturedModalRect";
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, func);
		min.owner = "Reika/DragonAPI/Instantiable/Event/Client/CreativeTabGuiRenderEvent";
		min.setOpcode(Opcodes.INVOKESTATIC);
		min.name = "fireFromTextureDraw";
		min.itf = false;
		min.desc = "(Lnet/minecraft/client/gui/inventory/GuiContainerCreative;IIIIIILnet/minecraft/creativetab/CreativeTabs;Lnet/minecraft/client/gui/GuiTextField;I)V";
		m.instructions.insertBefore(min, add);

	}
}

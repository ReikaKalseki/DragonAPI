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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class TileRender extends Patcher {

	public TileRender() {
		super("net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher", "bmk");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147549_a", "renderTileEntityAt", "(Lnet/minecraft/tileentity/TileEntity;DDDF)V");
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_147500_a" : "renderTileEntityAt";
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, func);
		min.owner = "Reika/DragonAPI/Instantiable/Event/Client/TileEntityRenderEvent";
		min.name = "fire";
		min.desc = "(Lnet/minecraft/client/renderer/tileentity/TileEntitySpecialRenderer;Lnet/minecraft/tileentity/TileEntity;DDDF)V";
		min.setOpcode(Opcodes.INVOKESTATIC);
	}
}

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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Auxiliary.CoreModDetection;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class RenderBlockEvent extends Patcher {

	public RenderBlockEvent() {
		super("net.minecraft.client.renderer.WorldRenderer", "blo");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147892_a", "updateRenderer", "(Lnet/minecraft/entity/EntityLivingBase;)V");
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_147805_b" : "renderBlockByRenderType";
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/client/renderer/RenderBlocks", name, "(Lnet/minecraft/block/Block;III)Z");

		String evt = "Reika/DragonAPI/Instantiable/Event/Client/RenderBlockAtPosEvent";
		/*
		m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, evt, "fire", "(Lnet/minecraft/client/renderer/WorldRenderer;Lnet/minecraft/client/renderer/RenderBlocks;Lnet/minecraft/block/Block;IIII)Z", false));
		m.instructions.insert(ain, new VarInsnNode(Opcodes.ILOAD, 17)); //renderpass "k2"
		AbstractInsnNode load = ain.getPrevious();
		while (load.getPrevious() instanceof VarInsnNode) {
			load = load.getPrevious();
		}
		m.instructions.insert(load, new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.remove(ain);
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
		 */
		VarInsnNode z = (VarInsnNode)ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(min), Opcodes.ILOAD);
		VarInsnNode y = (VarInsnNode)z.getPrevious();
		VarInsnNode x = (VarInsnNode)y.getPrevious();

		name = FMLForgePlugin.RUNTIME_DEOBF ? "func_149701_w" : "getRenderBlockPass";
		MethodInsnNode checkpass = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/block/Block", name, "()I");
		VarInsnNode store = (VarInsnNode)ReikaASMHelper.getFirstOpcodeAfter(m.instructions, m.instructions.indexOf(checkpass), Opcodes.ISTORE);
		int pass = -1;
		for (int i = m.instructions.indexOf(store); i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.ILOAD) {
				VarInsnNode vin = (VarInsnNode)ain;
				if (vin.var != store.var) {
					pass = vin.var;
					break;
				}
			}
		}

		min.desc = "(Lnet/minecraft/client/renderer/RenderBlocks;Lnet/minecraft/block/Block;IIILnet/minecraft/client/renderer/WorldRenderer;I)Z";
		min.name = "fire";
		min.owner = evt;
		min.setOpcode(Opcodes.INVOKESTATIC);
		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, pass)); //renderpass "k2"

		checkpass.desc = "(Lnet/minecraft/block/Block;III)I";
		checkpass.name = "getMaxRenderPass";
		checkpass.owner = evt;
		checkpass.setOpcode(Opcodes.INVOKESTATIC);
		m.instructions.insertBefore(checkpass, new VarInsnNode(Opcodes.ILOAD, x.var)); //x "j3"
		m.instructions.insertBefore(checkpass, new VarInsnNode(Opcodes.ILOAD, y.var)); //y "l2"
		m.instructions.insertBefore(checkpass, new VarInsnNode(Opcodes.ILOAD, z.var)); //z "i3"

		String call = "checkCanRenderPass";
		String sig = "(Lnet/minecraft/block/Block;IIII)Z";
		if (CoreModDetection.OPTIFINE.isInstalled()) {
			AbstractInsnNode ref = ReikaASMHelper.getFirstFieldCallByName(cn, m, "ForgeBlock_canRenderInPass");
			ref = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(ref), Opcodes.ALOAD, 25);
			AbstractInsnNode ref2 = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(ref), Opcodes.ISTORE, 28);
			ReikaASMHelper.deleteFrom(cn, m.instructions, ref.getNext(), ref2.getPrevious());
			m.instructions.insertBefore(ref2, new VarInsnNode(Opcodes.ILOAD, pass)); //pass
			m.instructions.insertBefore(ref2, new VarInsnNode(Opcodes.ILOAD, x.var)); //x "j3"
			m.instructions.insertBefore(ref2, new VarInsnNode(Opcodes.ILOAD, y.var)); //y "l2"
			m.instructions.insertBefore(ref2, new VarInsnNode(Opcodes.ILOAD, z.var)); //z "i3"
			m.instructions.insertBefore(ref2, new MethodInsnNode(Opcodes.INVOKESTATIC, evt, call, sig, false));

		}
		else {
			checkpass = ReikaASMHelper.getFirstMethodCallByName(cn, m, "canRenderInPass");
			checkpass.desc = sig;
			checkpass.name = call;
			checkpass.owner = evt;
			checkpass.setOpcode(Opcodes.INVOKESTATIC);
			m.instructions.insertBefore(checkpass, new VarInsnNode(Opcodes.ILOAD, x.var)); //x "j3"
			m.instructions.insertBefore(checkpass, new VarInsnNode(Opcodes.ILOAD, y.var)); //y "l2"
			m.instructions.insertBefore(checkpass, new VarInsnNode(Opcodes.ILOAD, z.var)); //z "i3"
		}
	}
}

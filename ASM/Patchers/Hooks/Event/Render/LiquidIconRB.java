package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

import cpw.mods.fml.relauncher.Side;

public class LiquidIconRB extends Patcher {

	public LiquidIconRB() {
		super("net.minecraft.client.renderer.RenderBlocks", "blm");
	}

	@Override
	protected void apply(ClassNode cn) {
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_147787_a" : "getBlockIconFromSideAndMetadata";
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147721_p", "renderBlockLiquid", "(Lnet/minecraft/block/Block;III)Z");

		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.name.equals(name)) {
					this.replaceIconCall(cn, m.instructions, i, min);
					ReikaASMHelper.log("Redirected fluid icon lookup @ "+i);
				}
			}
		}
	}

	private void replaceIconCall(ClassNode cn, InsnList li, int idx, MethodInsnNode min) { //on stack: block, s, meta
		/*
		InsnList repl = new InsnList();
		String call = FMLForgePlugin.RUNTIME_DEOBF ? "func_147793_a" : "getBlockIcon";
		String sig = "(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;";
		VarInsnNode vin = (VarInsnNode)li.get(idx-1); //meta
		li.remove(vin);
		vin = (VarInsnNode)li.get(idx-1); //side
		li.remove(vin);
		//repl.add(new VarInsnNode(Opcodes.ALOAD, 1)); //block
		repl.add(new VarInsnNode(Opcodes.ALOAD, 0));
		repl.add(new FieldInsnNode(Opcodes.GETFIELD, ReikaASMHelper.convertClassName(cn, false), FMLForgePlugin.RUNTIME_DEOBF ? "field_147845_a" : "blockAccess", "Lnet/minecraft/world/IBlockAccess;"));
		repl.add(new VarInsnNode(Opcodes.ILOAD, 2));
		repl.add(new VarInsnNode(Opcodes.ILOAD, 3));
		repl.add(new VarInsnNode(Opcodes.ILOAD, 4));
		repl.add(new VarInsnNode(Opcodes.ILOAD, vin.var));
		repl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ReikaASMHelper.convertClassName(cn, false), call, sig, false));
		ReikaASMHelper.replaceInstruction(li, min, repl);*/
		//li.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
		li.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 2));
		li.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 3));
		li.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 4));
		min.setOpcode(Opcodes.INVOKESTATIC);
		min.owner = "Reika/DragonAPI/ASM/ASMCallsClient";
		min.name = "getLiquidIconForRenderBlocks";
		min.desc = "(Lnet/minecraft/client/renderer/RenderBlocks;Lnet/minecraft/block/Block;IIIII)Lnet/minecraft/util/IIcon;";
		min.itf = false;
	}

	@Override
	public boolean runsOnSide(Side s) {
		return s == Side.CLIENT;
	}

	@Override
	public boolean computeFrames() {
		return false;
	}

}

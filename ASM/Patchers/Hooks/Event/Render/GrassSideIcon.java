package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;

import cpw.mods.fml.relauncher.Side;

public class GrassSideIcon extends Patcher {

	public GrassSideIcon() {
		super("net.minecraft.client.renderer.RenderBlocks", "blm");
	}

	@Override
	protected void apply(ClassNode cn) {
		String seek = FMLForgePlugin.RUNTIME_DEOBF ? "func_149990_e" : "getIconSideOverlay";
		String seeksig = "()Lnet/minecraft/util/IIcon;";
		for (MethodNode m : cn.methods) {
			for (int i = 0; i < m.instructions.size(); i++) {
				AbstractInsnNode ain = m.instructions.get(i);
				if (ain.getOpcode() == Opcodes.INVOKESTATIC) {
					MethodInsnNode min = (MethodInsnNode)ain;
					if (min.owner.equals("net/minecraft/block/BlockGrass") && min.name.equals(seek) && min.desc.equals(seeksig)) {
						this.redirect(cn, m.instructions, min);
					}
				}
			}
		}
	}

	private void redirect(ClassNode cn, InsnList li, MethodInsnNode min) {
		min.owner = "Reika/DragonAPI/Instantiable/Event/Client/GrassIconEvent";
		min.name = "fireSide";
		min.desc = "(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/util/IIcon;";
		li.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 1));
		li.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
		li.insertBefore(min, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/RenderBlocks", "blockAccess", "Lnet/minecraft/world/IBlockAccess;"));
		li.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 2));
		li.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 3));
		li.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 4));
	}

	@Override
	public boolean runsOnSide(Side s) {
		return s == Side.CLIENT;
	}

}

package Reika.DragonAPI.ASM.Patchers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

import cpw.mods.fml.relauncher.Side;

public abstract class BlockRenderBrightness extends Patcher {

	public BlockRenderBrightness(String deobf, String obf) {
		super(deobf, obf);
	}

	protected final void patchBlockLight(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72802_i", "getLightBrightnessForSkyBlocks", "(IIII)I");
		AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.IRETURN);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 5)); //sky
		li.add(new VarInsnNode(Opcodes.ILOAD, 6)); //block
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/LightVisualBrightnessEvent", "fireMixed", "(ILnet/minecraft/world/IBlockAccess;IIIII)I", false));
		m.instructions.insertBefore(ain, li);
	}

	@Override
	public final boolean runsOnSide(Side s) {
		return s == Side.CLIENT;
	}
}

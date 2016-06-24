package Reika.DragonAPI.ASM.Patchers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Auxiliary.CoreModDetection;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class SetBlockLight extends Patcher {

	public SetBlockLight() {
		super("net.minecraft.world.World", "ahb");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147465_d", "setBlock", "(IIILnet/minecraft/block/Block;II)Z");
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
		String cl = /*CoreModDetection.fastCraftInstalled() ? "fastcraft/J" : */cn.name;
		String func = /*CoreModDetection.fastCraftInstalled() ? "d" : */"func_147451_t";
		String sig = /*CoreModDetection.fastCraftInstalled() ? "(Lnet/minecraft/world/World;III)Z" : */"(III)Z";
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, cl, func, sig);
		min.owner = "Reika/DragonAPI/ASM/DragonAPIClassTransformer";
		min.name = "updateSetBlockLighting";
		min.desc = "(IIILnet/minecraft/world/World;I)Z";
		min.setOpcode(Opcodes.INVOKESTATIC);
		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 5));
	}

	@Override
	public boolean runWithCoreMod(CoreModDetection c) {
		return c != CoreModDetection.FASTCRAFT;
	}
}

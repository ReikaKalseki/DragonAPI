package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class GrassTopIcon extends Patcher {

	public GrassTopIcon() {
		super("net.minecraft.block.BlockGrass", "alh");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_149673_e", "getIcon", "(Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;");
		AbstractInsnNode ain = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.ARETURN).getPrevious();
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		//li.add(new VarInsnNode(Opcodes.ILOAD, 5));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/GrassIconEvent", "fire", "(Lnet/minecraft/util/IIcon;Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/util/IIcon;", false));
		//ReikaASMHelper.replaceInstruction(m.instructions, ain, li);
		m.instructions.insert(ain, li);
	}

}

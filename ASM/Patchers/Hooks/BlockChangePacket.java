package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class BlockChangePacket extends Patcher {

	public BlockChangePacket() {
		super("net.minecraft.client.multiplayer.WorldClient", "bjf");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147492_c", "func_147492_c", "(IIILnet/minecraft/block/Block;I)Z");
		m.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Extras/ChangePacketRenderer", "onBlockChangePacket", "(IIILnet/minecraft/block/Block;I)V", false));
		m.instructions.insert(new VarInsnNode(Opcodes.ILOAD, 5));
		m.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 4));
		m.instructions.insert(new VarInsnNode(Opcodes.ILOAD, 3));
		m.instructions.insert(new VarInsnNode(Opcodes.ILOAD, 2));
		m.instructions.insert(new VarInsnNode(Opcodes.ILOAD, 1));
	}

}

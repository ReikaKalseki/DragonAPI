package Reika.DragonAPI.ASM.Patchers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class StrongholdEyeRouting extends Patcher {

	public StrongholdEyeRouting() {
		super("net.minecraft.world.gen.structure.StructureStrongholdPieces$Stairs2", "auz");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_151553_a", "()Lnet/minecraft/world/ChunkPosition;");
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/ASMCalls", "getStrongholdSeekPos", "(L"+cn.name.replaceAll("\\\\.", "/")+";)Lnet/minecraft/world/ChunkPosition;", false));
		m.instructions.add(new InsnNode(Opcodes.ARETURN));
	}

}

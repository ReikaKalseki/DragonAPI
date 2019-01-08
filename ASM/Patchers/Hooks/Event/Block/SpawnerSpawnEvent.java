package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Block;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class SpawnerSpawnEvent extends Patcher {

	public SpawnerSpawnEvent() {
		super("net.minecraft.tileentity.MobSpawnerBaseLogic", "agq");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_98265_a", "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/entity/Entity;");
		AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.ARETURN);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/SpawnerGenerateEntityEvent", "fire", "(Lnet/minecraft/tileentity/MobSpawnerBaseLogic;Lnet/minecraft/entity/Entity;)V", false));
		m.instructions.insertBefore(ain, li);
	}

}

package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Block;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class SpawnerCheckPlayersEvent extends Patcher {

	public SpawnerCheckPlayersEvent() {
		super("net.minecraft.tileentity.MobSpawnerBaseLogic", "agq");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_98279_f", "isActivated", "()Z");
		m.instructions.clear();
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/SpawnerCheckPlayerEvent", "runCheck", "(Lnet/minecraft/tileentity/MobSpawnerBaseLogic;)Z", false));
		li.add(new InsnNode(Opcodes.IRETURN));
		m.instructions.add(li);
	}

}

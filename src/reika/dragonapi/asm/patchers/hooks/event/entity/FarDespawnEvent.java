package reika.dragonapi.asm.patchers.hooks.event.entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class FarDespawnEvent extends Patcher {

	public FarDespawnEvent() {
		super("net.minecraft.entity.EntityLiving", "sw");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70692_ba", "canDespawn", "()Z");
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/LivingFarDespawnEvent", "fire", "(Lnet/minecraft/entity/EntityLiving;)Z", false));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}
}

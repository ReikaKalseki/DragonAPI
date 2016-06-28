package reika.dragonapi.asm.patchers.hooks.event.entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;


public class AttackAggroEvent2 extends Patcher {

	public AttackAggroEvent2() {
		super("net.minecraft.entity.monster.EntityPigZombie", "yh");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70097_a", "attackEntityFrom", "(Lnet/minecraft/util/DamageSource;F)Z");
		AbstractInsnNode loc = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.INSTANCEOF);

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.FLOAD, 2));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/AttackAggroEvent", "fire", "(Lnet/minecraft/entity/monster/EntityMob;Lnet/minecraft/util/DamageSource;F)Z", false));

		//ReikaASMHelper.changeOpcode(loc.getNext(), Opcodes.IFNE);
		m.instructions.insertBefore(loc.getPrevious(), li);
		m.instructions.remove(loc.getPrevious());
		m.instructions.remove(loc);
	}
}

package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class PigZombieAggroSpreadEvent extends Patcher {

	public PigZombieAggroSpreadEvent() {
		super("net.minecraft.entity.monster.EntityPigZombie", "yh");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70097_a", "attackEntityFrom", "(Lnet/minecraft/util/DamageSource;F)Z");
		AbstractInsnNode loc = ReikaASMHelper.getNthOpcode(m.instructions, Opcodes.INSTANCEOF, 1); //would be 2, but since ASM remove the other, is 1

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, ((VarInsnNode)loc.getPrevious()).var));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.FLOAD, 2));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PigZombieAggroSpreadEvent", "fire", "(Lnet/minecraft/entity/monster/EntityPigZombie;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/DamageSource;F)Z", false));

		//ReikaASMHelper.changeOpcode(loc.getNext(), Opcodes.IFNE);
		m.instructions.insertBefore(loc.getPrevious(), li);
		m.instructions.remove(loc.getPrevious());
		m.instructions.remove(loc);
	}
}

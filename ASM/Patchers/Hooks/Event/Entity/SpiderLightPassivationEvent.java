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


public class SpiderLightPassivationEvent extends Patcher {

	public SpiderLightPassivationEvent() {
		super("net.minecraft.entity.monster.EntitySpider", "yn");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70782_k", "findPlayerToAttack", "()Lnet/minecraft/entity/Entity;");
		AbstractInsnNode ldc = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.LDC);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.FLOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/SpiderLightPassivationEvent", "fire", "(FLnet/minecraft/entity/monster/EntitySpider;F)F", false));
		m.instructions.insert(ldc, li);
	}

}

package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class LightLevelForSpawnEvent extends Patcher {

	public LightLevelForSpawnEvent() {
		super("net.minecraft.entity.monster.EntityMob", "yg");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70601_bi", "getCanSpawnHere", "()Z");
		AbstractInsnNode ain = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.INVOKEVIRTUAL);
		m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/LightLevelForSpawnEvent", "fire", "(ZLnet/minecraft/entity/monster/EntityMob;)Z", false));
		m.instructions.insert(ain, new VarInsnNode(Opcodes.ALOAD, 0));

		m = ReikaASMHelper.getMethodByName(cn, "func_70783_a", "getBlockPathWeight", "(III)F");
		ain = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.INVOKEVIRTUAL);
		m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/LightLevelForSpawnEvent", "firePathWeight", "(FLnet/minecraft/entity/monster/EntityMob;)F", false));
		m.instructions.insert(ain, new VarInsnNode(Opcodes.ALOAD, 0));
	}



}

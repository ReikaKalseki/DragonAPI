package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class KeepInvEvent extends Patcher {

	public KeepInvEvent() {
		super("net.minecraft.entity.player.EntityPlayer", "yz");
	}

	@Override
	protected void apply(ClassNode cn) {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PlayerKeepInventoryEvent", "fire", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/player/EntityPlayer;)Z", false));

		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_71049_a", "clonePlayer", "(Lnet/minecraft/entity/player/EntityPlayer;Z)V");
		AbstractInsnNode ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.LDC, "keepInventory");
		AbstractInsnNode load = ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(ain), Opcodes.ALOAD);
		ReikaASMHelper.deleteFrom(m.instructions, load.getNext(), ain.getNext());
		m.instructions.insert(load, ReikaASMHelper.copyInsnList(li));

		li = new InsnList();
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PlayerKeepInventoryEvent", "fire", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));

		m = ReikaASMHelper.getMethodByName(cn, "func_70645_a", "onDeath", "(Lnet/minecraft/util/DamageSource;)V");
		ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.LDC, "keepInventory");
		load = ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(ain), Opcodes.ALOAD);
		ReikaASMHelper.deleteFrom(m.instructions, load.getNext(), ain.getNext());
		m.instructions.insert(load, ReikaASMHelper.copyInsnList(li));
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}
}

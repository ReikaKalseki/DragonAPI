package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class XPUpdate extends Patcher {

	public XPUpdate() {
		super("net.minecraft.entity.item.EntityXPOrb", "sq");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70071_h_", "onUpdate", "()V");
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
		AbstractInsnNode loc = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.RETURN);
		m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/XPUpdateEvent", "fire", "(Lnet/minecraft/entity/item/EntityXPOrb;)V", false));
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}
}

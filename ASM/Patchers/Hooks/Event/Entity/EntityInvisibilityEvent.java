package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

import cpw.mods.fml.relauncher.Side;


public abstract class EntityInvisibilityEvent extends Patcher {

	public EntityInvisibilityEvent(String deobf, String obf) {
		super(deobf, obf);
	}

	@Override
	protected final void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_98034_c", "isInvisibleToPlayer", "(Lnet/minecraft/entity/player/EntityPlayer;)Z");
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/EntityInvisibilityDeRenderEvent", "fire", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));
	}

	@Override
	public final boolean runsOnSide(Side s) {
		return s == Side.CLIENT;
	}

}

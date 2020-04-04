package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Block;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public abstract class LeafDecayEvent extends Patcher {

	private String methodName = FMLForgePlugin.RUNTIME_DEOBF ? "func_149674_a" : "updateTick";
	private String methodDesc = "(Lnet/minecraft/world/World;IIILjava/util/Random;)V";

	public LeafDecayEvent(String s) {
		super(s, s);
	}

	public LeafDecayEvent(String deobf, String obf) {
		super(deobf, obf);
	}

	protected final void setMethod(String name, String sig) {
		methodName = name;
		methodDesc = sig;
	}

	@Override
	protected final void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, methodName, methodDesc);
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.name.equals("canSustainLeaves")) {
					min.name = "fire";
					min.owner = "Reika/DragonAPI/Instantiable/Event/LeafDecayEvent";
					ReikaASMHelper.addLeadingArgument(min, "Lnet/minecraft/block/Block;");
					min.setOpcode(Opcodes.INVOKESTATIC);
				}
			}
		}
	}

}

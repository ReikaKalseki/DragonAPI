package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class RiverGenLayerEvent extends Patcher {

	public RiverGenLayerEvent() {
		super("net.minecraft.world.gen.layer.GenLayerRiverMix", "axv");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_75904_a", "getInts", "(IIII)[I");

		//ReikaASMHelper.writeClassFile(cn, "C:/GenLayerShore_preASM");
		//ReikaASMHelper.log(ReikaASMHelper.clearString(m.instructions));

		int version = ReikaASMHelper.forgeVersion_Build;
		if (version == 1614) {
			this.apply_1614(m);
		}
		else if (version == 1558) {
			this.apply_1558(m);
		}
		else {
			throw new InstallationException("DragonAPI", "Unsupported Forge version! Use either 1558 or 1614!");
		}
	}

	private void apply_1614(MethodNode m) {
		AbstractInsnNode ain = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.IAND);
		int idx = m.instructions.indexOf(ain);
		AbstractInsnNode first = ReikaASMHelper.getLastInsnBefore(m.instructions, idx, Opcodes.ALOAD, 7);
		AbstractInsnNode last = ReikaASMHelper.getFirstOpcodeAfter(m.instructions, idx, Opcodes.IASTORE);

		//AbstractInsnNode ain = ReikaASMHelper.getNthOpcode(m.instructions, Opcodes.IMUL, 2);
		//AbstractInsnNode first = ReikaASMHelper.getFirstOpcodeAfter(m.instructions, m.instructions.indexOf(ain), Opcodes.ALOAD);
		//ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.GOTO);
		//AbstractInsnNode last = ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(ain), Opcodes.IASTORE);

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 5));
		li.add(new VarInsnNode(Opcodes.ALOAD, 6));
		li.add(new VarInsnNode(Opcodes.ALOAD, 7));
		li.add(new VarInsnNode(Opcodes.ILOAD, 8));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GenLayerRiverEvent", "fire_1614", "([I[I[II)V", false));

		AbstractInsnNode ref = first.getPrevious();
		ReikaASMHelper.deleteFrom(m.instructions, first, last);
		m.instructions.insert(ref, li);
	}

	private void apply_1558(MethodNode m) {
		VarInsnNode first = (VarInsnNode)ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.ALOAD, 5);
		JumpInsnNode last = (JumpInsnNode)ReikaASMHelper.getNthOpcode(m.instructions, Opcodes.IF_ICMPEQ, 2);

		ReikaASMHelper.deleteFrom(m.instructions, first.getNext(), last.getPrevious());
		InsnList li = new InsnList();
		//li.add(new VarInsnNode(Opcodes.ALOAD, 5));
		li.add(new VarInsnNode(Opcodes.ILOAD, 8));
		li.add(new InsnNode(Opcodes.IALOAD));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GenLayerRiverEvent", "fire_1558", "(I)Z", false));
		//li.add(new JumpInsnNode(Opcodes.IFEQ, ));

		last.setOpcode(Opcodes.IFEQ);
		m.instructions.insert(first, li);
	}
}

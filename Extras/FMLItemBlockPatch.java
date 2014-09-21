package Reika.DragonAPI.Extras;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class FMLItemBlockPatch implements IClassTransformer {

	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2) {
		if (arg0.equals("cpw.mods.fml.common.registry.GameData")) {
			return this.patchClassASM(arg0, arg2);
		}
		return arg2;
	}

	public byte[] patchClassASM(String name, byte[] bytes) {
		String methodName = "processIdRematches";
		String methodDesc = "(Ljava/lang/Iterable;ZLcpw/mods/fml/common/registry/GameData;Ljava/util/Map;)Ljava/util/List;";

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			if (m.name.equals(methodName) && m.desc.equals(methodDesc)) {
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 9));
				toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "cpw/mods/fml/common/event/FMLMissingMappingsEvent$MissingMapping", "id", "I"));
				toInject.add(new IntInsnNode(Opcodes.SIPUSH, 165));
				LabelNode label1 = new LabelNode();
				toInject.add(new JumpInsnNode(Opcodes.IF_ICMPLT, label1));
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 9));
				toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "cpw/mods/fml/common/event/FMLMissingMappingsEvent$MissingMapping", "id", "I"));
				toInject.add(new IntInsnNode(Opcodes.SIPUSH, 169));
				LabelNode label2 = new LabelNode();
				toInject.add(new JumpInsnNode(Opcodes.IF_ICMPLE, label2));
				toInject.add(label1);
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 9));
				toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "cpw/mods/fml/common/event/FMLMissingMappingsEvent$MissingMapping", "id", "I"));
				toInject.add(new IntInsnNode(Opcodes.SIPUSH, 175));
				LabelNode label3 = new LabelNode();
				toInject.add(new JumpInsnNode(Opcodes.IF_ICMPLE, label3));
				toInject.add(label2);
				AbstractInsnNode foundNode = null;
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode insn = m.instructions.get(i);
					if (foundNode != insn && insn instanceof FieldInsnNode) {
						FieldInsnNode insn2 = (FieldInsnNode)insn;
						if (insn2.getOpcode() == Opcodes.GETSTATIC && insn2.owner.equals("cpw/mods/fml/common/event/FMLMissingMappingsEvent$Action") && insn2.name.equals("DEFAULT")) {
							m.instructions.insertBefore(m.instructions.get(i - 1), toInject);
							i += toInject.size();
							foundNode = insn;
						}
					} else if (insn instanceof MethodInsnNode) {
						MethodInsnNode insn2 = (MethodInsnNode)insn;
						if (insn2.getOpcode() == Opcodes.INVOKESPECIAL && insn2.owner.equals("cpw/mods/fml/common/registry/GameData") && insn2.name.equals("block")) {
							m.instructions.insert(insn, label3);
							break;
						}
					}
				}
				break;
			}
		}

		// ASM specific for cleaning up and returning the final bytes for JVM processing.
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		try {
			FileOutputStream out = new FileOutputStream(new File("GameData.class"));
			out.write(writer.toByteArray());
			out.flush(); out.close();
		} catch (Exception ex) {}
		return writer.toByteArray();
	}
}
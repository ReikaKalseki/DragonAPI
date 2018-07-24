/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class WorldGeneratorHooks implements IClassTransformer {

	private final HashSet<String> superClasses = new HashSet();

	public WorldGeneratorHooks() {
		superClasses.add("net/minecraft/world/gen/feature/WorldGenerator");
		superClasses.add("net/minecraft/world/gen/feature/WorldGenAbstractTree");
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		if (superClasses.contains(classNode.superName) && (classNode.access & Modifier.ABSTRACT) == 0) {
			ReikaASMHelper.activeMod = "DragonAPI";
			MethodNode m = ReikaASMHelper.getMethodByName(classNode, "func_76484_a", "generate", "(Lnet/minecraft/world/World;Ljava/util/Random;III)Z");
			Collection<AbstractInsnNode> c = new ArrayList();
			for (int i = 0; i < m.instructions.size(); i++) {
				AbstractInsnNode ain = m.instructions.get(i);
				if (ain.getOpcode() == Opcodes.IRETURN) {
					c.add(ain);
				}
			}
			this.inject(m, m.instructions.getFirst(), true);
			for (AbstractInsnNode ain : c)
				this.inject(m, ain, false);
			ReikaASMHelper.log("Injected "+(c.size()+1)+" profiling hooks into "+classNode.name);
			ReikaASMHelper.activeMod = null;
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		classNode.check(classNode.version);
		return writer.toByteArray();
	}

	private void inject(MethodNode m, AbstractInsnNode ain, boolean isPre) {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		if (isPre) {
			li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/WorldgenProfiler", "startGenerator", "(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/feature/WorldGenerator;)V", false));
		}
		else {
			li.add(new VarInsnNode(Opcodes.ILOAD, 3)); //x
			li.add(new VarInsnNode(Opcodes.ILOAD, 5)); //z
			li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/WorldgenProfiler", "onRunGenerator", "(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/feature/WorldGenerator;II)V", false));
		}

		m.instructions.insertBefore(ain, li);
	}
}

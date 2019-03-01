/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Profiling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodException;
import Reika.DragonAPI.Interfaces.Subgenerator;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class MapGeneratorProfilingHooks implements IClassTransformer {

	private final HashSet<String> superClasses = new HashSet();

	public MapGeneratorProfilingHooks() {
		superClasses.add("net/minecraft/world/gen/MapGenBase");
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ClassNode cn = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(cn, 0);

		if (superClasses.contains(cn.superName) && !cn.interfaces.contains(Subgenerator.class.getName().replace(".", "/"))) {
			ReikaASMHelper.activeMod = "DragonAPI";
			//if ((classNode.access & Modifier.ABSTRACT) == 0) {
			try {
				ReikaASMHelper.activeMod = "DragonAPI";
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_151538_a", "func_151538_a", "(Lnet/minecraft/world/World;IIII[Lnet/minecraft/block/Block;)V");
				Collection<AbstractInsnNode> c = new ArrayList();
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.RETURN) {
						c.add(ain);
					}
				}
				this.inject(m, m.instructions.getFirst(), true);
				for (AbstractInsnNode ain : c)
					this.inject(m, ain, false);
				ReikaASMHelper.log("Injected "+(c.size()+1)+" profiling hooks into "+cn.name);
			}
			catch (NoSuchASMMethodException e) {
				ReikaASMHelper.log("Skipping profiling hooks on "+cn.name+"; does not contain generate method");
			}
			ReikaASMHelper.activeMod = null;
		}
		else {
			return bytes;
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(writer);
		cn.check(cn.version);
		return writer.toByteArray();
	}

	private void inject(MethodNode m, AbstractInsnNode ain, boolean isPre) {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4)); //chunkX (the non-offset one)
		li.add(new VarInsnNode(Opcodes.ILOAD, 5)); //chunkZ (the non-offset one)
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/WorldgenProfiler", isPre ? "startGenerator" : "onRunGenerator", "(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/MapGenBase;II)V", false));

		m.instructions.insertBefore(ain, li);
	}
}

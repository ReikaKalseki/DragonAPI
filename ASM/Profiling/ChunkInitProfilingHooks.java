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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import net.minecraft.launchwrapper.IClassTransformer;

public class ChunkInitProfilingHooks implements IClassTransformer {

	public ChunkInitProfilingHooks() {

	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		if (classNode.name.startsWith("net/minecraft/world/gen/") && !classNode.name.equals("net/minecraft/world/gen/ChunkProviderServer") && classNode.interfaces.contains("net/minecraft/world/chunk/IChunkProvider")) {
			ReikaASMHelper.activeMod = "DragonAPI";
			//if ((classNode.access & Modifier.ABSTRACT) == 0) {
			try {
				ReikaASMHelper.activeMod = "DragonAPI";
				MethodNode m = ReikaASMHelper.getMethodByName(classNode, "func_73154_d", "provideChunk", "(II)Lnet/minecraft/world/chunk/Chunk;");
				Collection<AbstractInsnNode> c = new ArrayList();
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.ARETURN) {
						c.add(ain);
					}
				}
				this.inject(m, m.instructions.getFirst(), true);
				for (AbstractInsnNode ain : c)
					this.inject(m, ain, false);
				ReikaASMHelper.log("Injected "+(c.size()+1)+" profiling hooks into "+classNode.name);
			}
			catch (NoSuchASMMethodException e) {
				ReikaASMHelper.log("Skipping profiling hooks on "+classNode.name+"; does not contain provideChunk method");
			}
			ReikaASMHelper.activeMod = null;
		}
		else {
			return bytes;
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		classNode.check(classNode.version);
		return writer.toByteArray();
	}

	private void inject(MethodNode m, AbstractInsnNode ain, boolean isPre) {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/WorldgenProfiler", isPre ? "startChunkInit" : "finishChunkInit", "(Lnet/minecraft/world/chunk/IChunkProvider;II)V", false));

		m.instructions.insertBefore(ain, li);
	}
}

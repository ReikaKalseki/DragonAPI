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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

import Reika.DragonAPI.ASM.Patchers.Hooks.Event.World.ChunkRequestEvent;
import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodException;
import Reika.DragonAPI.Interfaces.Subgenerator;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class WorldGeneratorProfilingHooks implements IClassTransformer {

	private final HashSet<String> superClasses = new HashSet();

	public WorldGeneratorProfilingHooks() {
		superClasses.add("net/minecraft/world/gen/feature/WorldGenerator");
		superClasses.add("net/minecraft/world/gen/feature/WorldGenAbstractTree");
		superClasses.add("net/minecraft/world/gen/feature/WorldGenHugeTrees");
		superClasses.add("biomesoplenty/common/world/generation/WorldGeneratorBOP");
		superClasses.add("Reika/ChromatiCraft/Base/ChromaWorldGenerator");
		superClasses.add("twilightforest/world/TFTreeGenerator");
		superClasses.add("twilightforest/world/TFGenerator");
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		if (!ChunkRequestEvent.patch.isEnabled())
			return bytes;

		ClassNode cn = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(cn, 0);

		if (superClasses.contains(cn.superName) && !cn.interfaces.contains(Subgenerator.class.getName().replace(".", "/"))) {
			ReikaASMHelper.activeMod = "DragonAPI";
			//if ((classNode.access & Modifier.ABSTRACT) == 0) {
			boolean flag = true;
			try {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_76484_a", "generate", "(Lnet/minecraft/world/World;Ljava/util/Random;III)Z");
				if ((m.access & Modifier.ABSTRACT) != 0) {
					flag = false;
				}
				else {
					Collection<AbstractInsnNode> c = new ArrayList();
					for (int i = 0; i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (ain.getOpcode() == Opcodes.IRETURN) {
							c.add(ain);
						}
					}
					this.inject(cn, m, m.instructions.getFirst(), true);
					for (AbstractInsnNode ain : c)
						this.inject(cn, m, ain, false);
					ReikaASMHelper.log("Injected "+(c.size()+1)+" profiling hooks into "+cn.name);

					ReikaASMHelper.addField(cn, "cachedX", "I", Modifier.PROTECTED, 0);
					ReikaASMHelper.addField(cn, "cachedZ", "I", Modifier.PROTECTED, 0);
				}
			}
			catch (NoSuchASMMethodException e) {
				flag = false;
			}
			if (!flag) {
				ReikaASMHelper.log("Skipping profiling hooks on "+cn.name+"; does not contain generate method");
				if (!superClasses.contains(cn.name)) {
					ReikaASMHelper.log("This class should be added to the superClass generator parent list!");
				}
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

	private void inject(ClassNode cn, MethodNode m, AbstractInsnNode ain, boolean isPre) {
		InsnList li = new InsnList();
		if (isPre) {
			li.add(new VarInsnNode(Opcodes.ALOAD, 0));
			li.add(new VarInsnNode(Opcodes.ILOAD, 3)); //cache X and Z in case they are modified across generate()
			li.add(new FieldInsnNode(Opcodes.PUTFIELD, cn.name, "cachedX", "I"));
			li.add(new VarInsnNode(Opcodes.ALOAD, 0));
			li.add(new VarInsnNode(Opcodes.ILOAD, 5));
			li.add(new FieldInsnNode(Opcodes.PUTFIELD, cn.name, "cachedZ", "I"));
		}

		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));

		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, cn.name, "cachedX", "I"));

		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, cn.name, "cachedZ", "I"));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/WorldgenProfiler", isPre ? "startGenerator" : "onRunGenerator", "(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/feature/WorldGenerator;II)V", false));

		m.instructions.insertBefore(ain, li);
	}
}

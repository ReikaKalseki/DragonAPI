/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.HashMap;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class DragonAPIClassTransfomer implements IClassTransformer {

	private static final HashMap<String, ClassPatch> classes = new HashMap();

	private static enum ClassPatch {
		CREEPERBOMBEVENT("net.minecraft.entity.monster.EntityCreeper", "xz"),
		ITEMRENDEREVENT("net.minecraft.client.gui.inventory.GuiContainer", "bex");

		private final String obfName;
		private final String deobfName;

		private static final ClassPatch[] list = values();

		private ClassPatch(String deobf, String obf) {
			obfName = obf;
			deobfName = deobf;
		}

		private byte[] apply(byte[] data) {
			ClassNode cn = new ClassNode();
			ClassReader classReader = new ClassReader(data);
			classReader.accept(cn, 0);
			switch(this) {
			case CREEPERBOMBEVENT: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146077_cc", "func_146077_cc", "()V");
				if (m == null) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Could not find method for "+this+" ASM handler!");
				}
				else {
					AbstractInsnNode pos = null;
					for (int i = 0; i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (ain.getOpcode() == Opcodes.IFNE) {
							pos = ain;
							break;
						}
					}
					while (pos.getNext() instanceof LineNumberNode || pos.getNext() instanceof LabelNode) {
						pos = pos.getNext();
					}
					m.instructions.insert(pos, new InsnNode(Opcodes.POP));
					m.instructions.insert(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z"));
					m.instructions.insert(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/CreeperExplodeEvent", "<init>", "(Lnet/minecraft/entity/monster/EntityCreeper;)V"));
					m.instructions.insert(pos, new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.insert(pos, new InsnNode(Opcodes.DUP));
					m.instructions.insert(pos, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/CreeperExplodeEvent"));
					m.instructions.insert(pos, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
				}
			}
			break;
			case ITEMRENDEREVENT: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146977_a", "func_146977_a", "(Lnet/minecraft/inventory/Slot;)V");
				if (m == null) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Could not find method for "+this+" ASM handler!");
				}
				else {
					AbstractInsnNode pos = m.instructions.getFirst();
					m.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					m.instructions.insertBefore(pos, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/RenderItemInSlotEvent"));
					m.instructions.insertBefore(pos, new InsnNode(Opcodes.DUP));
					m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
					m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/RenderItemInSlotEvent", "<init>", "(Lnet/minecraft/client/gui/inventory/GuiContainer;Lnet/minecraft/inventory/Slot;)V"));
					m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z"));
					m.instructions.insertBefore(pos, new InsnNode(Opcodes.POP));

					ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
				}
			}
			break;
			}

			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS/* | ClassWriter.COMPUTE_FRAMES*/);
			cn.accept(writer);
			return writer.toByteArray();
		}
	}

	@Override
	public byte[] transform(String className, String className2, byte[] opcodes) {
		if (!classes.isEmpty()) {
			ClassPatch p = classes.get(className);
			if (p != null) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Patching class "+className);
				opcodes = p.apply(opcodes);
				classes.remove(className); //for maximizing performance
			}
		}
		return opcodes;
	}

	static {
		for (int i = 0; i < ClassPatch.list.length; i++) {
			ClassPatch p = ClassPatch.list[i];
			String s = !FMLForgePlugin.RUNTIME_DEOBF ? p.deobfName : p.obfName;
			classes.put(s, p);
		}
	}
}

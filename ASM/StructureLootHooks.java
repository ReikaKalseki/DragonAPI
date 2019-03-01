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
import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodException;
import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodInstructionException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class StructureLootHooks implements IClassTransformer {

	private final HashSet<String> superClasses = new HashSet();

	public StructureLootHooks() {
		superClasses.add("net/minecraft/world/gen/structure/StructureComponent");
		superClasses.add("net/minecraft/world/gen/structure/ComponentScatteredFeaturePieces$Feature");
		superClasses.add("net/minecraft/world/gen/structure/StructureNetherBridgePieces$Piece");
		superClasses.add("net/minecraft/world/gen/structure/StructureStrongholdPieces$Stronghold");
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ClassNode cn = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(cn, 0);

		if (superClasses.contains(cn.name) || superClasses.contains(cn.superName)) {
			ReikaASMHelper.activeMod = "DragonAPI";
			//if ((classNode.access & Modifier.ABSTRACT) == 0) {
			try {
				ReikaASMHelper.activeMod = "DragonAPI";
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_74879_a", "generateStructureChestContents", "(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/structure/StructureBoundingBox;Ljava/util/Random;III[Lnet/minecraft/util/WeightedRandomChestContent;I)Z");
				MethodInsnNode look = ReikaASMHelper.getFirstMethodCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_76293_a" : "generateChestContents");
				this.inject(cn, m, look);
				ReikaASMHelper.log("Injected loot hooks into "+cn.name);

				ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				cn.accept(writer);
				cn.check(cn.version);

				ReikaASMHelper.activeMod = null;
				return writer.toByteArray();
			}
			catch (NoSuchASMMethodException e) {
				ReikaASMHelper.log("Skipping loot hooks on "+cn.name+"; does not contain loot generation method");
				ReikaASMHelper.activeMod = null;
				return bytes;
			}
			catch (NoSuchASMMethodInstructionException e) {
				ReikaASMHelper.log("Skipping loot hooks on "+cn.name+"; does not contain call to generate loot");
				ReikaASMHelper.activeMod = null;
				return bytes;
			}
			catch (Exception e) {
				ReikaASMHelper.log("Could not add loot hooks to "+cn.name+": "+e.toString());
				e.printStackTrace();
				ReikaASMHelper.activeMod = null;
				return bytes;
			}
		}
		else {
			return bytes;
		}
	}

	public static void inject(ClassNode cn, MethodNode m, AbstractInsnNode loc) {
		int var = ((VarInsnNode)ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(loc), Opcodes.ASTORE)).var;
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, var));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/ChestLootEvent", "fire", "(Ljava/lang/Object;Lnet/minecraft/inventory/IInventory;)V", false));
		m.instructions.insert(loc, li);
	}
}

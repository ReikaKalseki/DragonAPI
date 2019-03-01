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

import java.util.HashMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class SpecialBiomePlacement implements IClassTransformer {

	private final HashMap<String, String> relay = new HashMap();

	private static final String CLASS = "net/minecraft/world/gen/layer/GenLayerBiome";

	private static final int FLAG = 3840; //0x0F00; -3841 = 0xF0FF

	public SpecialBiomePlacement() {
		relay.put(FMLForgePlugin.RUNTIME_DEOBF ? "field_150608_ab" : "mesaPlateau", "getBiome_Hot");
		relay.put(FMLForgePlugin.RUNTIME_DEOBF ? "field_150607_aa" : "mesaPlateau_F", "getBiome_Hot2");
		relay.put(FMLForgePlugin.RUNTIME_DEOBF ? "field_76782_w" : "jungle", "getBiome_Warm");
		relay.put(FMLForgePlugin.RUNTIME_DEOBF ? "field_150578_U" : "megaTaiga", "getBiome_Cool");
		relay.put(FMLForgePlugin.RUNTIME_DEOBF ? "field_76789_p" : "mushroomIsland", "getBiome_Cold");
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ClassNode cn = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(cn, 0);

		if (CLASS.equals(cn.name) || CLASS.equals(cn.superName)) {
			ReikaASMHelper.activeMod = "DragonAPI";
			ReikaASMHelper.log("Injecting special biome placement code into "+cn.name);
			MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_75904_a", "getInts", "(IIII)[I");

			int l1_Var = -1;

			boolean flag = false;
			for (int i = 0; i < m.instructions.size(); i++) {
				AbstractInsnNode ain = m.instructions.get(i);
				if (ain.getOpcode() == Opcodes.SIPUSH && l1_Var == -1) {
					IntInsnNode iin = (IntInsnNode)ain;
					if (iin.operand == FLAG) {
						l1_Var = ((VarInsnNode)ReikaASMHelper.getFirstOpcodeAfter(m.instructions, i, Opcodes.ISTORE)).var;
					}
				}
				else if (ain.getOpcode() == Opcodes.GETSTATIC) {
					if (flag) {
						FieldInsnNode fin = (FieldInsnNode)ain;
						InsnList li = this.createRelay(fin, l1_Var);
						if (li != null) {
							AbstractInsnNode prev = fin.getPrevious();
							m.instructions.remove(fin.getNext());
							m.instructions.remove(fin);
							m.instructions.insert(prev, li);
							ReikaASMHelper.log("Replaced placement of "+fin.name+" with '"+relay.get(fin.name)+"' hook");
						}
					}
					else {
						flag = true;
					}
				}
			}
			ReikaASMHelper.activeMod = null;
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(writer);
		cn.check(cn.version);
		return writer.toByteArray();
	}

	private InsnList createRelay(FieldInsnNode original, int l1) {
		String call = relay.get(original.name);
		if (call == null)
			return null;

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ILOAD, l1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/SpecialBiomePlacementRegistry", call, "(I)I", false));
		return li;
	}

}

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

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class BlockPosWrapper {

	private static final String BLOCKPOS_CLASS = "net/minecraft/util/math/BlockPos";
	private static final String BLOCKPOS_CONSTR = "(III)V";
	private static final String BLOCKPOS_ARG = "L"+BLOCKPOS_CLASS+";";

	private static final HashMap<String, ImmutablePair<String, String>> classes = new HashMap();

	private static void addClass(String deobf, String obf) {
		ImmutablePair<String, String> p = new ImmutablePair(deobf, obf);
		classes.put(deobf, p);
		classes.put(obf, p);
	}

	private static boolean runForClass(String name, ClassNode cn) {
		ImmutablePair<String, String> p = classes.get(name);
		return p != null && name.equals(FMLForgePlugin.RUNTIME_DEOBF ? p.right : p.left);
	}

	public static void injectBlockPosWrappers(String name, ClassNode cn) {
		if (runForClass(name, cn)) {
			for (MethodNode mn : cn.methods) {
				if (mn.desc.contains(BLOCKPOS_ARG)) {
					injectBlockPosWrapper(cn, mn);
				}
			}
		}
	}

	public static void injectBlockPosWrapper(ClassNode cn, MethodNode mn) {
		injectBlockPosWrapper(cn, mn, 1);
	}

	public static void injectBlockPosWrapper(ClassNode cn, MethodNode mn, int minVar) {
		InsnList li = new InsnList();
		MethodInsnNode relay = ReikaASMHelper.getCallerInsn(cn.name, mn);
		ArrayList<String> args = ReikaASMHelper.parseMethodSignature(relay);
		int pos = args.indexOf(BLOCKPOS_ARG);
		args.remove(pos);
		args.add(pos, "I");
		args.add(pos, "I");
		args.add(pos, "I");
		li.add(new TypeInsnNode(Opcodes.NEW, BLOCKPOS_CLASS));
		li.add(new InsnNode(Opcodes.DUP));
		li.add(new VarInsnNode(Opcodes.ILOAD, minVar));
		li.add(new VarInsnNode(Opcodes.ILOAD, minVar+1));
		li.add(new VarInsnNode(Opcodes.ILOAD, minVar+2));
		li.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, BLOCKPOS_CLASS, "<init>", BLOCKPOS_CONSTR, false));
		li.add(relay);
		li.add(new InsnNode(ReikaASMHelper.getOpcodeForMethodReturn(relay)));
		String sig = ReikaASMHelper.compileSignature(args);
		ReikaASMHelper.addMethod(cn, li, mn.name, sig, mn.access);
	}

}

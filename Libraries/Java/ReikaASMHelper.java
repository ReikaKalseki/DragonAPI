/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

public class ReikaASMHelper {

	public static MethodNode getMethodByName(ClassNode c, String obf, String deobf, String sig) {
		String s = FMLForgePlugin.RUNTIME_DEOBF ? obf : deobf;
		List<MethodNode> methods = c.methods;
		for (int k = 0; k < methods.size(); k++) {
			MethodNode m = methods.get(k);
			if ((m.name.equals(s) && m.desc.equals(sig))) {
				return m;
			}
		}
		return null;
	}

	public static void removeCodeLine(MethodNode m, int line) {
		ArrayList<AbstractInsnNode> toRemove = new ArrayList();
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain instanceof LineNumberNode) {
				if (((LineNumberNode)ain).line == line) {
					toRemove.add(ain.getPrevious()); //"L#"
					while (!(ain.getNext() instanceof LineNumberNode)) {
						toRemove.add(ain);
						ain = ain.getNext();
					}
				}
			}
		}
		for (int i = 0; i < toRemove.size(); i++) {
			AbstractInsnNode insn = toRemove.get(i);
			m.instructions.remove(insn);
		}
	}

	public static boolean isMethodCall(AbstractInsnNode ain, String obf, String deobf) {
		if (ain instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode)ain;
			String s = FMLForgePlugin.RUNTIME_DEOBF ? obf : deobf;
			return min.name.equals(s);
		}
		return false;
	}

	public static void insertNAfter(MethodNode m, AbstractInsnNode root, AbstractInsnNode arg, int n) {
		for (int i = 0; i < n; i++) {
			root = root.getNext();
		}
		m.instructions.insert(root, arg);
	}

	public static void insertNAfter(MethodNode m, AbstractInsnNode root, InsnList arg, int n) {
		for (int i = 0; i < n; i++) {
			root = root.getNext();
		}
		m.instructions.insert(root, arg);
	}

	public static String clearString(InsnList c) {
		return printInsnList(c.iterator());
	}

	public static String clearString(Collection<AbstractInsnNode> c) {
		return printInsnList(c.iterator());
	}

	private static String printInsnList(Iterator<AbstractInsnNode> it) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		while (it.hasNext()) {
			AbstractInsnNode ain = it.next();
			sb.append(clearString(ain));
			sb.append("");
		}
		sb.append("\n}");
		return sb.toString();
	}

	public static String clearString(AbstractInsnNode ain) {
		Textifier t = new Textifier();
		TraceMethodVisitor mv = new TraceMethodVisitor(t);
		ain.accept(mv);
		StringWriter sw = new StringWriter();
		t.print(new PrintWriter(sw));
		t.getText().clear();
		return sw.toString();
	}

}

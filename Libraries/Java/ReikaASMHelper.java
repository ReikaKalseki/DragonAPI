/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import Reika.DragonAPI.Exception.ASMException.NoSuchASMFieldException;
import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodException;

public class ReikaASMHelper {

	private static Field opcodeField;

	public static void changeFieldType(ClassNode c, String obf, String deobf, String newType) throws NoSuchASMFieldException {
		FieldNode f = getFieldByName(c, obf, deobf);
		f.desc = newType;
		for (MethodNode m : c.methods) {
			for (int i = 0; i < m.instructions.size(); i++) {
				AbstractInsnNode ain = m.instructions.get(i);
				if (ain instanceof FieldInsnNode) {
					FieldInsnNode fin = (FieldInsnNode)ain;
					if (fin.name.equals(f.name)) {
						fin.desc = f.desc;
					}
				}
			}
		}
	}

	public static void changeMethodReturnType(ClassNode c, String obf, String deobf, String newType) throws NoSuchASMMethodException {
		//need to somehow set up an automated system where if this method is referenced anywhere else, it auto-ASMs
	}

	public static FieldNode getFieldByName(ClassNode c, String name) throws NoSuchASMFieldException {
		return getFieldByName(c, name, name);
	}

	public static FieldNode getFieldByName(ClassNode c, String obf, String deobf) throws NoSuchASMFieldException {
		String s = FMLForgePlugin.RUNTIME_DEOBF ? obf : deobf;
		List<FieldNode> fields = c.fields;
		for (int k = 0; k < fields.size(); k++) {
			FieldNode f = fields.get(k);
			if (f.name.equals(s)) {
				return f;
			}
		}
		throw new NoSuchASMFieldException(c, s);
	}

	public static MethodNode getMethodByName(ClassNode c, String name, String sig) throws NoSuchASMMethodException {
		return getMethodByName(c, name, name, sig);
	}

	public static MethodNode getMethodByName(ClassNode c, String obf, String deobf, String sig) throws NoSuchASMMethodException {
		String s = FMLForgePlugin.RUNTIME_DEOBF ? obf : deobf;
		List<MethodNode> methods = c.methods;
		for (int k = 0; k < methods.size(); k++) {
			MethodNode m = methods.get(k);
			if (m.name.equals(s) && m.desc.equals(sig)) {
				return m;
			}
		}
		throw new NoSuchASMMethodException(c, s, sig);
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

	public static ArrayList<String> parseMethodArguments(MethodNode mn) {
		//ReikaJavaLibrary.pConsole("PARSING METHOD: "+mn.desc);
		String desc = mn.desc.substring(mn.desc.indexOf('(')+1, mn.desc.lastIndexOf(')')); //strip to inside brackets
		ArrayList<String> li = new ArrayList();
		parseArguments(li, desc);
		//ReikaJavaLibrary.pConsole("PARSED METHOD: "+mn.desc+" > "+li);
		return li;
	}

	private static void parseArguments(ArrayList<String> args, String desc) {
		//ReikaJavaLibrary.pConsole("PARSING: "+desc);
		if (desc.startsWith("L")) { //Class
			int semi = desc.indexOf(';');
			String arg = desc.substring(0, semi+1);
			//ReikaJavaLibrary.pConsole("Parsed as class: "+arg);
			args.add(arg);
			parseArguments(args, desc.substring(arg.length()));
		}
		else if (desc.isEmpty()) { //done
			//ReikaJavaLibrary.pConsole("Parsed empty.");
		}
		else { //primitive
			String prim = desc.substring(0, 1);
			//ReikaJavaLibrary.pConsole("Parsed as primitive: "+prim);
			args.add(prim);
			parseArguments(args, desc.substring(1));
		}
	}

	public static boolean memberHasAnnotationOfType(MethodNode mn, String type) {
		return hasAnnotation(mn.visibleAnnotations, type);
	}

	public static boolean memberHasAnnotationOfType(FieldNode fn, String type) {
		return hasAnnotation(fn.visibleAnnotations, type);
	}

	private static boolean hasAnnotation(List<AnnotationNode> li, String type) {
		if (li == null || li.isEmpty())
			return false;
		for (AnnotationNode ann : li) {
			if (ann.desc.startsWith(type))
				return true;
		}
		return false;
	}

	public static void clearMethodBody(MethodNode m) {
		m.instructions.clear();/*
		String[] s = m.desc.split("\\)");
		String ret = s[s.length-1];
		ReturnType type = ReturnType.getFromSig(ret);
		AbstractInsnNode retcall = null;
		switch(type) {
		case LONG:
			retcall = new InsnNode(Opcodes.LRETURN);
			break;
		case DOUBLE:
			retcall = new InsnNode(Opcodes.DRETURN);
			break;
		case FLOAT:
			retcall = new InsnNode(Opcodes.FRETURN);
			break;
		case INT:
		case BYTE:
		case SHORT:
		case BOOLEAN:
			retcall = new InsnNode(Opcodes.IRETURN);
			break;
		case FLOATARRAY:
		case INTARRAY:
		case BOOLARRAY:
		case SHORTARRAY:
		case DOUBARRAY:
		case BYTEARRAY:
		case OBJECT:
			retcall = new InsnNode(Opcodes.ARETURN);
			break;
		case VOID:
			retcall = new InsnNode(Opcodes.RETURN);
			break;
		}
		if (retcall != null)
			m.instructions.add(retcall);*/
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

	public static void changeOpcode(AbstractInsnNode ain, int opcode) {
		try {
			opcodeField.setInt(ain, opcode);
		}
		catch (Exception e) {

		}
	}

	static {
		try {
			opcodeField = AbstractInsnNode.class.getDeclaredField("opcode");
			opcodeField.setAccessible(true);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static enum ReturnType {

		VOID("V"),
		INT("I"),
		BOOLEAN("Z"),
		BYTE("B"),
		LONG("L"),
		SHORT("S"),
		FLOAT("F"),
		DOUBLE("D"),
		INTARRAY("[I"),
		BYTEARRAY("[B"),
		SHORTARRAY("[S"),
		DOUBARRAY("[D"),
		BOOLARRAY("[Z"),
		FLOATARRAY("[F"),
		OBJECT("");

		private final String id;

		private static final HashMap<String, ReturnType> map = new HashMap();

		private ReturnType(String s) {
			id = s;
		}

		private static ReturnType getFromSig(String id) {
			return map.containsKey(id) ? map.get(id) : OBJECT;
		}

		static {
			for (int i = 0; i < values().length; i++) {
				ReturnType type = values()[i];
				map.put(type.id, type);
			}
		}

	}

}

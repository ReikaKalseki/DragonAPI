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
import java.util.Map;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import Reika.DragonAPI.Exception.ASMException;
import Reika.DragonAPI.Exception.ASMException.NoSuchASMFieldException;
import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodException;

public class ReikaASMHelper {

	private static Field opcodeField;

	private static final Logger logger = LogManager.getLogger();

	public static String activeMod;

	public static void log(Object o) {
		write(activeMod+": "+o, false);
	}

	public static void logError(Object o) {
		write(activeMod+" ERROR: "+o, true);
	}

	private static void write(String s, boolean err) {
		logger.log(err ? Level.ERROR : Level.INFO, s);
	}

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
		MethodNode mn = getMethodByNameAndSig(c, s, sig);
		if (mn == null)
			throw new NoSuchASMMethodException(c, s, sig);
		else
			return mn;
	}

	public static boolean classContainsMethod(ClassNode cn, MethodNode mn) {
		return getMethodByNameAndSig(cn, mn.name, mn.desc) != null;
	}

	private static MethodNode getMethodByNameAndSig(ClassNode c, String name, String sig) {
		List<MethodNode> methods = c.methods;
		for (int k = 0; k < methods.size(); k++) {
			MethodNode m = methods.get(k);
			if (m.name.equals(name) && m.desc.equals(sig)) {
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

	public static AbstractInsnNode getFirstOpcode(InsnList li, int opcode) {
		int index = 0;
		AbstractInsnNode ain = li.get(0);
		while (ain.getOpcode() != opcode && index < li.size()-1) {
			index++;
			ain = li.get(index);
		}
		return ain.getOpcode() == opcode ? ain : null;
	}

	public static AbstractInsnNode getNthOpcode(InsnList li, int opcode, int n) {
		int c = 0;
		for (int i = 0; i < li.size(); i++) {
			AbstractInsnNode ain = li.get(i);
			if (ain.getOpcode() == opcode) {
				c++;
				if (c == n)
					return ain;
			}
		}
		return null;
	}

	public static AbstractInsnNode getNthOfOpcodes(InsnList li, int n, int... opcodes) {
		int c = 0;
		for (int i = 0; i < li.size(); i++) {
			AbstractInsnNode ain = li.get(i);
			if (ReikaArrayHelper.contains(opcodes, ain.getOpcode())) {
				c++;
				if (c == n)
					return ain;
			}
		}
		return null;
	}

	public static AbstractInsnNode getLastOpcode(InsnList li, int opcode) {
		AbstractInsnNode ret = null;
		for (int i = 0; i < li.size(); i++) {
			AbstractInsnNode ain = li.get(i);
			if (ain.getOpcode() == opcode)
				ret = ain;
		}
		return ret;
	}

	public static AbstractInsnNode getFirstInsnAfter(InsnList li, int index, int opcode, Object... args) {
		AbstractInsnNode ain = li.get(index+1);
		while (!match(ain, opcode, args) && index < li.size()-1) {
			index++;
			ain = li.get(index);
		}
		return match(ain, opcode, args) ? ain : null;
	}

	public static AbstractInsnNode getLastInsn(InsnList li, int opcode, Object... args) {
		return getLastInsnBefore(li, li.size()-1, opcode, args);
	}

	public static AbstractInsnNode getLastInsnBefore(InsnList li, int index, int opcode, Object... args) {
		AbstractInsnNode ain = li.get(index-1);
		while (!match(ain, opcode, args) && index > 0) {
			index--;
			ain = li.get(index);
		}
		return match(ain, opcode, args) ? ain : null;
	}

	public static AbstractInsnNode getLastOpcodeBefore(InsnList li, int index, int opcode) {
		AbstractInsnNode ain = li.get(index-1);
		while (ain.getOpcode() != opcode && index > 0) {
			index--;
			ain = li.get(index);
		}
		return ain.getOpcode() == opcode ? ain : null;
	}

	public static AbstractInsnNode getLastFieldRefBefore(InsnList li, int index, String name) {
		AbstractInsnNode ain = li.get(index-1);
		while ((!(ain instanceof FieldInsnNode) || !((FieldInsnNode)ain).name.equals(name)) && index > 0) {
			index--;
			ain = li.get(index);
		}
		return ain instanceof FieldInsnNode && ((FieldInsnNode)ain).name.equals(name) ? ain : null;
	}

	public static AbstractInsnNode getLastNonZeroALOADBefore(InsnList li, int index) {
		AbstractInsnNode ain = li.get(index-1);
		while ((!(ain instanceof VarInsnNode) || ((VarInsnNode)ain).var == 0) && index > 0) {
			index--;
			ain = li.get(index);
		}
		return ain instanceof VarInsnNode && ((VarInsnNode)ain).var != 0 ? ain : null;
	}

	public static boolean match(AbstractInsnNode ain, AbstractInsnNode ain2) {
		if (ain.getOpcode() != ain2.getOpcode())
			return false;
		if (ain instanceof InsnNode) {
			return true;
		}
		else if (ain instanceof VarInsnNode) {
			return ((VarInsnNode)ain).var == ((VarInsnNode)ain2).var;
		}
		else if (ain instanceof LdcInsnNode) {
			return ((LdcInsnNode)ain2).cst.equals(((LdcInsnNode)ain).cst);
		}
		else if (ain instanceof IntInsnNode) {
			return ((IntInsnNode)ain).operand == ((IntInsnNode)ain2).operand;
		}
		else if (ain instanceof TypeInsnNode) {
			return ((TypeInsnNode)ain).desc.equals(((TypeInsnNode)ain2).desc);
		}
		else if (ain instanceof FieldInsnNode) {
			FieldInsnNode fin = (FieldInsnNode)ain;
			FieldInsnNode fin2 = (FieldInsnNode)ain2;
			return fin.owner.equals(fin2.owner) && fin.name.equals(fin2.name) && fin.desc.equals(fin2.desc);
		}
		else if (ain instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode)ain;
			MethodInsnNode min2 = (MethodInsnNode)ain2;
			return min.owner.equals(min2.owner) && min.name.equals(min2.name) && min.desc.equals(min2.desc) && min.itf == min2.itf;
		}
		else if (ain instanceof JumpInsnNode) {
			return ((JumpInsnNode)ain).label == ((JumpInsnNode)ain2).label;
		}
		else if (ain instanceof IincInsnNode) {
			IincInsnNode iin = (IincInsnNode)ain;
			IincInsnNode iin2 = (IincInsnNode)ain2;
			return iin.var == iin2.var && iin.incr == iin2.incr;
		}
		return false;
	}

	public static boolean match(AbstractInsnNode ain, int opcode, Object... args) {
		if (ain.getOpcode() != opcode)
			return false;
		if (ain instanceof InsnNode) {
			return true;
		}
		else if (ain instanceof VarInsnNode) {
			return args[0] instanceof Integer && ((VarInsnNode)ain).var == (Integer)args[0];
		}
		else if (ain instanceof LdcInsnNode) {
			return args[0].equals(((LdcInsnNode)ain).cst);
		}
		else if (ain instanceof IntInsnNode) {
			return args[0] instanceof Integer && ((IntInsnNode)ain).operand == (Integer)args[0];
		}
		else if (ain instanceof TypeInsnNode) {
			return args[0] instanceof String && ((TypeInsnNode)ain).desc.equals(args[0]);
		}
		else if (ain instanceof FieldInsnNode) {
			if (args.length != 3 || !(args[0] instanceof String) || !(args[1] instanceof String) || !(args[2] instanceof String))
				return false;
			FieldInsnNode fin = (FieldInsnNode)ain;
			return fin.owner.equals(args[0]) && fin.name.equals(args[1]) && fin.desc.equals(args[2]);
		}
		else if (ain instanceof MethodInsnNode) {
			if (args.length != 4 || !(args[0] instanceof String) || !(args[1] instanceof String) || !(args[2] instanceof String) || !(args[3] instanceof Boolean))
				return false;
			MethodInsnNode min = (MethodInsnNode)ain;
			return min.owner.equals(args[0]) && min.name.equals(args[1]) && min.desc.equals(args[2]) && min.itf == (Boolean)args[3];
		}
		else if (ain instanceof JumpInsnNode) {
			return args[0] instanceof LabelNode && ((JumpInsnNode)ain).label == args[0];
		}
		else if (ain instanceof IincInsnNode) {
			if (args.length != 2 || !(args[0] instanceof Integer) || !(args[1] instanceof Integer))
				return false;
			IincInsnNode iin = (IincInsnNode)ain;
			return iin.var == (Integer)args[0] && iin.incr == (Integer)args[1];
		}
		return false;
	}

	public static MethodInsnNode getFirstMethodCall(ClassNode cn, MethodNode m, String owner, String name, String sig) {
		return getNthMethodCall(cn, m, owner, name, sig, 1);
	}

	public static MethodInsnNode getNthMethodCall(ClassNode cn, MethodNode m, String owner, String name, String sig, int n) {
		int counter = 0;
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain instanceof MethodInsnNode) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.owner.equals(owner) && min.name.equals(name) && min.desc.equals(sig)) {
					counter++;
					if (counter >= n)
						return min;
				}
			}
		}
		throw new ASMException.NoSuchASMMethodInstructionException(cn, m, owner, name, sig, n > 1 ? n : -1);
	}

	public static FieldInsnNode getFirstFieldCall(ClassNode cn, MethodNode m, String owner, String name) {
		return getNthFieldCall(cn, m, owner, name, 1);
	}

	public static FieldInsnNode getNthFieldCall(ClassNode cn, MethodNode m, String owner, String name, int n) {
		int counter = 0;
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain instanceof FieldInsnNode) {
				FieldInsnNode min = (FieldInsnNode)ain;
				if (min.owner.equals(owner) && min.name.equals(name)) {
					counter++;
					if (counter >= n)
						return min;
				}
			}
		}
		throw new ASMException.NoSuchASMFieldInstructionException(cn, m, owner, name, n > 1 ? n : -1);
	}

	public static AbstractInsnNode getFirstOpcodeAfter(InsnList li, int idx, int opcode) {
		for (int i = idx; i < li.size(); i++) {
			AbstractInsnNode ain = li.get(i);
			if (ain.getOpcode() == opcode)
				return ain;
		}
		return null;
	}

	/** Currently broken */
	public static InsnList copyInsnList(InsnList li, LabelNode... pairs) {
		Map<LabelNode, LabelNode> map = new HashMap();
		for (int i = 0; i < pairs.length; i += 2) {
			map.put(pairs[i], pairs[i+1]);
		}
		return copyInsnList(li, map);
	}

	/** Currently broken */
	public static InsnList copyInsnList(InsnList li, Map<LabelNode, LabelNode> labels) {
		InsnList copy = new InsnList();
		for (int i = 0; i < li.size(); i++) {
			AbstractInsnNode ain = li.get(i);
			copy.add(copyInstruction(ain, labels));
		}
		return copy;
	}

	/** Currently broken */
	public static AbstractInsnNode copyInstruction(AbstractInsnNode ain, Map<LabelNode, LabelNode> labels) {
		return ain.clone(labels);
	}

	public static String clearAnnotations(List<AnnotationNode> li) {
		if (li == null)
			return "null";
		else if (li.isEmpty())
			return "[]";
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (AnnotationNode a : li) {
				sb.append(a.desc);
				sb.append(", ");
			}
			sb.append("]");
			return sb.toString();
		}
	}

	public static String clearTypeAnnotations(List<TypeAnnotationNode> li) {
		if (li == null)
			return "null";
		else if (li.isEmpty())
			return "[]";
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (AnnotationNode a : li) {
				sb.append(a.desc);
				sb.append(", ");
			}
			sb.append("]");
			return sb.toString();
		}
	}

	public static void addMethod(ClassNode cn, InsnList insns, String name, String sig, int flags) {
		if (getMethodByNameAndSig(cn, name, sig) != null)
			throw new ASMException.DuplicateASMMethodException(cn, name, sig);
		MethodNode m = new MethodNode(flags, name, sig, null, new String[0]);

		m.invisibleAnnotations = new ArrayList();
		m.invisibleParameterAnnotations = new List[0];
		m.invisibleLocalVariableAnnotations = new ArrayList();
		m.invisibleTypeAnnotations = new ArrayList();
		m.visibleAnnotations = new ArrayList();
		m.visibleLocalVariableAnnotations = new ArrayList();
		m.visibleParameterAnnotations = new List[0];
		m.visibleTypeAnnotations = new ArrayList();

		m.exceptions = new ArrayList();

		m.instructions = insns;

		cn.methods.add(m);
	}

	public static void removeMethod(ClassNode cn, String name, String sig) {
		Iterator<MethodNode> it = cn.methods.iterator();
		while (it.hasNext()) {
			MethodNode m = it.next();
			if (m.name.equals(name) && m.desc.equals(sig)) {
				it.remove();
				return;
			}
		}
	}

	public static void addField(ClassNode cn, String name, String type, int flags, Object init) {
		if (getFieldByName(cn, name) != null)
			throw new ASMException.DuplicateASMFieldException(cn, name);
		FieldNode m = new FieldNode(flags, name, type, null, init);
		cn.fields.add(m);
	}

	public static AbstractInsnNode getPattern(InsnList li, Object... pattern) {
		for (int i = 0; i < li.size(); i++) {
			if (i+pattern.length <= li.size()) {
				for (int k = 0; k < pattern.length; k++) {
					AbstractInsnNode at = li.get(i+k);
					Object o = pattern[k];
					if (o instanceof Integer) {
						if (at.getOpcode() == (int)o) {

						}
						else {
							break;
						}
					}
					else if (o instanceof AbstractInsnNode) {
						if (match(at, (AbstractInsnNode)o)) {

						}
						else {
							break;
						}
					}
					else {
						break;
					}
					if (k == pattern.length-1) {
						return li.get(i);
					}
				}
			}
		}
		return null;
	}

	public static void deleteFrom(InsnList li, AbstractInsnNode start, AbstractInsnNode end) {
		AbstractInsnNode loc = start;
		while (loc != end) {
			AbstractInsnNode loc2 = loc.getNext();
			li.remove(loc);
			loc = loc2;
		}
		li.remove(end);
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

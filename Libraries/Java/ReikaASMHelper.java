/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.classloading.FMLForgePlugin;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
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
import Reika.DragonAPI.Exception.ASMException.ASMConflictException;
import Reika.DragonAPI.Exception.ASMException.NoSuchASMFieldException;
import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodException;
import Reika.DragonAPI.IO.ReikaFileReader;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;

public class ReikaASMHelper {

	private static Field opcodeField;

	private static final Logger logger = LogManager.getLogger();

	public static String activeMod;

	public static final int forgeVersion_Major = Integer.parseInt((String)FMLInjectionData.data()[0]);
	public static final int forgeVersion_Minor = Integer.parseInt((String)FMLInjectionData.data()[1]);
	public static final int forgeVersion_Revision = Integer.parseInt((String)FMLInjectionData.data()[2]);
	public static final int forgeVersion_Build = Integer.parseInt((String)FMLInjectionData.data()[3]);

	public static final String REIKA_SRGS = "C:/Users/Reika/.gradle/caches/minecraft/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/reika/custom/srgs/";
	private static HashMap<String, String> srgMap = new HashMap();

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

	public static boolean classContainsMethod(Class c, MethodNode mn) {
		ArrayList<String> li = parseMethodSignature(mn);
		ArrayList<Class> args = new ArrayList();
		for (String s : li) {
			args.add(parseClass(s));
		}
		Class ret = args.remove(args.size()-1);
		Class[] params = args.toArray(new Class[args.size()]);
		Method[] calls = c.getDeclaredMethods();
		for (int i = 0; i < calls.length; i++) {
			Method m = calls[i];
			if (m.getReturnType() == ret) {
				if (Arrays.equals(m.getParameterTypes(), params)) {
					return true;
				}
			}
		}
		return false;
	}

	public static Class parseClass(String s) {
		if (s.charAt(0) == '[') {
			Class c = parseClass(s.substring(1));
			return Array.newInstance(c, 0).getClass();
		}
		if (s.length() == 1) {
			return PrimitiveType.getFromSig(s).classType;
		}
		if (s.charAt(0) == 'L')
			s = s.substring(1);
		if (s.charAt(s.length()-1) == ';')
			s = s.substring(0, s.length()-1);
		s = s.replaceAll("/", ".");
		try {
			return Class.forName(s);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
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
		ArrayList<String> li = parseMethodDesc(mn.desc);
		//ReikaJavaLibrary.pConsole("PARSED METHOD: "+mn.desc+" > "+li);
		return li;
	}

	private static ArrayList<String> parseMethodDesc(String desc) {
		String s = desc.substring(desc.indexOf('(')+1, desc.lastIndexOf(')')); //strip to inside brackets
		ArrayList<String> li = new ArrayList();
		parseArguments(li, s);
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

	public static boolean memberHasAnnotationOfType(ClassNode cn, String type) {
		return hasAnnotation(cn.visibleAnnotations, type);
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
		m.instructions.clear();
		ArrayList<String> li = parseMethodSignature(m);
		PrimitiveType type = PrimitiveType.getFromSig(li.get(li.size()-1));
		AbstractInsnNode retcall = null;
		AbstractInsnNode retobj = null;
		switch(type) {
			case LONG:
				retcall = new InsnNode(Opcodes.LRETURN);
				retobj = new InsnNode(Opcodes.LCONST_0);
				break;
			case DOUBLE:
				retcall = new InsnNode(Opcodes.DRETURN);
				retobj = new InsnNode(Opcodes.DCONST_0);
				break;
			case FLOAT:
				retcall = new InsnNode(Opcodes.FRETURN);
				retobj = new InsnNode(Opcodes.FCONST_0);
				break;
			case INT:
			case BYTE:
			case SHORT:
			case BOOLEAN:
				retcall = new InsnNode(Opcodes.IRETURN);
				retobj = new InsnNode(Opcodes.ICONST_0);
				break;
			case FLOATARRAY:
			case INTARRAY:
			case BOOLARRAY:
			case SHORTARRAY:
			case DOUBARRAY:
			case BYTEARRAY:
			case OBJECT:
				retcall = new InsnNode(Opcodes.ARETURN);
				retobj = new InsnNode(Opcodes.ACONST_NULL);
				break;
			case VOID:
				retcall = new InsnNode(Opcodes.RETURN);
				break;
		}
		if (retobj != null)
			m.instructions.add(retobj);
		if (retcall != null)
			m.instructions.add(retcall);
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

	public static AbstractInsnNode getNthInsn(int n, InsnList li, int opcode, Object... args) {
		int count = 0;
		for (int i = 0; i < li.size(); i++) {
			AbstractInsnNode ain = li.get(i);
			if (match(ain, opcode, args)) {
				count++;
				if (count == n)
					return ain;
			}
		}
		return null;
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

	public static AbstractInsnNode getLastJumpBefore(InsnList li, int index) {
		AbstractInsnNode ain = li.get(index-1);
		while (!(ain instanceof JumpInsnNode) && index > 0) {
			index--;
			ain = li.get(index);
		}
		return ain instanceof JumpInsnNode ? ain : null;
	}

	public static FieldInsnNode getLastFieldRefBefore(InsnList li, int index, String name) {
		AbstractInsnNode ain = li.get(index-1);
		while ((!(ain instanceof FieldInsnNode) || !((FieldInsnNode)ain).name.equals(name)) && index > 0) {
			index--;
			ain = li.get(index);
		}
		return ain instanceof FieldInsnNode && ((FieldInsnNode)ain).name.equals(name) ? (FieldInsnNode)ain : null;
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

	public static MethodInsnNode getFirstMethodCallByName(ClassNode cn, MethodNode m, String name) {
		return getNthMethodCallByName(cn, m, name, 1);
	}

	public static MethodInsnNode getNthMethodCallByName(ClassNode cn, MethodNode m, String name, int n) {
		int counter = 0;
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain instanceof MethodInsnNode) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (min.name.equals(name)) {
					counter++;
					if (counter >= n)
						return min;
				}
			}
		}
		throw new ASMException.NoSuchASMMethodInstructionException(cn, m, "[unspecified]", name, "[unspecified]", n > 1 ? n : -1);
	}

	public static FieldInsnNode getFirstFieldCallByName(ClassNode cn, MethodNode m, String name) {
		return getNthFieldCallByName(cn, m, name, 1);
	}

	public static FieldInsnNode getNthFieldCallByName(ClassNode cn, MethodNode m, String name, int n) {
		int counter = 0;
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain instanceof FieldInsnNode) {
				FieldInsnNode min = (FieldInsnNode)ain;
				if (min.name.equals(name)) {
					counter++;
					if (counter >= n)
						return min;
				}
			}
		}
		throw new ASMException.NoSuchASMFieldInstructionException(cn, m, "[unspecified]", name, n > 1 ? n : -1);
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

	public static AbstractInsnNode getNthOpcodeAfter(InsnList li, int n, int idx, int opcode) {
		int c = 0;
		for (int i = idx; i < li.size(); i++) {
			AbstractInsnNode ain = li.get(i);
			if (ain.getOpcode() == opcode) {
				c++;
				if (c == n)
					return ain;
			}
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
	public static AbstractInsnNode copyInstruction(AbstractInsnNode ain) {
		return copyInstruction(ain, new HashMap());
	}

	/** Currently broken */
	public static AbstractInsnNode copyInstruction(AbstractInsnNode ain, Map<LabelNode, LabelNode> labels) {
		return ain.clone(labels);
	}

	public static ArrayList<String> parseMethodSignature(MethodNode min) {
		return parseMethodSignature(min.desc);
	}

	public static ArrayList<String> parseMethodSignature(String sig) {
		int idx = sig.lastIndexOf(')');
		String ret = sig.substring(idx+1);
		ArrayList<String> li = parseMethodDesc(sig);
		li.add(ret);
		return li;
	}

	public static ArrayList<String> parseMethodSignature(MethodInsnNode min) {
		int idx = min.desc.lastIndexOf(')');
		String ret = min.desc.substring(idx+1);
		ArrayList<String> li = parseMethodDesc(min.desc);
		li.add(ret);
		return li;
	}

	public static String compileSignature(ArrayList<String> argsAndRet) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < argsAndRet.size()-1; i++) {
			sb.append(argsAndRet.get(i));
		}
		sb.append(")");
		sb.append(argsAndRet.get(argsAndRet.size()-1));
		return sb.toString();
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

	private static enum PrimitiveType {

		VOID("V",			void.class, 		Opcodes.RETURN,		Opcodes.ACONST_NULL, 	Opcodes.ASTORE),
		INT("I",			int.class, 			Opcodes.IRETURN, 	Opcodes.ILOAD, 			Opcodes.ISTORE),
		BOOLEAN("Z",		boolean.class, 		Opcodes.IRETURN,	Opcodes.ILOAD, 			Opcodes.ISTORE),
		BYTE("B",			byte.class,			Opcodes.IRETURN, 	Opcodes.ILOAD, 			Opcodes.ISTORE),
		LONG("L",			long.class,			Opcodes.LRETURN, 	Opcodes.LLOAD, 			Opcodes.LSTORE),
		SHORT("S",			short.class, 		Opcodes.IRETURN, 	Opcodes.ILOAD, 			Opcodes.ISTORE),
		FLOAT("F",			float.class, 		Opcodes.FRETURN, 	Opcodes.FLOAD, 			Opcodes.FSTORE),
		DOUBLE("D",			double.class, 		Opcodes.DRETURN, 	Opcodes.DLOAD, 			Opcodes.DSTORE),
		INTARRAY("[I",		int[].class, 		Opcodes.ARETURN, 	Opcodes.ALOAD, 			Opcodes.ASTORE),
		BYTEARRAY("[B",		byte[].class, 		Opcodes.ARETURN, 	Opcodes.ALOAD, 			Opcodes.ASTORE),
		SHORTARRAY("[S",	short[].class, 		Opcodes.ARETURN, 	Opcodes.ALOAD, 			Opcodes.ASTORE),
		DOUBARRAY("[D",		double[].class, 	Opcodes.ARETURN, 	Opcodes.ALOAD, 			Opcodes.ASTORE),
		BOOLARRAY("[Z",		boolean[].class,	Opcodes.ARETURN, 	Opcodes.ALOAD, 			Opcodes.ASTORE),
		FLOATARRAY("[F",	float[].class,		Opcodes.ARETURN, 	Opcodes.ALOAD, 			Opcodes.ASTORE),
		OBJECT("L*;",		Object.class,		Opcodes.ARETURN, 	Opcodes.ALOAD, 			Opcodes.ASTORE);

		private final String id;
		private final Class classType;
		private final int returnCode;
		private final int loadCode;
		private final int storeCode;

		private static final HashMap<String, PrimitiveType> map = new HashMap();

		private PrimitiveType(String s, Class c, int retcode, int loadcode, int storecode) {
			id = s;
			classType = c;
			returnCode = retcode;
			loadCode = loadcode;
			storeCode = storecode;
		}

		private static PrimitiveType getFromSig(String id) {
			return map.containsKey(id) ? map.get(id) : OBJECT;
		}

		static {
			for (int i = 0; i < values().length; i++) {
				PrimitiveType type = values()[i];
				map.put(type.id, type);
			}
		}

	}

	public static void throwConflict(String patcher, ClassNode cn, MethodNode m, String msg) {
		throwConflict(patcher, cn, m, msg, null);
	}

	public static void throwConflict(String patcher, ClassNode cn, MethodNode m, String msg, Throwable t) {
		ASMConflictException ex = new ASMConflictException(activeMod, cn, m, patcher, msg);
		if (t != null)
			ex.initCause(t);

		printClassToFile(cn, getMinecraftDirectoryString()+"/ClassError");

		throw ex;
	}

	private static String getMinecraftDirectoryString() {
		String s = ((File)FMLInjectionData.data()[6]).getAbsolutePath();
		if (s.endsWith("/.") || s.endsWith("\\.")) {
			s = s.substring(0, s.length()-2);
		}
		s = s.replaceAll("\\\\", "/");
		return s;
	}

	public static void printClassToFile(ClassNode cn, String path) {
		if (!path.endsWith("/"))
			path = path+"/";
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS/* | ClassWriter.COMPUTE_FRAMES*/);
		cn.accept(writer);
		byte[] newdata = writer.toByteArray();
		try {
			File f = new File(path+cn.name+".class");
			f.getParentFile().mkdirs();
			f.createNewFile();
			FileOutputStream out = new FileOutputStream(f);
			out.write(newdata);
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean checkForClass(String s) {
		try {
			return Launch.classLoader.getClassBytes(s) != null;
		}
		catch (IOException e) {
			return false;
		}
	}

	public static boolean checkIfClassInheritsMethod(ClassNode cn, MethodNode mn) {
		Class c;
		try {
			String s = cn.superName.replaceAll("/", ".");
			c = Class.forName(s);
			while (c != null) {
				if (classContainsMethod(c, mn))
					return true;
				c = c.getSuperclass();
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static AnnotationNode copyAnnotation(AnnotationNode a) {
		AnnotationNode cp = new AnnotationNode(a.desc);
		cp.values.addAll(a.values);
		return cp;
	}

	public static void changeFirstNumericalArgument(InsnList li, int num, int repl) {
		int look = getOpcodeForNum(num);
		AbstractInsnNode put = getInsnForNum(repl);
		if (put != null) {
			for (int i = 0; i < li.size(); i++) {
				AbstractInsnNode ain = li.get(i);
				if (ain.getOpcode() == look) {
					if (matchNumberNode(ain, num)) {
						li.insert(ain, put);
						li.remove(ain);
					}
				}
			}
		}
	}

	public static boolean matchNumberNode(AbstractInsnNode ain, int val) {
		switch(ain.getOpcode()) {
			case Opcodes.ICONST_0:
			case Opcodes.ICONST_1:
			case Opcodes.ICONST_2:
			case Opcodes.ICONST_3:
			case Opcodes.ICONST_4:
			case Opcodes.ICONST_5:
				return true;
			case Opcodes.BIPUSH:
			case Opcodes.SIPUSH:
				return ((IntInsnNode)ain).operand == val;
			case Opcodes.LDC:
				return ((LdcInsnNode)ain).cst.equals(val);
		}
		return false;
	}

	public static AbstractInsnNode getInsnForNum(int val) {
		int set = getOpcodeForNum(val);
		switch(set) {
			case Opcodes.ICONST_0:
			case Opcodes.ICONST_1:
			case Opcodes.ICONST_2:
			case Opcodes.ICONST_3:
			case Opcodes.ICONST_4:
			case Opcodes.ICONST_5:
				return new InsnNode(set);
			case Opcodes.BIPUSH:
			case Opcodes.SIPUSH:
				return new IntInsnNode(set, val);
			case Opcodes.LDC:
				return new LdcInsnNode(val);
		}
		return null;
	}

	private static int getOpcodeForNum(int val) {
		if (val == 0) {
			return Opcodes.ICONST_0;
		}
		else if (val == 1) {
			return Opcodes.ICONST_1;
		}
		else if (val == 2) {
			return Opcodes.ICONST_2;
		}
		else if (val == 3) {
			return Opcodes.ICONST_3;
		}
		else if (val == 4) {
			return Opcodes.ICONST_4;
		}
		else if (val == 5) {
			return Opcodes.ICONST_5;
		}
		else if (val <= 255) {
			return Opcodes.BIPUSH; //byte int push
		}
		else if (val <= 32767) {
			return Opcodes.SIPUSH; //short int push
		}
		else {
			return Opcodes.LDC;
		}
	}

	public static MethodInsnNode getCallerInsn(String owner, MethodNode mn) {
		int opcode = Opcodes.INVOKEVIRTUAL;
		if ((mn.access & Modifier.PRIVATE) != 0)
			opcode = Opcodes.INVOKESPECIAL;
		if (mn.name.equals("<init>"))
			opcode = Opcodes.INVOKESPECIAL;
		if ((mn.access & Modifier.STATIC) != 0)
			opcode = Opcodes.INVOKESTATIC;
		if ((mn.access & Modifier.INTERFACE) != 0)
			opcode = Opcodes.INVOKEINTERFACE;
		return new MethodInsnNode(opcode, owner, mn.name, mn.desc, opcode == Opcodes.INVOKEINTERFACE);
	}

	public static int getOpcodeForMethodReturn(MethodInsnNode m) {
		ArrayList<String> li = parseMethodSignature(m);
		String type = li.get(li.size()-1);
		PrimitiveType p = PrimitiveType.getFromSig(type);
		if (p != null) {
			return p.returnCode;
		}
		return Opcodes.ARETURN;
	}

	public static InsnList getIntReturnInsnList(int val) {
		InsnList li = new InsnList();
		li.add(getInsnForNum(val));
		li.add(new InsnNode(Opcodes.IRETURN));
		return li;
	}

	public static LabelNode getFirstLabelAfter(InsnList li, int idx) {
		for (int i = idx; i < li.size(); i++) {
			AbstractInsnNode ain = li.get(i);
			if (ain instanceof LabelNode) {
				return (LabelNode)ain;
			}
		}
		return null;
	}

	public static Side getSide() {
		return FMLLaunchHandler.side();
	}

	public static void writeClassFile(ClassNode cn, String path) {
		if (FMLForgePlugin.RUNTIME_DEOBF) {
			cn = copyClassNode(cn);
			deobfClassFile(cn);
		}

		ClassWriter writer = new ClassWriter(0);
		cn.accept(writer);
		byte[] data = writer.toByteArray();

		try {
			File f = new File(path+"/"+cn.name+".class");
			f.getParentFile().mkdirs();
			f.createNewFile();
			FileOutputStream out = new FileOutputStream(f);
			out.write(data);
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ClassNode copyClassNode(ClassNode cn) {
		ClassWriter writer = new ClassWriter(0);
		cn.accept(writer);
		byte[] data = writer.toByteArray();

		ClassNode cn2 = new ClassNode();
		ClassReader classReader = new ClassReader(data);
		classReader.accept(cn2, 0);

		return cn2;
	}

	public static void deobfClassFile(ClassNode cn) {
		if (srgMap.isEmpty()) {
			loadSRGs();
		}
		for (FieldNode f : cn.fields) {
			deobfField(f);
		}
		for (MethodNode m : cn.methods) {
			deobfMethod(m);
		}
	}

	private static void deobfField(FieldNode f) {
		String repl = srgMap.get(f.name);
		if (repl != null)
			f.name = repl;
	}

	private static void deobfMethod(MethodNode m) {
		String repl = srgMap.get(m.name);
		if (repl != null)
			m.name = repl;
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain instanceof FieldInsnNode) {
				FieldInsnNode fin = (FieldInsnNode)ain;
				repl = srgMap.get(fin.name);
				if (repl != null)
					fin.name = repl;
			}
			else if (ain instanceof MethodInsnNode) {
				MethodInsnNode min = (MethodInsnNode)ain;
				repl = srgMap.get(min.name);
				if (repl != null)
					min.name = repl;
			}
		}
	}

	private static void loadSRGs() {
		File f = new File(REIKA_SRGS+"mcp-srg.srg");
		ArrayList<String> li = ReikaFileReader.getFileAsLines(f, true);
		for (String s : li) {
			if (!s.startsWith("CL")) {
				String[] parts = s.split(" ");

				int deobfidx = s.startsWith("MD") ? 1 : 1;
				int obfidx = s.startsWith("MD") ? 3 : 2;

				String deobf = parts[deobfidx];
				String obf = parts[obfidx];

				if (!deobf.equals(obf)) {
					deobf = deobf.substring(deobf.lastIndexOf('/')+1);
					obf = obf.substring(obf.lastIndexOf('/')+1);
					srgMap.put(obf, deobf);
				}
			}
		}
	}

	public static String addLeadingArgument(String sig, String arg) {
		ArrayList<String> li = parseMethodSignature(sig);
		li.add(0, arg);
		return compileSignature(li);
	}

	public static String addTrailingArgument(String sig, String arg) {
		ArrayList<String> li = parseMethodSignature(sig);
		li.add(li.size()-1, arg);
		return compileSignature(li);
	}

	public static void addLeadingArgument(MethodInsnNode min, String arg) {
		min.desc = addLeadingArgument(min.desc, arg);
	}

	public static void addTrailingArgument(MethodInsnNode min, String arg) {
		min.desc = addTrailingArgument(min.desc, arg);
	}

	public static void replaceInstruction(InsnList li, AbstractInsnNode tgt, AbstractInsnNode repl) {
		li.insertBefore(tgt, repl);
		li.remove(tgt);
	}

	public static void clearClass(ClassNode cn) {
		Collection<MethodNode> c = new ArrayList(cn.methods);
		for (MethodNode m : cn.methods) {
			clearMethodBody(m);
			cn.methods.remove(m);
			addMethod(cn, copyInsnList(m.instructions), m.name, m.desc, m.access);
		}
	}

	public static void exposeMethod(ClassNode cn, String name, String sig) {
		MethodNode m = getMethodByName(cn, name, sig);
		m.access = m.access & ~Modifier.PRIVATE;
		m.access = m.access & ~Modifier.PROTECTED;
		m.access = m.access | Modifier.PUBLIC;
	}

}

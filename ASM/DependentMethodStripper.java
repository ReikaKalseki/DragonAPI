/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.ASMException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class DependentMethodStripper implements IClassTransformer {

	private static final String baseString = "LReika/DragonAPI/ASM/DependentMethodStripper$";
	private static final boolean DEBUG = true;

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		Iterator<FieldNode> fields = classNode.fields.iterator();
		while(fields.hasNext()) {
			FieldNode field = fields.next();
			AnnotationFail a = this.remove(classNode, field);
			if (a != null) {
				if (DEBUG) {
					ReikaJavaLibrary.pConsole(String.format("DRAGONAPI ASM: Removing Field: '%s.%s'; Reason: %s", classNode.name, field.name, a.text));
				}
				fields.remove();
			}
		}
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext()) {
			MethodNode method = methods.next();
			AnnotationFail a = this.remove(classNode, method);
			if (a != null) {
				if (DEBUG) {
					ReikaJavaLibrary.pConsole(String.format("DRAGONAPI ASM: Removing Method: '%s.%s%s'; Reason: %s", classNode.name, method.name, method.desc, a.text));
				}
				methods.remove();
			}
		}
		Iterator<InnerClassNode> classes = classNode.innerClasses.iterator();
		while(classes.hasNext()) {
			InnerClassNode method = classes.next();
			/*
			AnnotationFail a = this.remove(classNode, method);
			if (a != null) {
				if (DEBUG) {
					ReikaJavaLibrary.pConsole(String.format("DRAGONAPI ASM: Removing Method: %s.%s%s; Reason: %s", classNode.name, method.name, method.desc, a.text));
				}
				methods.remove();
			}
			 */
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		classNode.check(classNode.version);
		return writer.toByteArray();
	}

	private AnnotationFail remove(ClassNode cn, FieldNode f) {
		return processSmart(f) ? smartFail : this.remove(cn, f.visibleAnnotations);
	}

	private AnnotationFail remove(ClassNode cn, MethodNode f) {
		return processSmart(f) ? smartFail : this.remove(cn, f.visibleAnnotations);
	}

	private AnnotationFail remove(ClassNode cn, List<AnnotationNode> anns) {
		if (anns == null) {
			return null;
		}
		if (!FMLForgePlugin.RUNTIME_DEOBF) //prevents needing to always reload game in dev env (ASM not run on src edit, so Eclipse thinks new methods)
			return null;
		for (AnnotationNode ann : anns) {
			if (isDependencyAnnotation(ann)) {
				if (ann.values != null) {
					Annotations a = Annotations.getType(ann);
					if (a != null) {
						for (int x = 0; x < ann.values.size() - 1; x += 2) {
							Object key = ann.values.get(x);
							Object values = ann.values.get(x+1);
							if (key instanceof String && key.equals("value")) {
								if (values instanceof String[]) { //Enum
									String[] value = (String[])values;
									if (a.remove(value[1])) {
										return new AnnotationFail(a, value[1]);
									}
								}
								else if (values instanceof String) { //Normal string arg
									String sg = (String)values;
									if (a.remove(sg)) {
										return new AnnotationFail(a, sg);
									}
								}
							}
						}
					}
					else {
						throw new InvalidStrippingAnnotationException(cn, ann);
					}
				}
			}
		}
		return null;
	}

	private static class InvalidStrippingAnnotationException extends ASMException {

		private final AnnotationNode annotation;

		public InvalidStrippingAnnotationException(ClassNode cn, AnnotationNode ann) {
			super(cn);
			annotation = ann;
		}

		@Override
		public final String getMessage() {
			StringBuilder sb = new StringBuilder();
			sb.append(super.getMessage());
			sb.append("Annotation type "+annotation.desc+" is not valid dependency annotation!");
			return sb.toString();
		}

	}

	private static class AnnotationFail {

		private final Annotations annotation;
		private final String reference;
		private final String text;

		private AnnotationFail(Annotations a, String ref) {
			annotation = a;
			reference = ref;
			text = String.format(a.desc, ref);
		}
	}

	private static final AnnotationFail smartFail = new AnnotationFail(Annotations.SMART, "");

	private static boolean isDependencyAnnotation(AnnotationNode ann) {
		return ann.desc.startsWith(baseString);
	}

	private static boolean processSmart(MethodNode mn) {
		if (!ReikaASMHelper.memberHasAnnotationOfType(mn, "LReika/DragonAPI/ASM/DependentMethodStripper$SmartStrip"))
			return false;
		ArrayList<String> args = ReikaASMHelper.parseMethodArguments(mn);
		for (String s : args) {
			if (s.length() > 1 && !ReikaASMHelper.checkForClass(s.substring(1, s.length()-1))) //> 1 to avoid primitives, substring to drop L- and -;
				return true;
		}
		return false;
	}

	private static boolean processSmart(FieldNode fn) {
		return ReikaASMHelper.memberHasAnnotationOfType(fn, "LReika/DragonAPI/ASM/DependentMethodStripper$SmartStrip") && !ReikaASMHelper.checkForClass(fn.desc);
	}

	private static enum Annotations {
		MOD("ModDependent", "Required mod %s not loaded."),
		CLASS("ClassDependent", "Required class %s not found."),
		SMART("SmartStrip", "Refers to one or more classes not found.");

		private final String name;
		private final String desc;

		private static final HashMap<String, Annotations> map = new HashMap();

		private Annotations(String s, String d) {
			name = s;
			desc = d;
		}

		private boolean remove(String value) {
			switch(this) {
				case CLASS:
					return !ReikaASMHelper.checkForClass(value);
				case MOD:
					ModList mod = ModList.valueOf(value);
					return !mod.isLoaded();
				default:
					return false;
			}
		}

		private static Annotations getType(AnnotationNode ann) {
			String d = ann.desc.substring(0, ann.desc.length()-1); //remove ';'
			return map.get(d.substring(baseString.length()));
		}

		static {
			for (Annotations a : values()) {
				map.put(a.name, a);
			}
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.FIELD})
	public static @interface ModDependent {
		ModList value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.FIELD})
	public static @interface ClassDependent {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.FIELD})
	public static @interface SmartStrip {

	}
}

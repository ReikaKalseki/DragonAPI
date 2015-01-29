/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.ASMException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class DependentMethodStripper implements IClassTransformer
{
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
			if (this.remove(field)) {
				if (DEBUG) {
					ReikaJavaLibrary.pConsole(String.format("Removing Field: %s.%s", classNode.name, field.name));
				}
				fields.remove();
			}
		}
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext()) {
			MethodNode method = methods.next();
			if (this.remove(method)) {
				if (DEBUG) {
					ReikaJavaLibrary.pConsole(String.format("Removing Method: %s.%s%s", classNode.name, method.name, method.desc));
				}
				methods.remove();
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		classNode.check(classNode.version);
		return writer.toByteArray();
	}

	private boolean remove(FieldNode f) {
		return processSmart(f) || this.remove(f.visibleAnnotations);
	}

	private boolean remove(MethodNode f) {
		return processSmart(f) || this.remove(f.visibleAnnotations);
	}

	private boolean remove(List<AnnotationNode> anns) {
		if (anns == null) {
			return false;
		}
		if (!FMLForgePlugin.RUNTIME_DEOBF) //prevents needing to always reload game in dev env
			return false;
		for (AnnotationNode ann : anns) {
			if (isDependencyAnnotation(ann)) {
				if (ann.values != null) {
					Annotations a = Annotations.getType(ann);
					if (a != null) {
						for (int x = 0; x < ann.values.size() - 1; x += 2) {
							Object key = ann.values.get(x);
							Object values = ann.values.get(x+1);
							if (key instanceof String && key.equals("value")) {
								if (values instanceof String[]) {
									String[] value = (String[])values;
									//ReikaJavaLibrary.pConsole(mod+": "+Arrays.toString(value));
									if (a.remove(value[1])) {
										return true;
									}
								}
							}
						}
					}
					else {
						throw new InvalidStrippingAnnotationException("Annotation type "+ann.desc+" is not valid!");
					}
				}
			}
		}
		return false;
	}

	private static class InvalidStrippingAnnotationException extends ASMException {

		public InvalidStrippingAnnotationException(String s) {
			super(s);
		}

	}

	private static final String baseString = "LReika/DragonAPI/ASM/DependentMethodStripper$";

	private static boolean isDependencyAnnotation(AnnotationNode ann) {
		//ann.desc.equals("LReika/DragonAPI/ASM/DependentMethodStripper$ModDependent;");
		return ann.desc.startsWith(baseString);
	}

	private static boolean processSmart(MethodNode mn) {

	}

	private static boolean processSmart(FieldNode mn) {

	}

	private static enum Annotations {
		MOD("ModDependent"),
		CLASS("ClassDependent"),
		SMART("SmartStripper");

		private final String name;

		private static final HashMap<String, Annotations> map = new HashMap();

		private Annotations(String s) {
			name = s;
		}

		private boolean remove(String value) {
			switch(this) {
			case CLASS:
				return classExists(value);
			case MOD:
				ModList mod = ModList.valueOf(value);
				return !mod.isLoaded();
			default:
				return false;
			}
		}

		private static boolean classExists(String name) {
			try {
				return Launch.classLoader.getClassBytes(name) != null;
			}
			catch (IOException e) {
				return false;
			}
		}

		private static Annotations getType(AnnotationNode ann) {
			String d = ann.desc;
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
	public static @interface SmartStripper {

	}
}
